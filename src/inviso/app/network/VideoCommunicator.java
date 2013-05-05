package inviso.app.network;

import java.io.PrintWriter;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;

import android.os.AsyncTask;
import android.util.Base64;


public class VideoCommunicator extends AsyncTask<Void, Void, Void> {
	public Mat toSend;
	private MatOfInt imageEncodeParams = new MatOfInt(Highgui.IMWRITE_JPEG_QUALITY, 80);

	private String host, username, password;
	private int port;

	public VideoCommunicator(String host, String username, String password, int port) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		Channel c = new Channel(host, port, username, password);
		PrintWriter printWriter = c.getOutPrintWriter();
		
		while (c != null && !isCancelled()) {
			if (toSend != null) {
				MatOfByte buff = new MatOfByte();
				Highgui.imencode(".jpg", toSend, buff, imageEncodeParams);
				byte[] imageAsByte = buff.toArray();
				try {
					String encodedImage = Base64.encodeToString(imageAsByte,Base64.DEFAULT);	
					printWriter.write(encodedImage);
					printWriter.write(0);					
					printWriter.flush();
					toSend = null;
				} catch (Exception ex) {
					break;
				}
			}			
		}
		return null;
	}
}
