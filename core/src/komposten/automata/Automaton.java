package komposten.automata;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class Automaton implements Disposable
{
	private String name;
	
	public Automaton(String name)
	{
		this.name = name;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	protected abstract void createStartingGrid();
	public abstract void update();
	public abstract void render();
	public abstract void renderText(BitmapFont font, SpriteBatch batch);
}
