uniform mat4 u_MVPMatrix;		// A constant representing the combined model/view/projection matrix.      		       
uniform mat4 u_MVMatrix;		// A constant representing the combined model/view matrix.       		
		  			
attribute vec4 a_Position;		// Per-vertex position information we will pass in.   				
uniform vec4 a_Color;			// Per-vertex color information we will pass in. 			    
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in. 		

uniform float u_Brightness;		//	brightness....		  
		  
varying vec3 v_Position;		// This will be passed into the fragment shader.       		
varying vec4 v_Color;			// This will be passed into the fragment shader.          		
varying vec3 v_Normal;			// This will be passed into the fragment shader.  
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.    		  

uniform vec3 u_LightDir;
uniform float u_LightAngle;

// The entry point for our vertex shader.  
void main()                                                 	
{                                                         
	// Transform the vertex into eye space. 	
	v_Position = vec3(u_MVMatrix * a_Position);            
		
	// Pass through the color.
	v_Color = a_Color * u_Brightness;
	
	// Pass through the texture coordinate.
	v_TexCoordinate = a_TexCoordinate;                                      
	
	// Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(0.0, 0.0, 1.0, 0.0));
          
	// gl_Position is a special variable used to store the final position.
	// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
	gl_Position = u_MVPMatrix * a_Position;                       		  
}                                                          