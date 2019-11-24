#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// The inverse of the viewport dimensions along X and Y
uniform vec2 u_viewportInverse;

//// Color of the outline
uniform vec4 u_color;
uniform float u_outLineSize;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    vec2 uv = v_texCoord.xy;
    vec4 color = texture2D(u_texture, uv);

    if (color.a == 0.0) {
        if (texture2D(u_texture, uv + vec2(0.0, u_viewportInverse.y)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(0.0, -u_viewportInverse.y)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(u_viewportInverse.x, 0.0)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(-u_viewportInverse.x, 0.0)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(-u_viewportInverse.x, u_viewportInverse.y)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(-u_viewportInverse.x, -u_viewportInverse.y)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(u_viewportInverse.x, u_viewportInverse.y)).a != 0.0 ||
            texture2D(u_texture, uv + vec2(u_viewportInverse.x, -u_viewportInverse.y)).a != 0.0)
            color = u_color;
    } else {
    	color.a = u_color.a;
    }

    gl_FragColor = color;
}