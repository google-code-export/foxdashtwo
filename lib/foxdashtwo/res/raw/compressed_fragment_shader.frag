//thanks to arm and mali

precision mediump float;       	// Set the default precision to medium. We don't need as high of a precision in the fragment shader.

uniform sampler2D u_Texture;    // The input texture.
uniform sampler2D u_Alpha;      // The input alpha.

varying vec4 v_Color;          	// This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  
// The entry point for our fragment shader.
void main()                    		
{                              
	// Multiply the color by the diffuse illumination level and texture value to get final output color.
    vec4 colour = (v_Color  * texture2D(u_Texture, v_TexCoordinate));
    colour.a = (v_Color.a * texture2D(u_Alpha, v_TexCoordinate).r);
    gl_FragColor = colour;                                    		
}                                                                     	

