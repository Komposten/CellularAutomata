package komposten.automata;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.profiling.GL30Profiler;
import com.badlogic.gdx.math.Vector3;

import komposten.automata.backend.Engine;
import komposten.automata.backend.ShaderFactory;
import komposten.automata.backend.rendering.g3d.GridMesh3D;
import komposten.automata.backend.rendering.g3d.GridMesh3D2;
import komposten.automata.backend.rendering.g3d.Cell3D.Face;


public class Application extends ApplicationAdapter
{
	private OrthographicCamera orthographicCamera;
	private PerspectiveCamera perspectiveCamera;
	private SpriteBatch batch;
	
	private BitmapFont font;
	
//	private Engine engine;
	
	private double timer;
	private double gcTimer;
	
	private boolean debug;
	
	private GridMesh3D mesh;
	private GridMesh3D2 mesh2;
	
	@Override
	public void create()
	{
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		Gdx.input.setCursorCatched(true);
		
		orthographicCamera = new OrthographicCamera(width, height);
		orthographicCamera.translate(orthographicCamera.viewportWidth / 2, orthographicCamera.viewportHeight / 2, 0);
		orthographicCamera.update();
		
		perspectiveCamera = new PerspectiveCamera(67, width, height);
		perspectiveCamera.translate(500, 1280, 500);
		perspectiveCamera.lookAt(160, 1280, 160);
		perspectiveCamera.near = 1f;
		perspectiveCamera.far = 3000f;
		perspectiveCamera.update();
		
		ShaderFactory.initialise(orthographicCamera);
		
//		engine = new Engine(width, height, orthographicCamera, perspectiveCamera);
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		System.out.println("Creating GridMesh3D...");
		long time = System.nanoTime();
		mesh = new GridMesh3D(32, 256, 32, 10);
		System.out.println((System.nanoTime() - time) / 1E9);
		time = System.nanoTime();
		mesh2 = new GridMesh3D2(32, 256, 32, 10);
		System.out.println((System.nanoTime() - time) / 1E9);
//		engine.togglePaused();
		
		Gdx.input.setInputProcessor(processor);
	}


