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
uniform vec2 u_lightOrigin;
uniform vec4 u_lightColor;
uniform float lightHardness = 1.0;

// Geometry
uniform vec4 u_segments[256];
uniform int u_segmentCount;

// Casting
uniform int u_sampleCount;

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
	return clamp((1.0 - dist / u_lightRange) * lightHardness, 0.0, 1.0);
}

//8200
void main() {
	vec2 coord = v_texCoord.xy * u_bufferSize;
	float lightPerSample = 1.0 / u_sampleCount;

	vec2 normal = normalize(coord - u_lightOrigin);
	normal.yx = normal.xy;
	normal.x *= -1;
	float luminosity = 0;

	vec2 spread = u_lightRadius * 2 / u_sampleCount * normal;
	float offset = 0 - (u_sampleCount - 1) * 0.5;
	for (float s; s < u_sampleCount; ++s) {
		luminosity += sampleLight(coord, u_lightOrigin + spread * (offset + s)) * lightPerSample;
	}
	gl_FragColor = v_color * (1.0-luminosity) + u_lightColor * luminosity;

	// Display light radius as dark circle
	if (length(u_lightOrigin - coord) < u_lightRadius) {
		gl_FragColor *= 0.8;
	}

	// Awful hack to prevent the shader from breaking
    if (v_texCoord.x == 1.0 && v_texCoord.y == 1.0) {
    	gl_FragColor = texture2D(u_texture, v_texCoord.xy) * v_color;
    	gl_FragColor.a = luminosity;
    }

}
