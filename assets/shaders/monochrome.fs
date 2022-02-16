#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture0;
uniform float u_amount;


void main() {
   vec4 sample = texture2D(u_texture0, v_texCoords);
   float gray = 0.21 * sample.r + 0.71 * sample.g + 0.07 * sample.b;
   gl_FragColor = vec4(sample.rgb * (1.0 - u_amount) + (gray * u_amount), sample.a);
}