package inviso.app.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Channel {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private OutputStream os;

	public Channel(String host, int port, String username, String password) {
		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = socket.getOutputStream();
		} catch (Exception ex) {
			ex.printStackTrace();
			// Log.e("Network open stream error", ex.getMessage());
		}
	}

	public BufferedReader getIn() {
		return in;
	}

	public OutputStream getOutStream(){
		return os;
	}
		
	public PrintWriter getOutPrintWriter() {
		return out;
	}

	public void close() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception ex) {

		}
	}
}
