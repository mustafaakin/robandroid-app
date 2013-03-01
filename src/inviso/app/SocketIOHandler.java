package inviso.app;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;

public class SocketIOHandler {
	private final static int SOCKET_IO_PORT = 9000;
	SocketIOClient client;

	public SocketIOHandler(String host, final String user, final String password) {
		Log.d("CreateSOCK", "Called");
		client = new SocketIOClient(URI.create("http://" + host + ":" + SOCKET_IO_PORT), new SocketIOClient.Handler() {
			@Override
			public void onConnect() {
				try {
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

	public void sendVideo(byte[] data){
		
	}
	
}
