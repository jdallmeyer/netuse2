package ping;

import data.ListOfPings;
import data.Ping;
import main.ExceptionWindow;
import main.Logger;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DataLoadWorker extends SwingWorker<Boolean, Integer> {

	private final File mFile;
	private ListOfPings pings;
	private final Logger logger;

	public DataLoadWorker(File dataFile, ListOfPings list){
		mFile = dataFile;
		pings = list;
		logger = Logger.getInstance();
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		int count = 0;
		Scanner scanner = new Scanner(mFile);
		try {
			logger.statusLogUpdate("Loading data set");

			while (scanner.hasNext()) {
				String[] temp = scanner.nextLine().split(",");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
				Date date = simpleDateFormat.parse(temp[0]);
				Ping p = new Ping(Long.valueOf(temp[1]), date.getTime(), Boolean.valueOf(temp[2]));
				Thread.sleep(100);
				pings.addPing(p);
				count++;
			}
			logger.statusLogUpdate("Loaded " + count + " data records");
		}
		catch (InterruptedException ie){
			Thread.currentThread().interrupt();
		}
		finally {
			try {
				scanner.close();
			}
			catch (IllegalStateException ie) {
				new ExceptionWindow(ie.getMessage());
			}
		}
		return Boolean.TRUE;
	}
}
