package inviso.app;

import java.util.Date;

import inviso.app.network.Command;
import inviso.app.network.CommandCallback;
import inviso.app.network.DataCommunicator;
import inviso.app.network.VideoCommunicator;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class PanelActivity extends IOIOActivity implements CommandCallback {
	private MyIOIOLooper looper;
	
	private boolean isCameraTransmitting = false;
	private boolean isAutonomous = false;
	
	private VideoCommunicator commVideo;
	private DataCommunicator commData;
	
	private Matrix matrix;
	
	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			if (status == LoaderCallbackInterface.SUCCESS) {
				mOpenCvCameraView.enableView();
				Intent intent = getIntent();
				final String host = intent.getStringExtra("server");
				final String user = intent.getStringExtra("user");
				final String pass = intent.getStringExtra("pass");
				
				// Setup the Tasks
				commVideo = new VideoCommunicator(host, user, pass, 5000);
				
				commVideo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				
			} else {
				super.onManagerConnected(status);
			}
		}
	};

	@Override
	protected IOIOLooper createIOIOLooper() {
		looper = new MyIOIOLooper();
		if ( matrix != null){
			matrix.cancel(true);
		}
		if ( isAutonomous){
			matrix = new Matrix(looper);
		}
		// TODO: Also notify the Data Transmission tasks.
		return looper;
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);

		Intent intent = getIntent();
		final String host = intent.getStringExtra("server");
		final String user = intent.getStringExtra("user");
		final String pass = intent.getStringExtra("pass");

		commData = new DataCommunicator(host, user, pass, 6000, this);
		commData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
		mOpenCvCameraView.setCvCameraViewListener(new CvCameraViewListener2() {
			@Override
			public void onCameraViewStopped() {

			}

			@Override
			public void onCameraViewStarted(int width, int height) {

			}

			@Override
			public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
				Mat frame = inputFrame.rgba();
				if (commVideo != null) {
					commVideo.toSend = frame;
				} else {
					Log.d("COMM_VIDEO", "NULL BU AMCA");
				}
				return frame;
			}
		});

	}

	public void btnLogoutClicked(View w) {
		// TODO: Also stop the async tasks & camera gracefully.
		if (commData != null) {
			commData.cancel(true);
			Log.d("CANCELED", commData.isCancelled() ? "true" : "false");
		}
		this.finish();
	}

	public void sendTestMsg(View w){
		commData.send("Hello: " + (new Date()).toString());

	}
	
	@Override
	public void callback(Command c) {
		TextView info = (TextView) findViewById(R.id.informationText);
		info.setText(c.getValue());
	}
}
