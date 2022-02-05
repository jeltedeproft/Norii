#version 330 core

#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

in vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_amount;
uniform float u_speed;
uniform float u_time;

in vec2 frag_uv;

uniform sampler2D virtual_screen;
uniform float sharpness = 2.0;

out vec4 frag_color;

float sharpen(float pix_coord) {
    float norm = (fract(pix_coord) - 0.5) * 2.0;
    float norm2 = norm * norm;
    return floor(pix_coord) + norm * pow(norm2, sharpness) / 2.0 + 0.5;
}

void main() {
    vec2 vres = textureSize(virtual_screen, 0);
    frag_color = texture2D(virtual_screen, vec2(
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

/* LICENSE
GLSL Smooth Scaler by Justin Snyder
To the extent possible under law, the person who associated CC0 with
GLSL Smooth Scaler has waived all copyright and related or neighboring rights
to GLSL Smooth Scaler.
You should have received a copy of the CC0 legalcode along with this
work.  If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
*/