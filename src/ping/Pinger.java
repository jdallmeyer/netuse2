package ping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import data.ListOfPings;
import data.Ping;
import main.Logger;

public class Pinger {

	private ListOfPings pingList;
	private final Logger logger;
	private String target;
	private boolean record;
	private static final int TIMEOUT = 1000 ;

	public Pinger(String tgt, boolean rec, ListOfPings list) {
		pingList = list;
		logger = Logger.getInstance();
		target = tgt;
		record = rec;
	}

	public boolean doPing() {
		Ping aPing;
		InetAddress autoTarget = null;
		boolean status = false;
		long now = new Date().getTime();
		long later;
		long rtt;

		try {
			autoTarget = InetAddress.getByName(target);
		}
		catch (UnknownHostException e) {
			logger.statusLogUpdate("Unable to resolve host.  Check DNS settings.");
			return false;
		}

		try {
			if(autoTarget.isReachable(TIMEOUT)) {
				later = new Date().getTime();
				rtt = later - now;
				status = true;
			}
			else {
				rtt = 0;
				status = false;
			}
			if(record && pingList != null) {
				aPing = new Ping(rtt, now, status);
				pingList.addPing(aPing);
			}

		}
		catch (IOException io) {
			logger.statusLogUpdate("Network error detected.  Check network connection settings.");
		}

		return status;
	}
}
