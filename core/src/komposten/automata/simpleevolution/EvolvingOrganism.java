package komposten.automata.simpleevolution;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;

import komposten.utilities.tools.MathOps;

class EvolvingOrganism
{
	private static final float COLOUR_COMPONENT_LIMIT = 0.4f;
	private static final float MAX_GENETIC_DISTANCE = 0.01f; //Max obtainable distance is 1.73
	
	private static final long REPRODUCTION_THRESHOLD_BASE = 100;
	private static final long REPRODUCTION_THRESHOLD_REDUCTION = 25;
	private static final int BASE_HEALTH = 75;
	private static final int MAX_ADDITIONAL_HEALTH = 75;
	private static final int REPRODUCTION_DAMAGE_BASE = 75;
	private static final int REPRODUCTION_DAMAGE_REDUCTION = 60;
	private static final int HIT_DAMAGE_BASE = 100;
	private static final int HIT_DAMAGE_REDUCTION = 80;
	
	public enum Type
	{
		Alive,
		Dead;
	}
	
	private RandomXS128 random;
	private boolean dirty;
	private Type type;
	/**
	 * Represents fitness/genome as follows: <br />
	 * - Red = damage resistance (when attacked) <br />
	 * - Green = reproductive effectiveness (how often can you reproduce?) <br />
	 * - Blue = damage resistance (from reproducing)
	 */
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
	
	
	public void damage()
	{
		int damage = HIT_DAMAGE_BASE - (int)(getFitnessValue(color.r) * HIT_DAMAGE_REDUCTION);
		changeHealth(-damage);
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
		return reproductionTimer > REPRODUCTION_THRESHOLD_BASE - (getFitnessValue(color.g) * REPRODUCTION_THRESHOLD_REDUCTION);
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
		target.color.set(color).lerp(partner.color, 0.5f);
		mutate(target.color);
//		target.health = (int) (BASE_HEALTH + MAX_ADDITIONAL_HEALTH * getFitnessValue(target.color.b));
		target.health = BASE_HEALTH;
		
		int damage = REPRODUCTION_DAMAGE_BASE - (int)(getFitnessValue(color.b) * REPRODUCTION_DAMAGE_REDUCTION);
		changeHealth(-damage);
		
		target.reproductionTimer = 0;
		reproductionTimer = 0;
		partner.reproductionTimer = 0;
	}
	
	
	private List<Float> temp = new ArrayList<>();
	private void mutate (Color color)
	{
		float range = 0.1f;
		float interval = range / 2;
		float lossMultiplier = 2;
		
		float gain = random.nextFloat() * range / 2;
		float loss = -random.nextFloat() * range;
		
		//loss = -2 * (x + gain)  ==>  x = loss / -2 - gain
		//loss + x = -2 * gain  ==>  x = -2 * gain - loss
		
		float x = 0;
		if (gain < -loss / lossMultiplier)
			x = loss / (-lossMultiplier) - gain;
		else if (gain > -loss / lossMultiplier)
			x = (-lossMultiplier) * gain - loss;
		
		temp.add(gain);
		temp.add(loss);
		temp.add(x);
		float red = temp.remove(random.nextInt(temp.size()));
		float green = temp.remove(random.nextInt(temp.size()));
		float blue  = temp.remove(0);
		
		red = MathOps.clamp(COLOUR_COMPONENT_LIMIT, 1, color.r + red);
		green = MathOps.clamp(COLOUR_COMPONENT_LIMIT, 1, color.g + green);
		blue = MathOps.clamp(COLOUR_COMPONENT_LIMIT, 1, color.b + blue);
		
		color.set(red, green, blue, 1);
	}
	
	
	private float getFitnessValue(float colourComponent)
	{
		return (colourComponent - COLOUR_COMPONENT_LIMIT) / (1 - COLOUR_COMPONENT_LIMIT);
	}


//	private void mutate(Color color)
//	{
//		float r = getRandomDeviation(color.r);
//		float g = getRandomDeviation(color.g);
//		float b = -(r+g);
//		color.add(r, g, b, 1);
//	}
//
//
//	private float getRandomDeviation(float value)
//	{
//		float range = 0.1f;
//		float interval = range / 2;
//		float randomFloat = random.nextFloat() * range;
//		if (value < COLOUR_COMPONENT_LIMIT + interval)
//		{
//			return (randomFloat - (value - COLOUR_COMPONENT_LIMIT));
//		}
//		else if (value > 1 - interval)
//		{
//			return (randomFloat - range + (1 - value));
//		}
//		else
//		{
//			return randomFloat - interval;
//		}
//	}
}
