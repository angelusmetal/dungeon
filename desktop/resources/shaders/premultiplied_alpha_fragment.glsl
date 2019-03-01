#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// Vertex color
varying vec4 v_color;
// Texture coordinate
varying vec2 v_texCoord;

void main() {
    gl_FragColor = v_color * texture2D(u_texture, v_texCoord);
   	gl_FragColor.rgb *= gl_FragColor.a;
}