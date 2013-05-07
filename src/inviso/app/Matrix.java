package inviso.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import inviso.app.image.Position;
import inviso.app.image.PositionHandler;
import inviso.app.image.Scene;
import inviso.app.image.SceneDetectData;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

// Responsible for making decisions for autonomous movement.
public class Matrix extends AsyncTask<Void, String, Void> {
	final static private int GOAL_CHARGING = 0;

	private MyIOIOLooper looper;
	private boolean stopped = false;
	private PositionHandler posHandler = null;
	private ConditionVariable lock = new ConditionVariable(false);

	public ArrayList<Position> positions = new ArrayList<Position>();
	public int currentPosition = 0;
	public long chargeStartTime = 0;		
	private Mat lastImage = null;
	private int GOAL = GOAL_CHARGING;

	public Matrix(MyIOIOLooper looper) {
		this.looper = looper;
	}

	public void setLooper(MyIOIOLooper looper) {
		this.looper = looper;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		posHandler = new PositionHandler();
		publishProgress("Reading scenes");
		readFiles();
		publishProgress("Read scenes");
		Log.d("Matrix", "Find Position");
		publishProgress("Read values");


		while (!stopped) {
			try {
				// Stop
				// Find position
				// Find next action
				// while(complete) (perform turn/move;stop)											
				
				publishProgress("Waiting frame");
				while (lastImage == null);								
				publishProgress("Finding place");
				currentPosition = findPlace(lastImage).idx;

				lastImage = null;
				// 2. Set goal to charging dock
				GOAL = GOAL_CHARGING;
				// 3. Find the closest direction do dock & set it as goal node
				int dockIdx = closestDock();
				if ( currentPosition < dockIdx){
					publishProgress(currentPosition + ",F," + positions.get(currentPosition).angle);					
				}  else {
					publishProgress(currentPosition + ",R," + positions.get(currentPosition-1).angle);										
				}								
				lastImage = null;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				// Like I give a damn
			}
			// If angle is zero, issue move forward command for 2 secs
			// If angle is not zero;
			// Start turning left/right for 0.3 secs, check if completed
		}
		return null;
	}

	public TextView upd8;

	@Override
	protected void onProgressUpdate(String... values) {
		if (upd8 != null) {
			upd8.setText(values[0]);
		}
	}

	public void supplyImage(Mat image) {
		if (lastImage == null) {
			this.lastImage = image.clone();
		}
	}

	@Override
	protected void onCancelled() {
		stopped = true;
		lock.open();
	}

	public SceneDetectData findPlace(Mat frame) {
		SceneDetectData max = null;
		int maxValue = -1;
		Scene referenceScene = new Scene(frame);
		for (int i = 0; i < positions.size(); i++) {
			Scene scene = positions.get(i).scene;
			SceneDetectData data = referenceScene.compare(scene, false);
			if (data.dist_matches > maxValue) {
				max = data;
				maxValue = data.dist_matches;
				max.idx = i;
			}
		}
		return max;
	}

	public int closestDock() {
		int length = positions.size();
		for (int i = 0; i < length; i++) {
			int back = currentPosition - i;
			int forward = currentPosition + i;
			if (forward >= 0 && forward < length) {
				if (positions.get(forward).isChargingDock) {
					return forward;
				}
			}
			if (back >= 0 && back < length) {
				if (positions.get(back).isChargingDock) {
					return back;
				}
			}
		}
		return -1;
	}

	public void readFiles() {
		File pathInfo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		String filenameInfo = "infoFile.txt";
		File fileInfo = new File(pathInfo, filenameInfo);
		if (fileInfo.exists()) {
			try {
				Scanner scan = new Scanner(fileInfo);
				positions.clear();

				while (scan.hasNextLine()) {
					String[] txt = scan.nextLine().split("\t");
					String filename = txt[0];
					double angle = Double.parseDouble(txt[1]);
					boolean isChargingDock = Integer.parseInt(txt[2]) == 0 ? false : true;

					File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
					File file = new File(path, filename);

					Mat mat = Highgui.imread(file.getAbsolutePath());
					if (!mat.empty()) {
						Scene scene = new Scene(mat);
						Position pos = new Position(scene, isChargingDock, angle);
						positions.add(pos);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
