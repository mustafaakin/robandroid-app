package inviso.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback {
	int PreviewSizeWidth;
	int PreviewSizeHeight;
	SurfaceHolder mSurfHolder;
	Camera mCamera;
	int cameraId = -1;
	
	public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		cameraId = info.facing;
		
		PreviewSizeWidth = PreviewlayoutWidth;
		PreviewSizeHeight = PreviewlayoutHeight;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Parameters p = camera.getParameters();
		int width = p.getPreviewSize().width;
		int height = p.getPreviewSize().height;

		// TODO: Add also split string and headers here
		ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		Rect rect = new Rect(0, 0, width, height);
		YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
		yuvimage.compressToJpeg(rect, 80, outstr);
		// String encodedImage = Base64.encodeToString(outstr.toByteArray(),
		// Base64.DEFAULT);


	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Parameters parameters;
		mSurfHolder = arg0;

		parameters = mCamera.getParameters();
		parameters.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);

		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		mCamera = Camera.open(cameraId);
		try {
			// If did not set the SurfaceHolder, the preview area will be black.
			mCamera.setPreviewDisplay(arg0);
			mCamera.setPreviewCallback(this);
			Parameters p = mCamera.getParameters();
			p.setPreviewSize(PreviewSizeWidth, PreviewSizeHeight);
			mCamera.setParameters(p);
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
}