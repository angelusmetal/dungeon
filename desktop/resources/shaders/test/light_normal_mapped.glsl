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

// Based on https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson6

//8200
void main() {
	float lightPerSample = 1.0;// / u_sampleCount;

	vec2 normal = normalize(gl_FragCoord.xy - u_lightOrigin.xy);
	normal.yx = normal.xy;
	normal.x *= -1.0;
	float luminosity = 0.0;

//	vec2 spread = u_lightRadius * 2.0 / u_sampleCount * normal;
//	float offset = 0 - (u_sampleCount - 1) * 0.5;
//	for (float s; s < u_sampleCount; ++s) {
//		luminosity += sampleLight(gl_FragCoord.xy, u_lightOrigin + spread * (offset + s)) * lightPerSample;
//	}
	float dist = length(gl_FragCoord.xy - u_lightOrigin.xy);
	luminosity = clamp((1.0 - dist / u_lightRange) * u_lightHardness, 0.0, 1.0);

	//RGBA of our diffuse color
//	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);

	//RGB of our normal map
	vec3 NormalMap = texture2D(u_texture, v_texCoord.xy).rgb;
	// Uncomment below if vertical axis needs to be reversed
	//	NormalMap.g = 1.0 - NormalMap.g;

	//The delta position of light
	vec3 LightDir = vec3(u_lightOrigin.xy - gl_FragCoord.xy, u_lightOrigin.z);

	//Determine distance (used for attenuation) BEFORE we normalize our LightDir
	float D = length(LightDir);

	//normalize our vectors
	vec3 N = normalize(NormalMap * 2.0 - 1.0);
	vec3 L = normalize(LightDir);

	//Pre-multiply light color with intensity
	//Then perform "N dot L" to determine our diffuse term
	vec3 Diffuse = (u_lightColor.rgb * u_lightColor.a) * max(dot(N, L), 0.0);

	//pre-multiply ambient color with intensity
	vec3 Ambient = u_ambientColor.rgb * u_ambientColor.a;

	//calculate attenuation
	//float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );

	//the calculation which brings it all together
//	vec3 Intensity = Ambient + Diffuse * Attenuation;
	vec3 Intensity = Ambient + Diffuse * luminosity;
//	vec3 FinalColor = DiffuseColor.rgb * Intensity;
	vec3 FinalColor = vec3(1, 1, 1) * Intensity;
//	gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);
	gl_FragColor = v_color * vec4(FinalColor, 1.0);

}
