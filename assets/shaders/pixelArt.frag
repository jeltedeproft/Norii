#version 130
#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

#define SCANLINES
#define SHADOW_MASK
#define SCREEN_CURVE_RADIUS		5.0
#define SCREEN_CORNER_RADIUS	0.1
#define BRIGHTNESS      		1.5
#define PIXEL_SHARPNESS   		2.0
#define LINE_SHARPNESS			6.0
#define MASK_STRENGTH			0.15
#define IRES (vec2(16, 9)*5.0)
#define TILES 2.0


#define gaussian(a,b)	exp2((a)*(a)*-(b))

out vec2 v_texCoords;
out vec4 color;
uniform sampler2D u_texture;
uniform float u_amount;
uniform float u_speed;
uniform float u_time;
uniform vec2 u_resolution;


vec2 curveScreen( vec2 uv ) {
    float r = 3.14159265*0.5/SCREEN_CURVE_RADIUS;
    float d = 1.0-cos(uv.x*r)*cos(uv.y*r);		//distance to screen
    float s = cos(r);							//scale factor to re-fit window
    return uv / (1.0-d) * s;
}


float discardCorners( vec2 pos ) {
    pos = abs(pos);
    pos.x = pos.x*1.333-0.333;											// 4:3 aspect ratio correction
    if( min(pos.x, pos.y) < 1.0-SCREEN_CORNER_RADIUS ) return 1.0;		// not near corner -- break early
    float d = distance( pos, vec2(1.0-SCREEN_CORNER_RADIUS) );
    return float( d<SCREEN_CORNER_RADIUS );
}


vec3 getSample( vec2 pos, vec2 off ) {
	//get nearest emulated sample
	vec2 ir = IRES * TILES;
    pos = floor(pos*ir) + vec2(0.5) + off;
	vec3 col = vec3(0.0);
	if ( pos.x>=0.0 && pos.x<=ir.x*2.0 && pos.y>=0.0 && pos.y<=ir.y ) {
        col = texelFetch(u_texture, ivec2(pos), 0).rgb;
        col = pow( ( (col + 0.055) / 1.055), vec3(2.4) );		// SRGB => linear
	}
	return col;
}


vec3 getScanline( vec2 pos, float off ) {
	// 3-tap gaussian filter to get colour at arbitrary point along scanline
    vec2 ir = IRES * TILES;
    float d = 0.5-fract(pos.x*ir.x);
	vec3 ca = getSample( pos, vec2(-1.0, off ) );
	vec3 cb = getSample( pos, vec2( 0.0, off ) );
	vec3 cc = getSample( pos, vec2( 1.0, off ) );
	float wa = gaussian( d-1.0, PIXEL_SHARPNESS );
	float wb = gaussian( d,     PIXEL_SHARPNESS );
	float wc = gaussian( d+1.0, PIXEL_SHARPNESS );
	return ( ca*wa + cb*wb + cc*wc ) / ( wa+wb+wc);
}


vec3 getScreenColour( vec2 pos ) {
	//Get influence of 3 nearest scanlines
    vec2 ir = IRES * TILES;
    float d = 0.5-fract(pos.y*ir.y);
	vec3 ca = getScanline( pos,-1.0 );
	vec3 cb = getScanline( pos, 0.0 );
	vec3 cc = getScanline( pos, 1.0 );
    float wa = gaussian( d-1.0, LINE_SHARPNESS );
	float wb = gaussian( d,     LINE_SHARPNESS );
	float wc = gaussian( d+1.0, LINE_SHARPNESS );
    return ( ca*wa + cb*wb + cc*wc );
}


vec3 SlotMask_PixelPerfect( vec2 pos ) {
    //pos /= 1.0 + floor( u_resolution.y / 1440.0 );
    pos /= 1.0 + floor( u_resolution.y / (IRES.y*15.0) );
    float glow = 0.5;
    float f = mod(pos.x,3.0);
    vec3 col = vec3( (f<=1.0), (f>1.0&&f<=2.0), (f>2.0) );
    col += vec3( (f<1.5 || f>=2.5), (f>0.5 && f<=2.5), (f>1.5 || f<=0.5) ) * glow;
    col *= ( mod(pos.y+(fract(pos.x/6.0)>0.5?1.5:0.0),3.0)<1.0 ) ? glow : 1.0;
    col /= 1.0+glow;
    return col;
}


vec3 ACESFilm( vec3 x ) {
    return clamp((x*(2.51*x + 0.03)) / (x*(2.43*x + 0.59) + 0.14), 0.0, 1.0);
}


void main() {
    vec2 pos = v_texCoords;
   	pos = pos*2.0 - 1.0;
    pos.x *= u_resolution.x/u_resolution.y*(IRES.y/IRES.x);						// 4:3 aspect
    
    #ifdef SCREEN_SHAPE
    pos = curveScreen(pos);											// curve screen
    #endif
    
    if(max( abs(pos.x), abs(pos.y) )<1.0) {							// skip everything if we're beyond the screen edge
    	
        vec3 col = vec3(1.0);
        
        #ifdef SCREEN_SHAPE
        col *= discardCorners(pos);
        #endif
        
        #ifdef LIGHT_EFFECTS
        col *= 1.0 - sqrt(length(pos)*0.25);						// vignette
        #endif
        
        pos = pos*0.5 + 0.5;

        #ifdef SCANLINES
		col *= getScreenColour( pos );
        #else
        col *= getSample( pos, vec2(0.0) );
        #endif
        
        #ifdef SHADOW_MASK
        vec3 shadowmask = SlotMask_PixelPerfect( v_texCoords );
        col *= mix( vec3(1.0-MASK_STRENGTH), vec3(1.0+MASK_STRENGTH), shadowmask);
        #endif
        
        #ifdef LIGHT_EFFECTS
		col *= BRIGHTNESS;
    	col = ACESFilm(col);
        #endif
    
    	col = pow( col, vec3(1.0/2.4) ) * 1.055 - 0.055;			// linear => SRGB
    
    	color = vec4( col, 1.0 );
    }
    
    //fragColor = texelFetch( iChannel0, ivec2(fragCoord), 0);
}