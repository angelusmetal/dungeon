#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
/** texel size */
uniform vec2 u_texelSize;
/** amount of samples (the more, the smoother) */
uniform int u_samples;
/** blur: spread of samples, in texels (0: no blur) */
uniform float u_blur;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {

    vec2 uv = v_texCoord.xy;
	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
	for (int i = -u_samples; i <= u_samples; ++i) {
		for (int j = -u_samples; j <= u_samples; ++j) {
//			color += texture2D(u_texture, uv + vec2(u_texelSize.x * i * u_blur, u_texelSize.y * j * u_blur));
			color += texture2D(u_texture, vec2(v_texCoord.x + u_texelSize.x * i * u_blur, v_texCoord.y + u_texelSize.y * j * u_blur));
		}
	}
	int total_samples = (u_samples + u_samples + 1) * (u_samples + u_samples + 1);
	gl_FragColor = v_color * color / total_samples;
}