	@Override
	public void render()
	{
		timer += Gdx.graphics.getDeltaTime();
		gcTimer += Gdx.graphics.getDeltaTime();
		
		if (gcTimer > 60)
		{
			runGC();
			gcTimer = 0;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		
//		engine.update();
//		engine.render();

		Gdx.gl.glEnable(GL30.GL_CULL_FACE);
		Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
		ShaderProgram shader = ShaderFactory.getShader(ShaderFactory.DEFAULT_COLOR);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", perspectiveCamera.combined);
//		mesh.getMesh().render(shader, GL30.GL_TRIANGLES);
		mesh2.getMesh().render(shader, GL30.GL_TRIANGLES);
		shader.end();
		Gdx.gl.glDisable(GL30.GL_CULL_FACE);
		Gdx.gl.glDisable(GL30.GL_DEPTH_TEST);
		
		
		batch.begin();
		int x = 10;
		int y = Gdx.graphics.getHeight() - 10;
//		font.draw(batch, "Automata: " + engine.getCurrentAutomata().getName(), x, y);
		if (debug)
		{
			drawDebug(x, y - 30);
		}
//		engine.renderText(font, batch);
		batch.end();
		
		readInput();
	}


	private void runGC()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.gc();
			}
		});
		thread.start();
	}


	private void drawDebug(int x, int y)
	{
		String fps = "FPS: " + Gdx.graphics.getFramesPerSecond();
		String drawCalls = "Draw calls: " + GL30Profiler.drawCalls;
		String shaderSwitches = "Shader switches: " + GL30Profiler.shaderSwitches;
		String textureBinds = "Texture bindings: " + GL30Profiler.textureBindings;
		String glCalls = "GL calls: " + GL30Profiler.calls;
		String time = "Timer: " + formatTime((long)(timer*1E9));
		
		font.draw(batch, fps, x, y);
		font.draw(batch, drawCalls, x, y - 20);
		font.draw(batch, shaderSwitches, x, y - 40);
		font.draw(batch, textureBinds, x, y - 60);
		font.draw(batch, glCalls, x, y - 80);
		font.draw(batch, time, x, y - 100);
		font.getCache().clear();

		GL30Profiler.reset();
	}


	private String formatTime(long nanoTime)
	{
		int minutes = (int) (nanoTime / 60E9d);
		int seconds = (int) (nanoTime / 1E9d) - (minutes * 60);
		int hundreds = (int) (((nanoTime / 1E9d) % 1) * 100);
		return String.format("%1$02d:%2$02d:%3$02d", minutes, seconds, hundreds);
	}
	/*
	 * x = 1234567890
	 * minutes = x / 60E9 = 0
	 * seconds = x / 1E9 - 0 = 1.23 = 1
	 * hundreds = x / 1E7 - 100 = 123 - 100 = 23
	 */
	
	
	private void toggleDebug()
	{
		debug = !debug;
		
		if (debug)
			GL30Profiler.enable();
		else
			GL30Profiler.disable();
	}


	@Override
	public void dispose()
	{
//		engine.dispose();
	}
	
	
	Vector3 dummy = new Vector3();
	private void readInput()
	{
		boolean update = false;
		if (Gdx.input.isKeyPressed(Input.Keys.W))
		{
			dummy.set(perspectiveCamera.direction.x, 0, perspectiveCamera.direction.z);
			perspectiveCamera.translate(dummy);
			update = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S))
		{
			dummy.set(-perspectiveCamera.direction.x, 0, -perspectiveCamera.direction.z);
			perspectiveCamera.translate(dummy);
			update = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D))
		{
			dummy.set(-perspectiveCamera.direction.z, 0, perspectiveCamera.direction.x);
			perspectiveCamera.translate(dummy);
			update = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A))
		{
			dummy.set(perspectiveCamera.direction.z, 0, -perspectiveCamera.direction.x);
			perspectiveCamera.translate(dummy);
			update = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			perspectiveCamera.translate(0, 1, 0);
			update = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
		{
			perspectiveCamera.translate(0, -1, 0);
			update = true;
		}
		
		if (update)
		{
//			perspectiveCamera.lookAt(mesh.getColumnCount()/2*mesh.getCellSize(), mesh.getRowCount()/2*mesh.getCellSize(), mesh.getLayerCount()/2*mesh.getCellSize());
			perspectiveCamera.lookAt(mesh2.getColumnCount()/2*mesh2.getCellSize(), mesh2.getRowCount()/2*mesh2.getCellSize(), mesh2.getLayerCount()/2*mesh2.getCellSize());
			perspectiveCamera.update();
		}
	}
	
	
	private InputProcessor processor = new InputAdapter()
	{
		@Override
		public boolean keyUp(int keycode)
		{
			if (keycode == Input.Keys.F1)
			{
				toggleDebug();
				return true;
			}
//			else if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.P)
//			{
//				engine.togglePaused();
//				return true;
//			}
//			else if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9)
//			{
//				engine.setAutomataIndex(keycode - Input.Keys.NUM_1);
//			}
			
			return false;
		}
		
		
		@Override
		public boolean keyTyped(char character)
		{
			return false;
		}
		
		
		@Override
		public boolean keyDown(int keycode)
		{
			return false;
		}
		
		
		int oldX = -1;
		int oldY = -1;
		Vector3 right = new Vector3();
		@Override
		public boolean mouseMoved(int screenX, int screenY)
		{
			if (oldX == -1 || oldY == -1)
			{
				oldX = screenX;
				oldY = screenY;
				return false;
			}
			else
			{
				int dX = screenX - oldX;
				int dY = screenY - oldY;
				oldX = screenX;
				oldY = screenY;
				perspectiveCamera.rotate(perspectiveCamera.up, -dX / 5);
				perspectiveCamera.update();
				right.set(perspectiveCamera.direction.z, 0, -perspectiveCamera.direction.x);
//				perspectiveCamera.rotate(right, dY / 5);
				perspectiveCamera.update();
				return true;
			}
		}
	};
}
