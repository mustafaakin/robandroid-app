package inviso.app;

import android.util.Log;
import inviso.app.network.Command.CommandType;
import inviso.app.network.DataCommunicator;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class MyIOIOLooper extends BaseIOIOLooper {

	// Motors
	private PwmOutput m1_speed;
	private PwmOutput m2_speed;

	private DigitalOutput m1_dir1, m1_dir2;
	private DigitalOutput m2_dir1, m2_dir2;

	// Temp & Gas sensors
	private AnalogInput tempratuere, gas;
	private float[] buffTemp = new float[100], buffGas = new float[100];
	private int buffTempIdx = 0, buffGasIdx = 0;
	

	// Distance sensors
	private DigitalInput dist1, dist2;

	private volatile long lastCommandExpireTime = 0;
	private volatile CommandType direction;
	
	// Data Communicator
	private DataCommunicator commData;

	public void updateCommand(CommandType direction, long time) {
		lastCommandExpireTime = System.currentTimeMillis() + time;
		this.direction = direction;
	}

	public void stop() {
		lastCommandExpireTime = 0;
		direction = CommandType.STOP;
	}

	public MyIOIOLooper() {

	}
	
	void setDataComm(DataCommunicator commData){
		this.commData = commData;
	}

	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		try {
			// Motor 1
			m1_speed = ioio_.openPwmOutput(new DigitalOutput.Spec(3, Mode.OPEN_DRAIN), 20000);
			m1_dir1 = ioio_.openDigitalOutput(5, DigitalOutput.Spec.Mode.OPEN_DRAIN, false);
			m1_dir2 = ioio_.openDigitalOutput(6, DigitalOutput.Spec.Mode.OPEN_DRAIN, false);

			// Motor 2
			m1_speed = ioio_.openPwmOutput(new DigitalOutput.Spec(4, Mode.OPEN_DRAIN), 20000);
			m1_dir1 = ioio_.openDigitalOutput(22, DigitalOutput.Spec.Mode.OPEN_DRAIN, false);
			m1_dir2 = ioio_.openDigitalOutput(23, DigitalOutput.Spec.Mode.OPEN_DRAIN, false);

			// Sensor
			tempratuere = ioio_.openAnalogInput(33);
			gas = ioio_.openAnalogInput(34);
			
			// analog = ioio_.openAnalogInput(46);
			dist1 = ioio_.openDigitalInput(24, DigitalInput.Spec.Mode.PULL_UP);
			dist2 = ioio_.openDigitalInput(25, DigitalInput.Spec.Mode.PULL_UP);
		} catch (ConnectionLostException ex) {
			Log.d("Setup Exception", ex.getMessage());
		}
	}

	
	@Override
	public void loop() throws ConnectionLostException {
		try {
			long now = System.currentTimeMillis();
			if (now <= lastCommandExpireTime) { // under 0.5 second
				float leftSpeed = 0;
				float rightSpeed = 0;
				boolean leftDirection = true;
				boolean rightDirection = true;

				if (direction == CommandType.STOP) {
					leftSpeed = 0.0f;
					rightSpeed = 0.0f;
				} else if (direction == CommandType.FORWARD) {
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = true;
					rightDirection = true;
				} else if (direction == CommandType.LEFT) {
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = false;
					rightDirection = true;
				} else if (direction == CommandType.RIGHT) {
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = true;
					rightDirection = false;
				} else if (direction == CommandType.REVERSE) {
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = false;
					rightDirection = false;
				}
				
				m1_speed.setDutyCycle(leftSpeed);
				m2_speed.setDutyCycle(rightSpeed);
				
				m1_dir1.write(leftDirection);
				m1_dir2.write(!leftDirection);

				m2_dir1.write(rightDirection);
				m2_dir2.write(!rightDirection);
			} else {
				m1_speed.setDutyCycle(0);
				m2_speed.setDutyCycle(0);
			}
			
			buffTemp[buffTempIdx] = tempratuere.read();
			buffGas[buffGasIdx] = gas.read();
			
			buffTempIdx = (buffTempIdx + 1) % buffTemp.length;
			buffGasIdx = (buffGasIdx + 1) % buffGas.length;		
			
			
			if ( commData != null){				
				if ( buffTempIdx == buffTemp.length / 2){
					float avgTemp = 0, avgGas = 0;
					
					for(int i = 0; i < buffTemp.length; i++){
						avgTemp += buffTemp[i];
					}
					
					for(int i = 0; i < buffGas.length; i++){
						avgGas += buffGas[i];
					}
					
					avgTemp = avgTemp / buffTemp.length;
					avgGas = avgGas / buffGas.length;
					
					commData.send("TEMP: " + avgTemp);					
					commData.send("GAS: " + avgGas);
				} 
			}
			
			Thread.sleep(10);
		} catch (InterruptedException ie) {
			Log.d("Interrupted Exception", ie.getMessage());
		}
	}

}
