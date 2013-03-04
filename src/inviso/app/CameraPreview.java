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
		if ( (System.currentTimeMillis() - previous) > 1000/30 &&  channel != null){
			previous = System.currentTimeMillis();

			int width  = 640;
			int height = 480;
			
			long t1 = System.currentTimeMillis();
			
			ByteArrayOutputStream outstr = new ByteArrayOutputStream();
			Rect rect = new Rect(0, 0, width, height);
			Log.d("DATA SIZE:", data.length +"");
			
			YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
			
			
			long t2 = System.currentTimeMillis();

			yuvimage.compressToJpeg(rect, 50, outstr);
			long t3 = System.currentTimeMillis();

			String encodedImage = Base64.encodeToString(outstr.toByteArray(),Base64.DEFAULT);
			long t4 = System.currentTimeMillis();



			Log.d("TIMES - ENCODEE", "" + (t4-t3));
			Log.d("TIMES - COMPRRE", "" + (t3-t2));
			Log.d("TIMES - YUVIMAG", "" + (t2-t1));
			
			
			channel.send(encodedImage);			
		}
	}
	
	/**
	 * Converts YUV420 NV21 to RGB8888
	 * 
	 * @param data byte array on YUV420 NV21 format.
	 * @param width pixels width
	 * @param height pixels height
	 * @return a RGB8888 pixels int array. Where each int is a pixels ARGB. 
	 */
	public static int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height) {
	    int size = width*height;
	    int offset = size;
	    int[] pixels = new int[size];
	    int u, v, y1, y2, y3, y4;

	    // i percorre os Y and the final pixels
	    // k percorre os pixles U e V
	    for(int i=0, k=0; i < size; i+=2, k+=2) {
	        y1 = data[i  ]&0xff;
	        y2 = data[i+1]&0xff;
	        y3 = data[width+i  ]&0xff;
	        y4 = data[width+i+1]&0xff;

	        u = data[offset+k  ]&0xff;
	        v = data[offset+k+1]&0xff;
	        u = u-128;
	        v = v-128;

	        pixels[i  ] = convertYUVtoRGB(y1, u, v);
	        pixels[i+1] = convertYUVtoRGB(y2, u, v);
	        pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
	        pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

	        if (i!=0 && (i+2)%width==0)
	            i+=width;
	    }

	    return pixels;
	}

	private static int convertYUVtoRGB(int y, int u, int v) {
	    int r,g,b;

	    r = y + (int)1.402f*v;
	    g = y - (int)(0.344f*u +0.714f*v);
	    b = y + (int)1.772f*u;
	    r = r>255? 255 : r<0 ? 0 : r;
	    g = g>255? 255 : g<0 ? 0 : g;
	    b = b>255? 255 : b<0 ? 0 : b;
	    return 0xff000000 | (b<<16) | (g<<8) | r;
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