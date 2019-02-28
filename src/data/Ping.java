package data;

import java.util.Date;

public class Ping {
	
	private final long replyTime;
	private final Date timestamp;
	private final boolean goodPing;
	
	public Ping(long rtt, long ts, boolean status) {
		replyTime = rtt;
		timestamp = new Date(ts);
		goodPing = status;
	}
	
	public long getReplyTime() {
		return replyTime;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public boolean isgoodPing() {
		return goodPing;
	}
}
