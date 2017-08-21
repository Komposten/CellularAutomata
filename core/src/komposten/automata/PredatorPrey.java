package komposten.automata;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.RandomXS128;

import komposten.automata.Organism.Type;
import komposten.automata.backend.ShaderFactory;
import komposten.automata.backend.rendering.GridMesh;

public class PredatorPrey extends Automata
{
	private static final int CELL_SIZE = 5;
	private GridMesh gridMesh;
	private ShaderProgram shader;
	
	private Organism[] organisms;
	private RandomXS128 random;
	
	
	public PredatorPrey(int width, int height)
	{
		super("PredatorPrey");
		gridMesh = new GridMesh(width, height, CELL_SIZE);
		shader = ShaderFactory.getShader(ShaderFactory.DEFAULT_COLOR);
		random = new RandomXS128();
		
		organisms = new Organism[gridMesh.getCellCount()];
		
		createStartingGrid();
	}
	
	
	private void createStartingGrid()
	{
		for (int i = 0; i < organisms.length; i++)
		{
			Organism organism = new Organism(getRandomType());
			organisms[i] = organism;
			gridMesh.setColor(organism.getColor(), i);
		}
		
		gridMesh.refreshMesh();
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
		for (int r = 0; r < gridMesh.getRowCount(); r++)
		{
			for (int c = 0; c < gridMesh.getColumnCount(); c++)
			{
				int index = gridMesh.getIndex(r, c);
				
				Organism organism = organisms[index];

				int adjacentR = r + random.nextInt(3) - 1;
				int adjacentC = c + random.nextInt(3) - 1;
				
				if (adjacentR < 0 || adjacentR >= gridMesh.getRowCount()) continue;
				if (adjacentC < 0 || adjacentC >= gridMesh.getColumnCount()) continue;
				
				Organism neighbour = organisms[gridMesh.getIndex(adjacentR, adjacentC)];
				
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
					gridMesh.setColor(organism.getColor(), r, c);
					organism.clearDirty();
				}
				
				if (neighbour.isDirty())
				{
					gridMesh.setColor(neighbour.getColor(), adjacentR, adjacentC);
					neighbour.clearDirty();
				}
			}
		}
		
		gridMesh.refreshMesh();
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
		gridMesh.getMesh().render(shader, GL30.GL_TRIANGLES);
		shader.end();
	}


	@Override
	public void dispose()
	{
		gridMesh.dispose();
	}
}
