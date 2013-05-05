package inviso.app;

import java.util.ArrayList;

import org.opencv.core.Mat;

import inviso.app.image.PositionHandler;
import android.os.AsyncTask;
import android.os.ConditionVariable;

// Responsible for making decisions for autonomous movement.
public class Matrix extends AsyncTask<Void,Void,Void> {
	private MyIOIOLooper looper;
	private boolean stopped = false;
	private PositionHandler positionHandler = null;
	private ConditionVariable lock = new ConditionVariable(false);
	private ArrayList<Mat> images  = new ArrayList<Mat>(3);
	private boolean isAcceptingImage = true;
	
	private int GOAL = GOAL_CHARGING;	
	final static private int GOAL_CHARGING = 0;
	
	final static private int REFERENCE_IMAGE_COUNT_NEED = 3;
	
	public Matrix(MyIOIOLooper looper){
		this.looper = looper;
	}
		
	public void setLooper(MyIOIOLooper looper) {
		this.looper = looper;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		positionHandler = new PositionHandler();
		while ( !stopped){
			lock.block();
			// compute	
			// give command
			// wait until robot moves and stops
			// change is accepting images to true
			// lock until 3 frames given
		}
		return null;
	}
	
	public void supplyImage(Mat image){
		if ( isAcceptingImage){
			images.add(image);
			if ( images.size() == REFERENCE_IMAGE_COUNT_NEED){
				isAcceptingImage = false;
				lock.open();
			}
		} 
	}
	
	
	public void controlMovement(){
		
	}
	
	@Override
	protected void onCancelled() {
		stopped = true;
		lock.open();
	}
}
