package komposten.automata.predatorprey;

import com.badlogic.gdx.graphics.Color;


class Organism
{
	public static final int MAX_HEALTH = 100;
	private static final int START_HEALTH = 10;
	
	public enum Type
	{
		Predator(Color.RED), Prey(Color.GREEN), Nothing(Color.BLACK);

		public Color color;


		private Type(Color color)
		{
			this.color = color;
		}
	}


	
	private boolean dirty;
	private int health;
	private Type type;
	private Color color;


	public Organism(Type type)
	{
		this.type = type;
		health = START_HEALTH;
		color = new Color(0, 0, 0, 1);
		
		dirty = false;
	}


	public void setType(Type type)
	{
		this.type = type;
		color.set(type.color);
		dirty = true;
	}


	public void setHealth(int health)
	{
		this.health = health;

		if (health < 0)
		{
			setType(Type.Nothing);
		}

		float factor = (health / (float)MAX_HEALTH) * 0.75f + 0.25f;
		color.set(type.color).mul(factor, factor, factor, 1);
		dirty = true;
	}


	public Type getType()
	{
		return type;
	}


	public Color getColor()
	{
		return color;
	}


	public int getHealth()
	{
		return health;
	}
	
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	
	public void clearDirty()
	{
		dirty = false;
	}


	public void moveTo(Organism other)
	{
		other.setHealth(getHealth());
		other.setType(getType());

		setType(Type.Nothing);
	}


	public void reproduceTo(Organism other)
	{
		other.setType(Type.Prey);
		other.setHealth(START_HEALTH);
		setHealth(START_HEALTH);
	}
}
