package komposten.automata.predatorprey;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.RandomXS128;

import komposten.automata.Automaton;
import komposten.automata.backend.ShaderFactory;
import komposten.automata.backend.rendering.GridPixmap;
import komposten.automata.backend.rendering.Quad;
import komposten.automata.predatorprey.Organism.Type;

public class PixPredatorPrey extends Automaton
{
	private static final int CELL_SIZE = 1;

	private OrthographicCamera camera;
	private Quad quad;
	private GridPixmap mesh;
	private ShaderProgram shader;
	
	private Organism[] organisms;
	private RandomXS128 random;
	
	
	
	public PixPredatorPrey(int width, int height, OrthographicCamera orthographicCamera)
	{
		super("PixPredatorPrey");
		camera = orthographicCamera;
		mesh = new GridPixmap(width, height, CELL_SIZE);
		shader = ShaderFactory.getShader(ShaderFactory.DEFAULT_TEXTURE);
		random = new RandomXS128();
		
		organisms = new Organism[mesh.getCellCount()];
		quad = new Quad(width, height, true);

		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.end();
		
		createStartingGrid();
	}
	
	
	@Override
	protected void createStartingGrid()
	{
		for (int i = 0; i < organisms.length; i++)
		{
			Organism organism = new Organism(getRandomType());
			organisms[i] = organism;
			mesh.setColor(organism.getColor(), i);
		}
		
		mesh.refreshTexture();
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
		for (int r = 0; r < mesh.getRowCount(); r++)
		{
			for (int c = 0; c < mesh.getColumnCount(); c++)
			{
				int index = mesh.getIndex(r, c);
				
				Organism organism = organisms[index];

				int adjacentR = r + random.nextInt(3) - 1;
				int adjacentC = c + random.nextInt(3) - 1;
				
				if (adjacentR < 0 || adjacentR >= mesh.getRowCount()) continue;
				if (adjacentC < 0 || adjacentC >= mesh.getColumnCount()) continue;
				
				Organism neighbour = organisms[mesh.getIndex(adjacentR, adjacentC)];
				
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
					mesh.setColor(organism.getColor(), r, c);
					organism.clearDirty();
				}
				
				if (neighbour.isDirty())
				{
					mesh.setColor(neighbour.getColor(), adjacentR, adjacentC);
					neighbour.clearDirty();
				}
			}
		}
		
		mesh.refreshTexture();
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
		mesh.getTexture().bind(0);
		quad.render(shader, GL30.GL_TRIANGLES);
		shader.end();
	}
	
	
	@Override
	public void renderText(BitmapFont font, SpriteBatch batch)
	{
	}


	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
