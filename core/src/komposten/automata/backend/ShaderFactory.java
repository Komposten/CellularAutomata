package komposten.automata.backend;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderFactory
{
	public static final String DEFAULT_COLOR = "default_color";
	public static final String DEFAULT_TEXTURE = "default_texture";
	
	private static Map<String, ShaderProgram> shaders;
	
	private static Camera camera;
	
	
	public static void initialise(Camera camera)
	{
		shaders = new HashMap<String, ShaderProgram>();
		ShaderFactory.camera = camera;
		
		loadDefaultShaders();
	}
	
	
	private static void loadDefaultShaders()
	{
		createShader(DEFAULT_COLOR, "shaders/default.vert", "shaders/default.frag");
		createShader(DEFAULT_TEXTURE, "shaders/default_texture.vert", "shaders/default_texture.frag");
		
		getShader(DEFAULT_TEXTURE).setUniformi("u_texture", 0);
	}
	
	
	public static boolean createShader(String name, String vertPath, String fragPath)
	{
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertPath), Gdx.files.internal(fragPath));
		
		if (!shader.isCompiled())
		{
			System.out.println("Could not compile the shader:");
			System.out.println(shader.getLog());
			return false;
		}
		
		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined, false);
		shader.end();
		
		shaders.put(name, shader);
		
		return true;
	}


	public static ShaderProgram getShader(String name)
	{
		return shaders.get(name);
	}
}
