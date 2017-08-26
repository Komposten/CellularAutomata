package komposten.automata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;


public class CameraInputReader
{
	private Camera camera;
	private Vector3 calculationVector = new Vector3();
	private float sensitivity;


	public CameraInputReader(Camera camera)
	{
		this(camera, 3f);
	}


	public CameraInputReader(Camera camera, float mouseSensitivity)
	{
		this.camera = camera;
		sensitivity = mouseSensitivity;
	}


	public void readInput()
	{
		boolean update = false;
		if (readKeyboard())
			update = true;
		if (readMouse())
			update = true;

		if (update)
		{
			// camera.lookAt(mesh.getColumnCount()/2*mesh.getCellSize(),
			// mesh.getRowCount()/2*mesh.getCellSize(),
			// mesh.getLayerCount()/2*mesh.getCellSize());
			// camera.lookAt(mesh2.getWidth()/2*mesh2.getCellSize(),
			// mesh2.getHeight()/2*mesh2.getCellSize(),
			// mesh2.getDepth()/2*mesh2.getCellSize());
			// camera.lookAt(25, 25, 25);
			camera.update();
		}
	}


	private boolean readKeyboard()
	{
		boolean needsCameraUpdate = false;
		if (Gdx.input.isKeyPressed(Input.Keys.W))
		{
			calculationVector.set(camera.direction.x, 0, camera.direction.z).nor();
			camera.translate(calculationVector);
			needsCameraUpdate = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S))
		{
			calculationVector.set(-camera.direction.x, 0, -camera.direction.z).nor();
			camera.translate(calculationVector);
			needsCameraUpdate = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D))
		{
			calculationVector.set(-camera.direction.z, 0, camera.direction.x).nor();
			camera.translate(calculationVector);
			needsCameraUpdate = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A))
		{
			calculationVector.set(camera.direction.z, 0, -camera.direction.x).nor();
			camera.translate(calculationVector);
			needsCameraUpdate = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
		{
			camera.translate(0, 1, 0);
			needsCameraUpdate = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
		{
			camera.translate(0, -1, 0);
			needsCameraUpdate = true;
		}

		return needsCameraUpdate;
	}


	private boolean readMouse()
	{
		int deltaX = Gdx.input.getDeltaX();
		int deltaY = Gdx.input.getDeltaY();

		if (deltaX != 0 || deltaY != 0)
		{
			float rotationAngleX = -deltaX / (100 / sensitivity);
			double rotationAngleY = deltaY / (100 / sensitivity);

			// Rotate left/right
			camera.rotate(Vector3.Y, rotationAngleX);

			// Rotate up/down
			calculationVector.set(camera.direction);
			calculationVector.y = 0;
			double currentAngle = Math.toDegrees(Math.atan2(
					calculationVector.y - camera.direction.y, calculationVector.len()));
			double maxAngle = 89;
			if (currentAngle + rotationAngleY >= maxAngle)
			{
				rotationAngleY = maxAngle - currentAngle;
			}
			else if (currentAngle + rotationAngleY <= -maxAngle)
			{
				rotationAngleY = -maxAngle - currentAngle;
			}

			calculationVector.set(camera.direction.z, 0, -camera.direction.x);
			camera.rotate(calculationVector, (float) rotationAngleY);

			return true;
		}

		return false;
	}
}
