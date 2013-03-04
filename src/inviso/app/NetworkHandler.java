package inviso.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class NetworkHandler {
	private final static int WEB_PORT = 3000;

	public static boolean login(String host, String username, String password) {
		HttpClient client = new DefaultHttpClient();
		String url = "http://" + host + ":" + WEB_PORT + "/robotlogin/" + username + "/" + password;
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			boolean loginResult = response.getStatusLine().getStatusCode() == 200;
			return loginResult;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	static class ConnectionChannel {
		public static final int VIDEO_PORT = 5000;
		public static final int DATA_PORT = 6000;
		
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;

		public ConnectionChannel(String host, int port, String username, String password) {
			try {
				socket = new Socket(host, port);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception ex) {
				ex.printStackTrace();
				// Log.e("Network open stream error", ex.getMessage());
			}
		}
		
		class SendThread implements Runnable {
			volatile String msg;

			@Override
			public void run() {

				
				while(true){
					if ( msg != null){
						out.write(msg);
						out.write(0);
						out.flush();
						msg = null;
					}
				}
			}
		}
		SendThread sender;
		
		public synchronized void send(String msg){
			if ( sender == null){
				sender = new SendThread();
				new Thread(sender).start();
			}
			sender.msg = msg;
		}
		
		public synchronized char[] recieveChars(int count){
			char[] data = new char[count];
			try {
				in.read(data, 0, count);
			} catch (IOException e) {
				return null;
			}
			return data;
		}	
	}
}
