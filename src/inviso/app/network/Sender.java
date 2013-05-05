package inviso.app.network;

import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

public class Sender implements Runnable {
	private PrintWriter out;
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(500);
	private boolean isStopped = false;

	public Sender(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void run() {
		Log.d("SENDER THREAD", "STARTED");
		while (!isStopped) {
			try {
				String msg = queue.take();
				out.println(msg);
				out.flush();
			} catch (Exception ex) {
				
			}
		}
	}

	public void stopTransmission(){
		isStopped = true;
	}
	
	public void send(String msg) {
		queue.add(msg);
	}
}