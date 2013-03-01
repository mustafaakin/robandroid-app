package inviso.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class PanelActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);

		SurfaceView camView = new SurfaceView(this);
		SurfaceHolder camHolder = camView.getHolder();
		int width = 352;
		int height = 288;

		CameraPreview camPreview = new CameraPreview(width, height);

		camHolder.addCallback(camPreview);

		FrameLayout mainLayout = (FrameLayout) findViewById(R.id.videoview);
		mainLayout.addView(camView, new LayoutParams(width, height));

	}
}
