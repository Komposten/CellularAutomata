package komposten.automata.predatorprey3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

import komposten.automata.Automaton;
import komposten.automata.backend.rendering.g3d.GridMesh3D2;
import komposten.automata.predatorprey.Organism;
import komposten.automata.predatorprey.Organism.Type;

public class PredatorPrey3D extends Automaton
{
	private static final int CELL_SIZE = 1;
	
	private GridMesh3D2 mesh;
	
	private Organism[] organisms;
	private RandomXS128 random;
	
	private int predators;
	private int prey;

	private ModelBatch batch;
	private Renderable renderable;
	
	
	public PredatorPrey3D(int width, int height, int depth, ModelBatch batch)
	{
		super("PredatorPrey");
		this.batch = batch;
		mesh = new GridMesh3D2(width, height, depth, CELL_SIZE, false);
		random = new RandomXS128();
		
		organisms = new Organism[mesh.getCellCount()];
		
		renderable = new Renderable();
		renderable.environment = new Environment();
		renderable.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		renderable.environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(0, -.2f, -1)));
		renderable.material = new Material("PredatorPrey3D");
		renderable.worldTransform.idt();
		renderable.meshPart.set("cells", mesh.getMesh(), 0, mesh.getMesh().getNumVertices(), GL30.GL_TRIANGLES);
		
		createStartingGrid();
	}
	
	
	@Override
	protected void createStartingGrid()
	{
		for (int i = 0; i < organisms.length; i++)
		{
			Organism organism = new Organism(getRandomType());
			organisms[i] = organism;
			
			if (organism.getType() == Type.Nothing)
				mesh.removeCell(i);
			else
				mesh.updateCell((short)0, organism.getColor(), i);
		}
		
		refreshMesh();
	}


	private void refreshMesh()
	{
		mesh.refreshMesh();
		renderable.meshPart.size = mesh.getMesh().getNumVertices();
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
		predators = 0;
		prey = 0;
		
		for (int x = 0; x < mesh.getWidth(); x++)
		{
			for (int y = 0; y < mesh.getHeight(); y++)
			{
				for (int z = 0; z < mesh.getDepth(); z++)
				{
					int index = mesh.getIndex(x, y, z);
					
					Organism organism = organisms[index];
					
					if (organism.getType() == Type.Nothing)
					{
						continue;
					}
					
					int adjacentX = x + random.nextInt(3) - 1;
					int adjacentY = y + random.nextInt(3) - 1;
					int adjacentZ = z + random.nextInt(3) - 1;
					
					if (adjacentX < 0 || adjacentX >= mesh.getWidth()) continue;
					if (adjacentY < 0 || adjacentY >= mesh.getHeight()) continue;
					if (adjacentZ < 0 || adjacentZ >= mesh.getDepth()) continue;
					
					Organism neighbour = organisms[mesh.getIndex(adjacentX, adjacentY, adjacentZ)];
					
					switch (organism.getType())
					{
						case Predator :
							predators++;
							updatePredator(organism, neighbour);
							break;
						case Prey :
							prey++;
							updatePrey(organism, neighbour);
							break;
						case Nothing :
							break;
					}
					
					if (organism.isDirty())
					{
						updateOrganism(organism, x, y, z);
						organism.clearDirty();
					}
					
					if (neighbour.isDirty())
					{
						updateOrganism(neighbour, adjacentX, adjacentY, adjacentZ);
						neighbour.clearDirty();
					}
				}
			}
		}
		
		refreshMesh();
	}


	private void updateOrganism(Organism organism, int x, int y, int z)
	{
		if (organism.getType() == Type.Nothing)
		{
			mesh.removeCell(x, y, z);
		}
		else
		{
			if (mesh.hasCell(x, y, z))
				mesh.updateCell((short) 0, organism.getColor(), x, y, z);
			else
				mesh.addCell((short)0, organism.getColor(), x, y, z);
		}
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
		batch.render(renderable);
	}
	
	
	@Override
	public void renderText(BitmapFont font, SpriteBatch batch)
	{
		font.draw(batch, "Predators: " + predators, 10, 20);
		font.draw(batch, "Prey: " + prey, 10, 40);
	}


	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
