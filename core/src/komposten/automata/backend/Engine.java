package komposten.automata.backend;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import komposten.automata.Automaton;
import komposten.automata.predatorprey.PixPredatorPrey;
import komposten.automata.predatorprey.PredatorPrey;
import komposten.automata.simpleevolution.SimpleEvolution;

public class Engine implements Disposable
{
	private List<Automaton> automatas;
	
	private boolean paused;
	private int automataIndex;
	
	
	public Engine(int width, int height, OrthographicCamera orthographicCamera, PerspectiveCamera perspectiveCamera)
	{
		automatas = new ArrayList<>();
		System.out.println("Creating PredatorPrey...");
		automatas.add(new PredatorPrey(width, height, orthographicCamera));
		System.out.println("Creating PixPredatorPrey...");
		automatas.add(new PixPredatorPrey(width, height, orthographicCamera));
		System.out.println("Creating SimpleEvolution...");
		automatas.add(new SimpleEvolution(width, height, orthographicCamera));
	}
	
	
	public void update()
	{
		if (!paused)
		{
			automatas.get(automataIndex).update();
		}
	}
	
	
	public void render()
	{
		automatas.get(automataIndex).render();
	}
	
	
	public void renderText(BitmapFont font, SpriteBatch batch)
	{
		automatas.get(automataIndex).renderText(font, batch);
	}
	
	
	public Automaton getCurrentAutomata()
	{
		return automatas.get(automataIndex);
	}
	
	public void setAutomataIndex(int automataIndex)
	{
		this.automataIndex = automataIndex;
		
		if (automataIndex >= automatas.size())
		{
			this.automataIndex = automatas.size() - 1;
		}
	}
	
	
	public void togglePaused()
	{
		paused = !paused;
	}


	@Override
	public void dispose()
	{
		for (Automaton automata : automatas)
		{
			automata.dispose();
		}
	}
}
