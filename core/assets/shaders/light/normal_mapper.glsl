#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
uniform vec2 u_texelSize;

varying vec4 v_color;
varying vec2 v_texCoord;

float value(vec4 color) {
	return color.x + color.y + color.z;
}

/**
 * A feeble attempt at doing a shader to generate normal maps off textures
 */
void main() {
    vec2 uv = v_texCoord.xy;
    vec4 color = texture2D(u_texture, uv);

	// Sample the value delta of neighboring pixels
    float val = value(color);
    float upleft = value(texture2D(u_texture, uv + vec2(-u_texelSize.x, u_texelSize.y))) - val;
    float up = value(texture2D(u_texture, uv + vec2(0.0, u_texelSize.y))) - val;
    float upright = value(texture2D(u_texture, uv + vec2(u_texelSize.x, u_texelSize.y))) - val;
    float left = value(texture2D(u_texture, uv + vec2(-u_texelSize.x, 0.0))) - val;
    float right = value(texture2D(u_texture, uv + vec2(u_texelSize.x, 0.0))) - val;
    float downleft = value(texture2D(u_texture, uv + vec2(-u_texelSize.x, -u_texelSize.y))) - val;
    float down = value(texture2D(u_texture, uv + vec2(0.0, -u_texelSize.y))) - val;
    float downright = value(texture2D(u_texture, uv + vec2(u_texelSize.x, -u_texelSize.y))) - val;

    // I'm making these names up; don't judge me
    float verticalBias = (upleft + up + upright - downleft - down - downright) / 3.0;
	float horizontalBias = (upright + right + downright - upleft - left - downleft) / 3.0;
	float radialBias = clamp(length(vec2(horizontalBias, verticalBias)), 0.0, 1.0);

    gl_FragColor = v_color * vec4(horizontalBias * 0.5 + 0.5, verticalBias * 0.5 + 0.5, radialBias * 0.5 + 0.5, 1.0);
}
