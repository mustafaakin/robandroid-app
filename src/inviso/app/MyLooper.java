package inviso.app;
import android.util.Log;
import inviso.app.NetworkHandler.ConnectionChannel;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.DigitalOutput.Spec.Mode;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class MyLooper extends BaseIOIOLooper {
	// Motors
	private PwmOutput motor1Speed;
	private PwmOutput motor2Speed;
	private DigitalOutput motor1Direction;
	private DigitalOutput motor2Direction;
	
	// Sensor
	// private AnalogInput analog;
	
	private volatile long lastCommandTime = 0;
	private volatile Direction direction;
	
	private ConnectionChannel dataSend;
	
	enum Direction {
		FORWARD, REVERSE, STOP, LEFT, RIGHT
	}
	
	public void updateCommand(Direction direction) {
		lastCommandTime = System.currentTimeMillis();
		this.direction = direction;
	}

	public MyLooper() {

	}

	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		try {
			// Motor 1
			motor1Speed = ioio_.openPwmOutput(new DigitalOutput.Spec(3,
					Mode.OPEN_DRAIN), 20000);
			motor1Direction = ioio_.openDigitalOutput(4,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, false);

			// Motor 2
			motor2Speed = ioio_.openPwmOutput(new DigitalOutput.Spec(5,
					Mode.OPEN_DRAIN), 20000);
			motor2Direction = ioio_.openDigitalOutput(6,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, false);

			// Sensor
			// analog = ioio_.openAnalogInput(46);
		} catch (ConnectionLostException ex) {
			Log.d("Setup Exception", ex.getMessage());
		}
	}

	@Override
	public void loop() throws ConnectionLostException {
		try {
			long now = System.currentTimeMillis();
			long difference = now - lastCommandTime;
			Log.d("Time Difference", difference + "");
			if (difference < 500) { // under 0.5 second
				float leftSpeed = 0;
				float rightSpeed = 0;
				boolean leftDirection = true;
				boolean rightDirection = true;
				
				if ( direction == Direction.STOP){
					leftSpeed = 0.0f;
					rightSpeed = 0.0f;
				} else if ( direction == Direction.FORWARD){
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = true;
					rightDirection = true;
				} else if ( direction == Direction.LEFT){
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = false;
					rightDirection = true;
				} else if ( direction == Direction.RIGHT){
					leftSpeed = 1.0f;
					rightSpeed =  1.0f;
					leftDirection = true;
					rightDirection = false;
				}  else if ( direction == Direction.REVERSE){
					leftSpeed = 1.0f;
					rightSpeed = 1.0f;
					leftDirection = false;
					rightDirection = false;
				}				
				motor1Speed.setDutyCycle(leftSpeed);
				motor2Speed.setDutyCycle(rightSpeed);
				motor1Direction.write(leftDirection);
				motor2Direction.write(rightDirection);		
				// dataSend.send("Voltage: " + analog.getVoltage());
			} else {
				motor1Speed.setDutyCycle(0);
				motor2Speed.setDutyCycle(0);
			}
			Thread.sleep(10);
		} catch (InterruptedException ie) {
			Log.d("Interrupted Exception", ie.getMessage());
		}
	}
}
