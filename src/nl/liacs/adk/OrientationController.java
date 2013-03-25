package nl.liacs.adk;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.content.Context;

public class OrientationController implements IController, SensorEventListener {

	private ISnake snake;
	
	private SensorManager sensorMgr = null;
	private Sensor sensorAccel;
	private Sensor sensorMagnet;
	private Sensor sensorOrient;
	private float[] accelValues = null;
	private float[] magneticValues = null;
	private float[] mRot;
	private float[] orientation;

	public OrientationController()
	{
		snake = null;

	    mRot = new float[9];
	    orientation = new float[3];
	}
	
	private void setRotation(double rotation)
	{
		if (snake != null)
			snake.setRotation(rotation);
		else
			Log.d("ADK.OrientationController", "This should not happen! :P");
	}

	public ISnake getSnake() {
		return snake;
	}

	public void setSnake(ISnake snake) {
		this.snake = snake;
	}

	public void setListeners(View v) {
		Context c = v.getContext();

		sensorMgr = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		int delay = SensorManager.SENSOR_DELAY_GAME;
		
	    sensorAccel = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    boolean accelOK = sensorMgr.registerListener(
	        this, sensorAccel, delay);
	    Log.d("ADK.OrientationController", "Registered acellerometer: " + accelOK);

	    sensorMagnet = sensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    boolean magnetOK = sensorMgr.registerListener(
	        this, sensorMagnet, delay);
		sensorOrient = sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		Log.d("ADK.OrientationController", "Registered magnet-o-meter 3000: " + magnetOK);

	    boolean orientOK = sensorMgr.registerListener(
	        this, sensorOrient, delay);
	    Log.d("ADK.OrientationController", "Registered orientation sensor: " + orientOK);
	}
	
	public void unsetListeners()
	{
		sensorMgr.unregisterListener(this, sensorAccel);
	    sensorMgr.unregisterListener(this, sensorMagnet);
	    sensorMgr.unregisterListener(this, sensorOrient);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
//		Log.d("ADK.OrientationContoller", "Accuracy changed: " + accuracy);
//		WOAH DUDE, SPAMMY
	}

	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType())
		{
		case Sensor.TYPE_ACCELEROMETER:
			accelValues = event.values;
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticValues = event.values;
			break;
		case Sensor.TYPE_ORIENTATION:
			if (accelValues == null || magneticValues == null)
				break;
			if (!SensorManager.getRotationMatrix(mRot, null, accelValues, magneticValues))
				break;
			SensorManager.getOrientation(mRot, orientation);
			double rot = orientation[1]-1.57;
			if (orientation[2] > 0)
				rot = -rot;
			setRotation(rot);
			break;
		}
	}
	
	public void update()
	{
	}
}
