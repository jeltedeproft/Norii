#version 330 core

#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

in vec2 v_texCoords;
in vec2 frag_uv;

uniform sampler2D u_texture;
uniform sampler2D u_viewport;
uniform float u_sharpness;
uniform float u_amount;
uniform float u_speed;
uniform float u_time;


float sharpen(float pix_coord) {
    float norm = (fract(pix_coord) - 0.5) * 2.0;
    float norm2 = norm * norm;
    return floor(pix_coord) + norm * pow(norm2, u_sharpness) / 2.0 + 0.5;
}

void main() {
    vec2 vres = textureSize(u_viewport, 0);
    gl_FragColor = texture2D(u_viewport, vec2(
        sharpen(v_texCoords.x * vres.x) / vres.x,
        sharpen(v_texCoords.y * vres.y) / vres.y
    ));
    // To visualize how this makes the grid:
    // frag_color = vec4(
    //     fract(sharpen(frag_uv.x * vres.x)),
    //     fract(sharpen(frag_uv.y * vres.y)),
    //     0.5, 1.0
    // );
}

/*
Normally, you only really have 2 texture filtering options for upscaling and they both have their problems.
Bilinear filtering makes everything blurry and gross, so that's usually super bad
Nearest filtering gets the job done but looks funky when you aren't scaling by an integer ratio
This is a compromise that smooths out boundaries between pixels while leaving the pixels themselves crisp and clean.
As a bonus, it is adjustable with floating-point granularity.
To use this, ensure that the source texture is set to use bilinear filtering.
*/