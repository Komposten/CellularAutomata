package komposten.automata.simpleevolution;

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
import komposten.automata.simpleevolution.EvolvingOrganism.Type;

public class SimpleEvolution extends Automaton
{
	private static final int CELL_SIZE = 2;
	
	private OrthographicCamera camera;
	private Quad quad;
	private GridPixmap mesh;
	private ShaderProgram shader;
	
	private EvolvingOrganism[] cells;
	private RandomXS128 random;
	
	private int living;

	
	public SimpleEvolution(int width, int height, OrthographicCamera orthographicCamera)
	{
		super("SimpleEvolution");
		camera = orthographicCamera;
		mesh = new GridPixmap(width, height, CELL_SIZE);
		shader = ShaderFactory.getShader(ShaderFactory.DEFAULT_TEXTURE);
		random = new RandomXS128();
		
		cells = new EvolvingOrganism[mesh.getCellCount()];
		quad = new Quad(width, height, true);

		shader.begin();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.end();
		
		createStartingGrid();
	}
	
	
	@Override
	protected void createStartingGrid()
	{
		for (int i = 0; i < cells.length; i++)
		{
			EvolvingOrganism organism = new EvolvingOrganism(getRandomType());
			cells[i] = organism;
			mesh.setColor(organism.getColor(), i);
		}
		
		mesh.refreshTexture();
	}


	private EvolvingOrganism.Type getRandomType()
	{
		int value = random.nextInt(1000);
		
		if (value < 100)
			return Type.Alive;
		else
			return Type.Dead;
	}


	@Override
	public void update()
	{
		living = 0;
		for (int r = 0; r < mesh.getRowCount(); r++)
		{
			for (int c = 0; c < mesh.getColumnCount(); c++)
			{
				int index = mesh.getIndex(r, c);

				int adjacentR = r + random.nextInt(3) - 1;
				int adjacentC = c + random.nextInt(3) - 1;
				
				if (adjacentR < 0 || adjacentR >= mesh.getRowCount()) continue;
				if (adjacentC < 0 || adjacentC >= mesh.getColumnCount()) continue;
				
				EvolvingOrganism organism = cells[index];
				EvolvingOrganism neighbour = cells[mesh.getIndex(adjacentR, adjacentC)];
				
				switch (organism.getType())
				{
					case Alive :
						living++;
						updateOrganism(organism, r, c, neighbour, adjacentR, adjacentC);
						break;
					case Dead :
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
	
	
	private void updateOrganism(EvolvingOrganism organism, int row, int column, EvolvingOrganism neighbour, int row2, int column2)
	{
		switch (neighbour.getType())
		{
			case Alive :
				if (organism.canReproduce() && neighbour.canReproduce())
				{
					if (organism.canReproduceWith(neighbour))
					{
						int index = findDeadAround(row, column);
						if (index < 0)
						{
							index = findDeadAround(row2, column2);
						}
						
						if (index >= 0)
						{
							EvolvingOrganism target = cells[index];
							organism.reproduceTo(target, neighbour);
							
							mesh.setColor(target.getColor(), index);
							target.clearDirty();
						}
						
						organism.changeHealth(-2);
					}
					else
					{
						organism.changeHealth(-5000);
					}
				}
				
				break;
			case Dead :
				organism.moveTo(neighbour);
				break;
		}
		
		organism.timePassed();
	}


	private int findDeadAround(int row, int column)
	{
		for (int r = row-1; r <= row+1; r++)
		{
			for (int c = column-1; c <= column+1; c++)
			{
				if (r < 0 || r >= mesh.getRowCount()) continue;
				if (c < 0 || c >= mesh.getColumnCount()) continue;
				
				int index = mesh.getIndex(r, c);
				if (cells[index].getType() == Type.Dead)
				{
					return index;
				}
			}
		}
		
		return -1;
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
		font.draw(batch, "Alive: " + living, 10, 20);
	}


	@Override
	public void dispose()
	{
		mesh.dispose();
	}
}
