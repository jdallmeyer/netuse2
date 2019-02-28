package ping;

import java.util.Date;
import javax.swing.SwingWorker;
import data.ListOfPings;
import data.SystemParameter;
import javafx.collections.ObservableList;
import main.ExceptionWindow;
import main.Logger;

public class AnalysisWorker extends SwingWorker<Boolean, Integer> {

	private final ObservableList<SystemParameter> parameters;
	private final Logger logger;
	private ListOfPings listOfPings;

	public AnalysisWorker(ObservableList<SystemParameter> params, ListOfPings lop) {
		logger = Logger.getInstance();
		logger.statusLogUpdate("Beginning automatic network analysis.");
		parameters = params;
		listOfPings = lop;
	}

	@Override
	protected Boolean doInBackground() {
		boolean retval = true;
		logger.statusLogUpdate("Testing ping performance.");
		Pinger pinger = new Pinger("www.google.com", true, listOfPings);

		logger.statusLogUpdate("Pinging www.google.com every 1 second for 60 seconds");
		long now = new Date().getTime();
		long end = now + 60000;
		while((now < end) && !this.isCancelled()) {
			if(!pinger.doPing()) {
				retval = false;
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ie) {
				new ExceptionWindow(ie.getMessage());
				Thread.currentThread().interrupt();
			}
			now = new Date().getTime();
		}		
		logger.statusLogUpdate("Ping complete");
		
		AnalysisEngine analysisEngine = new AnalysisEngine(listOfPings);
		if(retval) {
			retval = analysisEngine.loadParameters(parameters);
		}
		if(retval) {
			retval = analysisEngine.analyze();
		}
		if(retval) {
			logger.logIssue("No issues detected.");
		}

		return retval;
	}

}
