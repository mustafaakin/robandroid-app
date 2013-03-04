package inviso.app;

import org.json.JSONArray;

public class MessagePack {
	String channel;
	JSONArray message;

	public MessagePack(String channel, JSONArray message) {
		this.channel = channel;
		this.message = message;
	}
}
