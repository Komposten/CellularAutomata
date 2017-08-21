package komposten.automata;

import com.badlogic.gdx.utils.Disposable;

public abstract class Automata implements Disposable
{
	private String name;
	
	public Automata(String name)
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
}
