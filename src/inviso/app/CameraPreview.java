package inviso.app;

import inviso.app.NetworkHandler.ConnectionChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;

import org.json.JSONArray;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback {
	int PreviewSizeWidth;
	int PreviewSizeHeight;
	SurfaceHolder mSurfHolder;
	Camera mCamera;
	int cameraId = -1;
	long previous = 0;
	ConnectionChannel channel; 
	
	public void stopCamera(){
		mCamera.stopPreview();
	}
	
	public void startCamera(){
		mCamera.startPreview();
	}
	
	
	public void setChannel(ConnectionChannel channel) {
		this.channel = channel;
	}
	
	public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		cameraId = info.facing;
		
		PreviewSizeWidth = PreviewlayoutWidth;
		PreviewSizeHeight = PreviewlayoutHeight;
	}

	
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if ( (System.currentTimeMillis() - previous) > 1000/25 &&  channel != null){
			previous = System.currentTimeMillis();

			int width  = 640;
			int height = 480;
								
			ByteArrayOutputStream outstr = new ByteArrayOutputStream();
			Rect rect = new Rect(0, 0, width, height);
			
			YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);	
			yuvimage.compressToJpeg(rect, 50, outstr);		
			String encodedImage = Base64.encodeToString(outstr.toByteArray(),Base64.DEFAULT);			
			channel.send(encodedImage);			
		}
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