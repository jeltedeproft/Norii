#define STEPS 250.0
#define MDIST 100.0
#define pi 3.1415926535
#define rot(a) mat2(cos(a),sin(a),-sin(a),cos(a))
#define sat(a) clamp(a,0.0,1.0)

//Comment to remove triangle wobble
#define WOBBLE 

//ADJUST AA HERE
#define AA 1.0

//Camera Control
//#define CAM

//based on ideas from 
//https://www.shadertoy.com/view/fsVSzw
//https://www.shadertoy.com/view/MscSDB
//https://www.shadertoy.com/view/3ddGzn
#define h13(n) fract((n)*vec3(12.9898,78.233,45.6114)*43758.5453123)
vec2 vor(vec2 v, vec3 p, vec3 s){
    p = abs(fract(p-s)-0.5);
    float a = max(p.x,max(p.y,p.z));
    float b = min(v.x,a);
    float c = max(v.x,min(v.y,a));
    return vec2(b,c);
}

float vorMap(vec3 p){
    vec2 v = vec2(5.0);
    v = vor(v,p,h13(0.96));
    p.xy*=rot(1.2);
    v = vor(v,p,h13(0.55));
    p.yz*=rot(2.);
    v = vor(v,p,h13(0.718));
    p.zx*=rot(2.7);
    v = vor(v,p,h13(0.3));
    return v.y-v.x; 
}

//box sdf
float box(vec3 p, vec3 b){
  vec3 q = abs(p)-b;
  return length(max(q,0.0))+min(max(q.x,max(q.y,q.z)),0.0);
}


float va = 0.; //voronoi animations
float sa = 0.; //size change animation
float rlg; //global ray length
bool hitonce = false; //for tracking complications with the voronoi 


//I put quite a lot of effort into making the normals inside the voronoi correct but 
//in the end the normals are only partially correct and I barely used them, however
//the code is still messy from my failed attempt :)
vec2 map(vec3 p, vec3 n){
    vec2 a = vec2(1);
    vec2 b = vec2(2);
    vec3 po = p;
    vec3 no = n;
    p-=n;
    float len = 9.5;
    len+=sa;
    float len2 = len-1.0;
    p.x-=(len/2.0);
    a.x = box(p,vec3(1,1,len));
    a.x = min(a.x,box(p-vec3(0,len2,len2),vec3(1,len,1)));
    a.x = min(a.x,box(p-vec3(-len2,0,-len2),vec3(len,1,1)));
    float tip = box(p-vec3(len2,len2*2.0,len2),vec3(len2,1,1));   
    float cut = (p.xz*=rot(pi/4.0-0.15)).y;
    tip = max(-cut+len2/2.0,tip);
    a.x = min(a.x,tip);
    b.x = tip;
    a.x-=0.4;
    p = po;
    p.xz*=rot(pi/4.0);
    p.xy*=rot(-0.9553155);
    po = p;
    n.xz*=rot(pi/4.0);
    n.xy*=rot(-0.9553155);
    p.xz-=n.xy;
    p.xz*=rot(-iTime*0.3);
    if(hitonce)a.x = max(a.x,-vorMap(vec3(p.x,p.z,rlg+n.z)*0.35+3.)+va*1.6);
    p = po;
    b.y = 3.0;
    p-=n;
    p.xz*=rot(pi/6.0);
    p.x+=1.75;
    p.z+=0.4;
    po = p;
    for(float i = 0.; i<3.; i++){ //blocks
        b.y+=i;
        p.xz*=rot((2.0*pi/3.0)*i);
        float t = (iTime+i*((2.0*pi)/9.0))*3.;
        p.y-=35.-50.*step(sin(t),0.);
        p.x+=4.5;
        p.xy*=rot(t);
        p.x-=4.5;
        p.xz*=rot(t);
        b.x = box(p,vec3(1.5,.5,.5))-0.25;
        a = (a.x<b.x)?a:b;
        p = po;
    }
    return a;
}
vec3 norm(vec3 p){
    vec2 e= vec2(0.0001,0);
    return normalize(map(p,vec3(0)).x-vec3(
    map(p,e.xyy).x,
    map(p,e.yxy).x,
    map(p,e.yyx).x));
}

