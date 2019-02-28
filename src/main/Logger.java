package main;

import java.util.Date;

public class Logger {
	private Date timestamp;
	private static Logger instance = null;

	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	private Logger() {
		timestamp = new Date();
		statusLogUpdate("Logging Initialized");
	}

	private String timestamp(String str) {
		timestamp = new Date();
		StringBuilder sb = new StringBuilder(str);
		sb.insert(0, timestamp.toString() + " - ");

		return sb.toString();
	}

	public void statusLogUpdate(String message) {
		MainGUIController mgc = Main.getMGC();
		if(message == null || mgc == null) {
			return;
		}


		mgc.appendToStatusLog(timestamp(message));
		mgc.appendToConsoleLog(timestamp(message));
	}

	public void logIssue(String message) {
		MainGUIController mgc = Main.getMGC();

		if(mgc != null) {
			mgc.appendTodetectedIssueLog(timestamp(message));
		}
	}

}
