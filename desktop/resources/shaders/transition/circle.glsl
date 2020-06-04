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
	float maxRadius = length(u_bufferSize) * 0.5;
	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
	if (u_phase > 0.0) {
		gl_FragColor.a = step(maxRadius * u_phase, length(gl_FragCoord.xy - u_bufferSize.xy * 0.5));
	} else {
		gl_FragColor.a = 1.0 - step(maxRadius * (1.0 + u_phase), length(gl_FragCoord.xy - u_bufferSize.xy * 0.5));
	}
}
