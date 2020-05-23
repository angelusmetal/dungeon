#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
uniform vec2 u_bufferSize;
uniform float u_phase;

varying vec4 v_color;
varying vec2 v_texCoord;

// Draw a circle transition
void main() {
	float separation = length(u_bufferSize) * 0.05;
	float radius = separation / 1.4 * abs(u_phase);
	vec2 closest = mod(gl_FragCoord.xy, separation) - separation * 0.5;

	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
	if (u_phase > 0.0) {
		gl_FragColor.a = step(radius, length(closest * v_texCoord.x));
	} else {
		gl_FragColor.a = step(radius, length(closest * (1.0 - v_texCoord.x)));
	}
}
