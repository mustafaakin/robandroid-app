package inviso.app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	private final static int SOCKET_IO_PORT = 9000;

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

	private static String readResponse(HttpResponse response) throws IOException {
		InputStream in = response.getEntity().getContent();
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	SocketIOClient client;

	public void createSocketIOClient(String host, final String user, final String password) {
		Log.d("CreateSOCK", "Called");
		client = new SocketIOClient(URI.create("http://" + host + ":" + SOCKET_IO_PORT), new SocketIOClient.Handler() {
			@Override
			public void onConnect() {
				try {
					Log.d("O", "Called");
					JSONObject message = new JSONObject();
					message.put("username", user);
					message.put("password", password);
					
					JSONArray args = new JSONArray();
					args.put(message);
					client.emit("login", args);
				} catch (JSONException ex) {
					Log.d("JSON EXCEPTION", ex.getMessage());
				}
			}

			@Override
			public void on(String event, JSONArray arguments) {

			}

			@Override
			public void onDisconnect(int code, String reason) {

			}

			@Override
			public void onError(Exception error) {

			}
		});
		client.connect();
	}
}
