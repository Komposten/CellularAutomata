package komposten.automata;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.RandomXS128;

import komposten.automata.Organism.Type;
import komposten.automata.backend.ShaderFactory;
import komposten.automata.backend.rendering.GridPixmap;
import komposten.automata.backend.rendering.Quad;

public class PixPredatorPrey extends Automata
{
	private static final int CELL_SIZE = 1;
	
	private Quad quad;
	private GridPixmap gridPixmap;
	private ShaderProgram shader;
	
	private Organism[] organisms;
	private RandomXS128 random;
	
	
	public PixPredatorPrey(int width, int height)
	{
		super("PixPredatorPrey");
		gridPixmap = new GridPixmap(width, height, CELL_SIZE);
		shader = ShaderFactory.getShader(ShaderFactory.DEFAULT_TEXTURE);
		random = new RandomXS128();
		
		organisms = new Organism[gridPixmap.getCellCount()];
		quad = new Quad(width, height, true);
		
		createStartingGrid();
	}
	
	
	private void createStartingGrid()
	{
		for (int i = 0; i < organisms.length; i++)
		{
			Organism organism = new Organism(getRandomType());
			organisms[i] = organism;
			gridPixmap.setColor(organism.getColor(), i);
		}
		
		gridPixmap.refreshTexture();
	}


	private Type getRandomType()
	{
		int value = random.nextInt(1000);
		
		if (value < 50)
			return Type.Predator;
		else if (value < 100)
			return Type.Prey;
		else
			return Type.Nothing;
	}


	@Override
	public void update()
	{
		for (int r = 0; r < gridPixmap.getRowCount(); r++)
		{
			for (int c = 0; c < gridPixmap.getColumnCount(); c++)
			{
				int index = gridPixmap.getIndex(r, c);
				
				Organism organism = organisms[index];

				int adjacentR = r + random.nextInt(3) - 1;
				int adjacentC = c + random.nextInt(3) - 1;
				
				if (adjacentR < 0 || adjacentR >= gridPixmap.getRowCount()) continue;
				if (adjacentC < 0 || adjacentC >= gridPixmap.getColumnCount()) continue;
				
				Organism neighbour = organisms[gridPixmap.getIndex(adjacentR, adjacentC)];
				
				switch (organism.getType())
				{
					case Predator :
						updatePredator(organism, neighbour);
						break;
					case Prey :
						updatePrey(organism, neighbour);
						break;
					case Nothing :
						break;
				}
				
				if (organism.isDirty())
				{
					gridPixmap.setColor(organism.getColor(), r, c);
					organism.clearDirty();
				}
				
				if (neighbour.isDirty())
				{
					gridPixmap.setColor(neighbour.getColor(), adjacentR, adjacentC);
					neighbour.clearDirty();
				}
			}
		}
		
		gridPixmap.refreshTexture();
	}
	
	
	private void updatePredator(Organism organism, Organism neighbour)
	{
		organism.setHealth(organism.getHealth() - 1);
		
		switch (neighbour.getType())
		{
			case Predator :
				break;
			case Prey :
				neighbour.setType(organism.getType());
				organism.setHealth(organism.getHealth() + neighbour.getHealth());
				break;
			case Nothing :
				organism.moveTo(neighbour);
				break;
		}
	}


	private void updatePrey(Organism organism, Organism neighbour)
	{
		organism.setHealth(organism.getHealth() + 1);

		switch (neighbour.getType())
		{
			case Predator :
			case Prey :
				break;
			case Nothing :
				if (organism.getHealth() > Organism.MAX_HEALTH)
				{
					organism.reproduceTo(neighbour);
				}
				else
				{
					organism.moveTo(neighbour);
				}
				break;
		}
	}


	@Override
	public void render()
	{
		shader.begin();
		gridPixmap.getTexture().bind(0);
		quad.render(shader, GL30.GL_TRIANGLES);
		shader.end();
	}


	@Override
	public void dispose()
	{
		gridPixmap.dispose();
	}
}
