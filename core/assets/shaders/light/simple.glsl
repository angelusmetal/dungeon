#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// Light
uniform float u_lightRange;
uniform vec3 u_lightOrigin;
uniform vec4 u_lightColor;
uniform float u_lightHardness;
uniform vec4 u_ambientColor;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
	vec2 normal = normalize(gl_FragCoord.xy - u_lightOrigin.xy);
	float dist = length(gl_FragCoord.xy - u_lightOrigin.xy);
	float luminosity = pow(u_lightHardness, dist / u_lightRange * 4.0);

	vec3 Diffuse = u_lightColor.rgb * u_lightColor.a;
	vec3 Ambient = u_ambientColor.rgb * u_ambientColor.a * texture2D(u_texture, v_texCoord.xy).rgb;
	vec3 Intensity = Ambient + Diffuse * luminosity;
	vec3 FinalColor = vec3(1, 1, 1) * Intensity;
	gl_FragColor = v_color * vec4(FinalColor, 1.0);

}
