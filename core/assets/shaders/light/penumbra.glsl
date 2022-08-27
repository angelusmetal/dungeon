#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;

// Draw a penumbra triangle that evenly distributes alpha on the starting vertex as well as between the opposing 2
void main() {
	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
	gl_FragColor.a = v_texCoord.x / (1.0 - v_texCoord.y) + v_color.a;

}
