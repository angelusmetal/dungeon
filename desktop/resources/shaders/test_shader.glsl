#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// Buffer size
uniform vec2 u_bufferSize;

// Light
uniform float u_lightRange;
uniform float u_lightRadius;
uniform vec3 u_lightOrigin;
uniform vec4 u_lightColor;
uniform float u_lightHardness;
uniform vec4 u_ambientColor;

// Geometry
uniform vec4 u_segments[256];
uniform int u_segmentCount;

varying vec4 v_color;
varying vec2 v_texCoord;

// Given three colinear points p, q, r, the function checks if
// point q lies on line segment 'pr'
bool onSegment(vec2 p, vec2 q, vec2 r)
{
	return q.x == clamp(q.x, p.x, r.x) && q.y == clamp(q.y, p.y, r.y);
//	return step(q.x, p.x) * step(q.x, r.x) * step(q.y, p.y) * step(q.y, r.y);
//    return (q.x <= max(p.x, r.x) && q.x >= min(p.x, r.x) &&
//        q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y));
}

// To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are colinear
// 1 --> Clockwise
// 2 --> Counterclockwise
float orientation(vec2 p, vec2 q, vec2 r)
{
    // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
    // for details of below formula.
    return  (q.y - p.y) * (r.x - q.x) -
            (q.x - p.x) * (r.y - q.y);

//    if (val == 0) return 0; // colinear
//
//    return (val > 0)? 1: 2; // clock or counterclock wise
}

// The main function that returns true if line segment 'p1q1'
// and 'p2q2' intersect.
bool doIntersect(vec2 p1, vec2 q1, vec2 p2, vec2 q2)
{
    // Find the four orientations needed for general and
    // special cases
    float o1 = orientation(p1, q1, p2);
    float o2 = orientation(p1, q1, q2);
    float o3 = orientation(p2, q2, p1);
    float o4 = orientation(p2, q2, q1);

//    return (o1 != o2 && o3 != o4)
    // General case
    return (o1*o2 < 0.0 && o3*o4 < 0.0);
//    // Special Cases
//    // p1, q1 and p2 are colinear and p2 lies on segment p1q1
//    || (o1 == 0 && onSegment(p1, p2, q1))
//
//    // p1, q1 and q2 are colinear and q2 lies on segment p1q1
//    || (o2 == 0 && onSegment(p1, q2, q1))
//
//    // p2, q2 and p1 are colinear and p1 lies on segment p2q2
//    || (o3 == 0 && onSegment(p2, p1, q2))
//
//    // p2, q2 and q1 are colinear and q1 lies on segment p2q2
//    || (o4 == 0 && onSegment(p2, q1, q2));
}

float sampleLight(vec2 coord, vec2 lightSampleOrigin) {
	// Iterate segments for intersection
	for (int i = 0; i < u_segmentCount; ++i) {
		if (doIntersect(coord, lightSampleOrigin, u_segments[i].xy, u_segments[i].zw)) {
			return 0.0; // Intersection; return shadow
		}
	}
	// No intersection; return light with appropriate attenuation
	float dist = length(coord - lightSampleOrigin);
	return clamp((1.0 - dist / u_lightRange) * u_lightHardness, 0.0, 1.0);
}

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
	vec3 NormalMap = texture2D(u_texture, mod(gl_FragCoord.xy / 350.0, 1.0)).rgb;
	NormalMap.g = 1.0 - NormalMap.g;

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
