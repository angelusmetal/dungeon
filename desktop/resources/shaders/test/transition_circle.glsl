#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
uniform vec2 u_bufferSize;
uniform float u_open;

varying vec4 v_color;
varying vec2 v_texCoord;

// Draw a circle transition
void main() {
	float maxRadius = length(u_bufferSize) * 0.5;
	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
	gl_FragColor.a = step(maxRadius * u_open, length(gl_FragCoord.xy - u_bufferSize.xy * 0.5));
}
