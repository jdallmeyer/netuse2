package data;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class ListOfPings {

	private final ArrayList<Ping> pingList;
	private final XYChart.Series<String, Integer> lineData;
	private final ObservableList<PieChart.Data> pieData;
	private PieChart.Data goodSlice;
	private PieChart.Data badSlice;
	private PieChart.Data slowSlice;

	public ListOfPings() {
		pingList = new ArrayList<>();
		lineData = new XYChart.Series<>();
		lineData.setName("RTT Values");
		pieData = FXCollections.observableArrayList();
		prepareSlices();
	}

	private void prepareSlices() {
		goodSlice = new PieChart.Data("Good Replies", 0);
		badSlice = new PieChart.Data("Bad Replies", 0);
		slowSlice = new PieChart.Data("High-Latency Replies", 0);
	}

	public void addPing(Ping ping) {
		pingList.add(ping);
		if (ping.getReplyTime() >= 500 ) {
			double slowVal = slowSlice.getPieValue();
			slowVal += 1;
			slowSlice.setPieValue(slowVal);
		}
		else if(ping.isgoodPing()) {
			double goodVal = goodSlice.getPieValue();
			goodVal += 1;
			goodSlice.setPieValue(goodVal);
			Platform.runLater(() ->
			lineData.getData().add(new XYChart.Data<>(String.valueOf(ping.getTimestamp()), (int) ping.getReplyTime())));
		}
		else {
			double badVal = badSlice.getPieValue();
			badVal += 1;
			badSlice.setPieValue(badVal);
		}
	}

	public int getAverageRTT() {
		int avg = 0;

		for(Ping p: pingList) {
			avg += p.getReplyTime();
		}

		avg /= pingList.size();

		return avg;
	}

	public int getBadPingCount() {
		int retval = 0;
		for(Ping p: pingList) {
			if(!p.isgoodPing()) {
				retval++;
			}
		}

		return retval;
	}

	public int getTotalPings() {
		return pingList.size();
	}

	public XYChart.Series<String, Integer> getXYChartData() {
		return lineData;
	}

	public ArrayList<Ping> getPingList(){
		return pingList;
	}

	public ObservableList<PieChart.Data> getPieData() {
		pieData.add(goodSlice);
		pieData.add(badSlice);
		pieData.add(slowSlice);
		return pieData;
	}

	public void reset() {
		lineData.getData().clear();
		goodSlice.setPieValue(0);
		badSlice.setPieValue(0);
		slowSlice.setPieValue(0);
		pieData.clear();
		pingList.clear();
	}
}
