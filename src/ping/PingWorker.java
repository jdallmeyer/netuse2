package ping;

import java.util.Date;
import javax.swing.SwingWorker;
import data.ListOfPings;
import main.ExceptionWindow;
import main.Logger;

public class PingWorker extends SwingWorker<Boolean, Integer> {

	private final String target;
	private final int duration;
	private final int delay;
	private boolean record;
	private Logger logger = null;
	private ListOfPings listOfPings;

	public PingWorker(String tgt, int dur, int del, boolean rec, ListOfPings list) {
		logger = Logger.getInstance();
		target = tgt;
		duration = dur;
		delay = del * 1000;
		record = rec;
		listOfPings = list;
	}

	@Override
	protected Boolean doInBackground() {
		boolean retval = true;
		logger.statusLogUpdate("Pinging " + target + " every " + delay / 1000 + " second(s) for " + duration + " seconds");
		long now = new Date().getTime();
		long end = now + duration * 1000;
		Pinger pinger = new Pinger(target, record, listOfPings);
		while((now < end) && !this.isCancelled()) {
			if(!pinger.doPing()) {
				retval = false;
			}
			try {
				Thread.sleep(delay);
			}
			catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				new ExceptionWindow(ie.getMessage());
			}
			now = new Date().getTime();
		}		

		logger.statusLogUpdate("Ping complete");


		return retval;

	}
}
