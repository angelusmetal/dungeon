#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// Coords, size & color of sphere
uniform vec2 u_coord;
uniform float u_size;
uniform vec4 u_color;
uniform float u_ratio;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
	vec4 base = texture2D(u_texture, v_texCoord.xy) * v_color;
	vec2 dist = u_coord - v_texCoord.xy;
	dist.x *= u_ratio;
	// Correct ratio
	float i = 1.0 - length(dist) / u_size;

    gl_FragColor = base * (1-i) + u_color * i;
}