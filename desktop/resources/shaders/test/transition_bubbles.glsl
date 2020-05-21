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
	float separation = length(u_bufferSize) * 0.05;
	float radius = separation / 1.4 * u_open;
	vec2 closest = mod(gl_FragCoord.xy, separation) - separation * 0.5;

	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
	gl_FragColor.a = step(radius, length(closest * v_texCoord.x));
}
