package mondegogroup.ics.uci.mingming.research.spaceshipgame;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class SensorDataManager extends Service implements SensorEventListener{
	private final IBinder mBinder = new MyBinder();
	
	private SensorManager mSensorManager = null;
	
    // accelerometer vector
    private float[] accel = new float[3];
    
    //gyro vector
    private float[] gyro = new float[3];
	
    public float[] angle = new float[3];
    
    public float[] deltaAngle = new float[3];
    
    public float[] smoothedValues;
    
       
    public float[] acce = new float[3];
    public float[] deltaAcc = new float[3];
    private long timestamp2 = 0;
    
    private long timestamp = 0;
    private static final float NS2S = 1.0f / 1000000000.0f;
    
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public class MyBinder extends Binder {
		SensorDataManager getSensorDataManager()
		{
			return SensorDataManager.this;
		}
	}
    
    
    @Override
    public void onCreate() {
        super.onCreate();
 
        // get sensorManager and initialize sensor listeners
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();

    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSensorManager.unregisterListener(this);
	}
	
    // This function registers sensor listeners for the accelerometer, magnetometer and gyroscope.
    public void initListeners(){
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST);
     
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_FASTEST);
     
        mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch(event.sensor.getType()) {
		    case Sensor.TYPE_ACCELEROMETER:
		        // copy new accelerometer data into accel array and calculate orientation
		        //System.arraycopy(event.values, 0, accel, 0, 3);  
		        if(timestamp2 != 0)
		        {
		        	final float dT = (event.timestamp - timestamp2) * NS2S;
		        	float[] smoothedValues = smoothAcc(event.values);
		            deltaAcc[0] = smoothedValues[0] * dT;
		            deltaAcc[1] = smoothedValues[1] * dT;
		            deltaAcc[2] = smoothedValues[2] * dT;
		            
		            acce[0] += deltaAcc[0];
		            acce[1] += deltaAcc[1];
		            acce[2] += deltaAcc[2];
		        }
		        timestamp2 = event.timestamp;
		        break;
		    case Sensor.TYPE_GYROSCOPE:
		        // copy new gyroscope data into gryo array and calculate orientation
		        //System.arraycopy(event.values, 0, gyro, 0, 3); 
		        if(timestamp != 0)
		        {
		        	final float dT = (event.timestamp - timestamp) * NS2S;
		        	float[] smoothedValues = smoothGyro(event.values);

		            deltaAngle[0] = smoothedValues[0] * dT;
		            deltaAngle[1] = smoothedValues[1] * dT;
		            deltaAngle[2] = smoothedValues[2] * dT;
		            
		        	angle[0] += deltaAngle[0];
		            angle[1] += deltaAngle[1];
		            angle[2] += deltaAngle[2];
		        }
		        timestamp = event.timestamp;
		        break;
	        default:
	        	break;
		}
		
	}
	
	int counter = 0;
	final int bufferSize = 3;
	float[] smoothedX = new float[3];
	float[] smoothedY = new float[3];
	float[] smoothedZ = new float[3];
	public float[] smoothAcc(float[] data)
	{
		smoothedX[counter] = data[0];
		smoothedY[counter] = data[1];
		smoothedZ[counter] = data[2];
		counter ++;
		float[] smoothedData = new float[bufferSize];
		if(counter >= bufferSize)
		{
			for(int i = 0; i < bufferSize; i++)
			{
				smoothedData[0] += smoothedX[i];
				smoothedData[1] += smoothedY[i];
				smoothedData[2] += smoothedZ[i];
			}
			smoothedData[0] /= bufferSize;
			smoothedData[1] /= bufferSize;
			smoothedData[2] /= bufferSize;
			
			counter %= bufferSize;
		}
		else
		{
			for(int i = 0; i < counter; i++)
			{
				smoothedData[0] += smoothedX[i];
				smoothedData[1] += smoothedY[i];
				smoothedData[2] += smoothedZ[i];
			}
			smoothedData[0] /= counter;
			smoothedData[1] /= counter;
			smoothedData[2] /= counter;
		}
		return smoothedData;
	}
	
	
	int counterGyro = 0;
	final int bufferSizeGyro = 3;
	float[] smoothedXGyro = new float[3];
	float[] smoothedYGyro = new float[3];
	float[] smoothedZGyro = new float[3];
	public float[] smoothGyro(float[] data)
	{
		smoothedXGyro[counter] = data[0];
		smoothedYGyro[counter] = data[1];
		smoothedZGyro[counter] = data[2];
		counterGyro ++;
		float[] smoothedData = new float[bufferSizeGyro];
		if(counterGyro >= bufferSizeGyro)
		{
			for(int i = 0; i < bufferSizeGyro; i++)
			{
				smoothedData[0] += smoothedXGyro[i];
				smoothedData[1] += smoothedYGyro[i];
				smoothedData[2] += smoothedZGyro[i];
			}
			smoothedData[0] /= bufferSizeGyro;
			smoothedData[1] /= bufferSizeGyro;
			smoothedData[2] /= bufferSizeGyro;
			
			counterGyro %= bufferSizeGyro;
		}
		else
		{
			for(int i = 0; i < counterGyro; i++)
			{
				smoothedData[0] += smoothedXGyro[i];
				smoothedData[1] += smoothedYGyro[i];
				smoothedData[2] += smoothedZGyro[i];
			}
			smoothedData[0] /= counterGyro;
			smoothedData[1] /= counterGyro;
			smoothedData[2] /= counterGyro;
		}
		/*
		for(int i = 0; i < 3; i++)
		{
			if(smoothedData[i] < 10 * Math.PI/180)
			{
				smoothedData[i] = 0.0f;
			}
		}
		*/
		return smoothedData;
	}
	
	public float[] getSmoothGyroData()
	{
		return smoothedValues; 
	}
	
	public float[] getAngleData()
	{
		return angle;
	}
	
	public float[] getDeltaAngle()
	{
		return deltaAngle;
	}
	
	public float[] getDeltaAcc()
	{
		return deltaAcc;
	}
}
