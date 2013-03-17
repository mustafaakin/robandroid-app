package inviso.app;

import inviso.app.MyLooper.Direction;
import inviso.app.NetworkHandler.ConnectionChannel;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PanelActivity extends IOIOActivity {
	ConnectionChannel data;
	ConnectionChannel video;

	MyLooper looper;
	CameraPreview camPreview;

	@Override
	protected IOIOLooper createIOIOLooper() {
		looper = new MyLooper();
		return looper;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);

		final TextView txtServer = (TextView) findViewById(R.id.txtServer);

		SurfaceView camView = new SurfaceView(this);
		final SurfaceHolder camHolder = camView.getHolder();
		final int width = 640;
		final int height = 480;

		camPreview = new CameraPreview(width, height);
		camHolder.addCallback(camPreview);

		FrameLayout mainLayout = (FrameLayout) findViewById(R.id.videoview);
		mainLayout.addView(camView, new LayoutParams(width, height));

		Intent intent = getIntent();
		final String server = intent.getStringExtra("server");
		final String user = intent.getStringExtra("user");
		final String pass = intent.getStringExtra("pass");

		new Thread(new Runnable() {
			@Override
			public void run() {
				data = new ConnectionChannel(server, ConnectionChannel.DATA_PORT, user, pass);
				video = new ConnectionChannel(server, ConnectionChannel.VIDEO_PORT, user, pass);
				camPreview.setChannel(video);

				data.setCallback(new MessageCallback() {
					@Override
					public void callback(final char message, final char value) {
						Log.d("Message Callback", (int) message + ", " + (int) value);

						if (camPreview != null) {
							if (message == 30) {
								camPreview.startCamera();
								camHolder.addCallback(camPreview);
							}
							if (message == 31) {
								camPreview.stopCamera();
								camHolder.removeCallback(camPreview);
							}
						}

						if (looper != null) {
							if (message == 10)
								looper.updateCommand(Direction.FORWARD);
							if (message == 11)
								looper.updateCommand(Direction.REVERSE);
							if (message == 12)
								looper.updateCommand(Direction.LEFT);
							if (message == 13)
								looper.updateCommand(Direction.RIGHT);
							if (message == 14)
								looper.updateCommand(Direction.STOP);
						}

						runOnUiThread(new Thread() {
							@Override
							public void run() {
								txtServer.setText("MESSAGE: " + (int) message + " VALUE: " + (int) value);
							}
						});
					}
				});
			}
		}).start();

	}

	public void btnLogoutClicked(View w) {
		this.finish();
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
