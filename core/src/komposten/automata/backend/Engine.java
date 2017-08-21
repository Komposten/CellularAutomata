package komposten.automata.backend;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import komposten.automata.Automata;
import komposten.automata.predatorprey.PixPredatorPrey;
import komposten.automata.predatorprey.PredatorPrey;

public class Engine implements Disposable
{
	private List<Automata> automatas;
	
	private boolean paused;
	private int automataIndex;
	
	
	public Engine(int width, int height)
	{
		automatas = new ArrayList<>();
		automatas.add(new PredatorPrey(width, height));
		automatas.add(new PixPredatorPrey(width, height));
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
	
	
	public Automata getCurrentAutomata()
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
		for (Automata automata : automatas)
		{
			automata.dispose();
		}
	}
}
