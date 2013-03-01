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

}
