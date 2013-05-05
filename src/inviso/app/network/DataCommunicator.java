package inviso.app.network;

import java.io.BufferedReader;
import android.os.AsyncTask;

public class DataCommunicator extends AsyncTask<Void, Command, Void> {
	private String host, username, password;
	private int port;
	private CommandCallback owner;
	private Channel channel;
	private Thread threadSender;
	private Sender runnSender;
	
	public void send(String msg){
		runnSender.send(msg);
	}
	
	public DataCommunicator(String host, String username, String password, int port, CommandCallback owner) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.port = port;
		this.owner = owner;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		channel = new Channel(host, port, username, password);
		BufferedReader in = channel.getIn();
		
		runnSender = new Sender(channel.getOutPrintWriter());
		threadSender = new Thread(runnSender);
		threadSender.start();
		
		while (channel != null && !isCancelled()) {
			try {
				String str = in.readLine();
				Command cmd = Command.parse(str);
				this.publishProgress(cmd);
			} catch (Exception ex) {
				break;
			}
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		if (channel != null) {
			channel.close();
			runnSender.stopTransmission();
		}
	}

	@Override
	protected void onProgressUpdate(Command... values) {
		// Just using it to be sure that it works on GUI thread for safety.
		owner.callback(values[0]);
	}
}
