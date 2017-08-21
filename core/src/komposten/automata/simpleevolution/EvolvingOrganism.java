package komposten.automata.simpleevolution;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;

class EvolvingOrganism
{
	public static final long REPRODUCTION_THRESHOLD = 50;
	public static final float MAX_GENETIC_DISTANCE = 0.01f; //Max obtainable distance is 1.73
	public static final int INITIAL_HEALTH = 75;
	
	public enum Type
	{
		Alive,
		Dead;
	}
	
	private RandomXS128 random;
	private boolean dirty;
	private Type type;
	private Color color;
	
	private long reproductionTimer;
	private int health;
	
	
	public EvolvingOrganism(Type type)
	{
		random = new RandomXS128();
		color = new Color(0.7f, 0.7f, 0.7f, 1);
		setType(type);
	}
	
	
	public Color getColor()
	{
		return color;
	}
	
	
	public Type getType()
	{
		return type;
	}
	
	
	public void setType(Type type)
	{
		if (this.type == type)
			return;
		this.type = type;
		
		if (type == Type.Dead)
		{
			color.set(0, 0, 0, 1);
		}
		
		dirty = true;
	}
	
	
	public void changeHealth(int change)
	{
		health += change;
		
		
		if (health <= 0)
		{
			setType(Type.Dead);
		}
	}
	
	
	public void timePassed()
	{
		reproductionTimer++;
	}
	
	
	public boolean isDirty()
	{
		return dirty;
	}
	
	
	public void clearDirty()
	{
		dirty = false;
	}
	
	
	public boolean canReproduce()
	{
		return reproductionTimer > REPRODUCTION_THRESHOLD;
	}
	
	
	public boolean canReproduceWith(EvolvingOrganism other)
	{
		float dR = color.r - other.color.r;
		float dG = color.g - other.color.g;
		float dB = color.b - other.color.b;
		
		return Math.sqrt(dR*dR+dG*dG+dB*dB) < MAX_GENETIC_DISTANCE;
	}


	public void moveTo(EvolvingOrganism other)
	{
		other.color.set(color);
		other.setType(Type.Alive);
		setType(Type.Dead);
	}


	public void reproduceTo(EvolvingOrganism target, EvolvingOrganism partner)
	{
		target.setType(Type.Alive);
		target.health = INITIAL_HEALTH;
		target.color.set(color).lerp(partner.color, 0.5f);
		mutate(target.color);
		
		target.reproductionTimer = 0;
		reproductionTimer = 0;
		partner.reproductionTimer = 0;
	}


	private void mutate(Color color)
	{
		float r = addRandomDeviation(color.r);
		float g = addRandomDeviation(color.g);
		float b = addRandomDeviation(color.b);
		color.set(r, g, b, 1);
	}


	private float addRandomDeviation(float value)
	{
		float range = 0.1f;
		float interval = range / 2;
		float randomFloat = random.nextFloat() * range;
		float lowerLimit = 0.4f;
		
		if (value < lowerLimit + interval)
		{
			return value + (randomFloat - (value - lowerLimit));
		}
		else if (value > 1 - interval)
		{
			return value + (randomFloat - range + (1 - value));
		}
		else
		{
			return randomFloat - interval;
		}
	}
}