void render(out vec4 fragColor, in vec2 fragCoord){
    vec2 uv = (fragCoord-0.5*iResolution.xy)/iResolution.y;
    vec3 col = vec3(0);
    uv.x-=0.025;
    vec2 uv2 = uv;
    vec2 uv3 = uv;
    
    //Calculating the animation for the size wobble and voronoi crumble
    uv2.y-=0.1;
    uv2*=rot(iTime*1.25);
    float ang =atan(uv2.x,uv2.y)/(pi*2.)+0.5;
    float range = 0.175;
    #ifdef WOBBLE
    sa = sin(ang*10.+iTime*2.5)*0.3;
    #endif
    ang = smoothstep(0.0,range,ang)*smoothstep(0.0,range,1.0-ang);
    va = (1.0-ang)*0.175;
    uv*=rot(-pi/6.0);
    
    vec3 ro = vec3(5,5,5)*6.5;
    
    #ifdef CAM
    if(iMouse.z>0.){
        ro.yz*=rot(2.0*(iMouse.y/iResolution.y-0.5));
        ro.zx*=rot(-7.0*(iMouse.x/iResolution.x-0.5));
    }    
    #endif
    
    //maybe there is an easier way to make an orthographic target camera
    //but this is what I figured out
    vec3 lk = vec3(0,0,0);
    vec3 f = normalize(lk-ro);
    vec3 r = normalize(cross(vec3(0,1,0),f));
    vec3 rd = f+uv.x*r+uv.y*cross(f,r);
    ro+=(rd-f)*17.0;
    rd=f;

    vec3 p = ro;
    float rl = 0.;
    vec2 d= vec2(0);
    float shad = 0.;
    float rlh = 0.;
    float i2 = 0.; 
    
    //Spaghetified raymarcher 
    for(float i = 0.; i<STEPS; i++){
        p = ro+rd*rl;
        d = map(p, vec3(0));
        rl+=d.x;
        if((d.x)<0.0001){
            shad = i2/STEPS;
            if(hitonce)break;
            hitonce = true;
            rlh = rl;
        }
        if(rl>MDIST||(!hitonce&&i>STEPS-2.)){
            d.y = 0.;
            break;
        }
        rlg = rl-rlh;
        if(hitonce&&rlg>3.0){hitonce = false; i2 = 0.;}  
        if(hitonce)i2++;
    }
    //Color Surface
    if(d.y>0.0){
        vec3 n = norm(p);
        vec3 r = reflect(rd,n);
        vec3 ld = normalize(vec3(0,1,0));
        float spec = pow(max(0.,dot(r,ld)),13.0);

        //Color the triangle
        vec3 n2 = n*0.65+0.35;
        col += mix(vec3(1,0,0),vec3(0,1,0),sat(uv3.y*1.1))*n2.r;
        uv3*=rot(-(2.0*pi)/3.0);
        col += mix(vec3(0,1.0,0),vec3(0,0,1),sat(uv3.y*1.1))*n2.g;
        uv3*=rot(-(2.0*pi)/3.0);
        col += mix(vec3(0,0,1),vec3(1,0,0),sat(uv3.y*1.1))*n2.b;
        

        
        //NuSan SSS
        float sss=0.5;
        float sssteps = 10.;
        for(float i=1.; i<sssteps; ++i){
            float dist = i*0.2;
            sss += smoothstep(0.,1.,map(p+ld*dist,vec3(0)).x/dist)/(sssteps*1.5);
        }
        sss = clamp(sss,0.0,1.0);
        
        //blackle AO
        #define AO(a,n,p) smoothstep(-a,a,map(p,-n*a).x)
        float ao = AO(1.9,n,p)*AO(3.,n,p)*AO(7.,n,p);
        
        //Apply AO on the triangle
        if(rlg<0.001){
            col*=mix(ao,1.0,0.2);
        }
        //Color the inside of the crumbled bits 
        else {
            col = vec3(0.2-shad);
        }
        //Color the moving blocks
        if(d.y>1.0){
            col = (n*0.6+0.4)*vec3(sss)+spec;
        }
        //a bit of gamma correction
        col = pow(col,vec3(0.7));
    }
    //Color Background
    else{
        vec3 bg = mix(vec3(0.345,0.780,0.988),vec3(0.361,0.020,0.839),length(uv));
        col = bg;
    }
    fragColor = vec4(col,1.0);
}

//External AA, (I compacted it for fun)
void mainImage(out vec4 O,vec2 C){
    float px=1./AA,i,j;vec4 cl2,cl;
    if(AA==1.){render(cl,C);O=cl;return;}
    for(i=0.;i<AA +min(iTime,0.0);i++){for(j=0.;j<AA;j++){
    vec2 C2 = vec2(C.x+px*i,C.y+px*j);
    render(cl2,C2);cl+=cl2;
    rlg=0.; hitonce = false;
    }}cl/=AA*AA;O=cl;
}