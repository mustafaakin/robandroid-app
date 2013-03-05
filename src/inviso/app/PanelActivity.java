package inviso.app;

import inviso.app.NetworkHandler.ConnectionChannel;

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
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PanelActivity extends Activity {
	ConnectionChannel data;
	ConnectionChannel video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);

		final TextView txtServer = (TextView)findViewById(R.id.txtServer); 
		
		SurfaceView camView = new SurfaceView(this);
		SurfaceHolder camHolder = camView.getHolder();
		int width = 640;
		int height = 480;

		final CameraPreview camPreview = new CameraPreview(width, height);
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
						runOnUiThread(new Thread(){
							@Override
							public void run() {
								txtServer.setText("MESSAGE: " + (int)message +  " VALUE: " + (int)value);
							}
						});
					}
				});
			}
		}).start();

	}

	public void stupidButtonClicked(View w) {
		data.send("Hello there: " + System.currentTimeMillis());
	}
}
