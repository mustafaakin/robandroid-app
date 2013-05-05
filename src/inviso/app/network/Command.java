package inviso.app.network;

import java.util.HashMap;

public class Command {
	public enum CommandType {
		FORWARD, REVERSE, LEFT, RIGHT, SERVO_UP, SERVO_DOWN, CAMERA_STOP, CAMERA_START, AUTONOMOUS_STOP, AUTONOMOUS_START, UNKNOWN
	}

	CommandType type;
	String value;

	public Command(CommandType type, String value) {
		this.type = type;
		this.value = value;
	}

	public static Command parse(String str) {
		HashMap<String, CommandType> map = new HashMap<String, CommandType>();
		map.put("FORWARD", CommandType.FORWARD);
		map.put("REVERSE", CommandType.REVERSE);
		map.put("LEFT", CommandType.LEFT);
		map.put("RIGHT", CommandType.RIGHT);
		map.put("SERVO_UP", CommandType.SERVO_UP);
		map.put("SERVO_DOWN", CommandType.SERVO_DOWN);
		map.put("CAMERA_STOP", CommandType.CAMERA_STOP);
		map.put("CAMERA_START", CommandType.CAMERA_START);
		map.put("AUTONOMOUS_STOP", CommandType.AUTONOMOUS_STOP);
		map.put("AUTONOMOUS_START", CommandType.AUTONOMOUS_START);
		
		String[] parts = str.split(" ");
		CommandType c = map.get(parts[0]);
		if (c == null) {
			return new Command(CommandType.UNKNOWN, str);
		} else {
			return new Command(c, str);
		}
	}
	
	public CommandType getType() {
		return type;
	}
	public String getValue() {
		return value;
	}
}
