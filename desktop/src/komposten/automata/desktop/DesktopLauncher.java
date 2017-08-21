package komposten.automata.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import komposten.automata.Application;


public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		config.vSyncEnabled = false;
		config.title = "Cellular Automata";
		config.useGL30 = true;
		new LwjglApplication(new Application(), config);
	}
}
