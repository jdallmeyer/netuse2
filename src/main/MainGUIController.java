package main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import data.ListOfPings;
import data.Ping;
import data.SystemParameter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ping.AnalysisWorker;
import ping.DataLoadWorker;
import ping.PingWorker;

public class MainGUIController implements Initializable{

	private ObservableList<SystemParameter> parameters;
	private ListOfPings pings;
	private PingWorker pw;
	private PseudoClass errorClass;
	private AnalysisWorker aw;

	//GUI Objects
	@FXML
	private BorderPane graphPane;
	@FXML
	private LineChart<String, Integer> pingChart;
	@FXML
	private PieChart avgChart;

	//System Information Pane
	@FXML
	private TableView<SystemParameter> parameterTable;
	@FXML
	private TableColumn<String, String> parameterColumn;
	@FXML
	private TableColumn<String, String> valueColumn;

	//Duration Mode Tab
	@FXML
	private Tab manualTab;
	@FXML
	private TextField pingTarget;
	@FXML
	private TextField pingDuration;
	@FXML
	private ChoiceBox<String> durationChoice;
	@FXML
	private TextArea consoleLog;
	@FXML
	private Button durationStartButton;
	@FXML
	private Button durationStopButton;
	@FXML
	private Button durationClearButton;

	//Analysis Mode Tab
	@FXML
	private Tab analysisTab;
	@FXML
	private TextArea statusLog;
	@FXML
	private Button autoStartButton;
	@FXML
	private Button autoStopButton;
	@FXML
	private Button autoClearButton;
	@FXML
	private TextArea issueLog;


	@Override
	public void initialize(URL location, ResourceBundle resourceBundle) {

		errorClass = PseudoClass.getPseudoClass("error");
		pings = new ListOfPings();
		durationChoice.setItems(FXCollections.observableArrayList("seconds","minutes","hours"));
		statusLog.setText(new Date().toString() + " - System initialized\n");
		consoleLog.setText(new Date().toString() + " - System initialized\n");
		statusLog.setWrapText(true);
		consoleLog.setWrapText(true);
		issueLog.setWrapText(true);
		parameterColumn.setCellValueFactory(
				new PropertyValueFactory<>("parameter"));
		valueColumn.setCellValueFactory(
				new PropertyValueFactory<>("value"));
		pingChart.getData().add(pings.getXYChartData());
		avgChart.getData().addAll(pings.getPieData());

	}

	@FXML
	protected void handleCloseItemClicked(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	protected void automaticPingStart(ActionEvent event) {
		autoStartButton.setDisable(true);
		autoClearButton.setDisable(true);
		autoStopButton.setDisable(false);
		manualTab.setDisable(true);
		aw = new AnalysisWorker(parameters, pings);
		aw.addPropertyChangeListener(new AnalysisPropertyChangeHandler());
		aw.execute();
	}

	@FXML
	protected void automaticPingStop(ActionEvent event) {
		if(aw != null){
			aw.cancel(true);
		}
		autoStartButton.setDisable(false);
		autoClearButton.setDisable(false);
		manualTab.setDisable(false);
		appendToStatusLog(new Date().toString() + " - Stopping analysis");
	}

	@FXML
	protected void automaticPingClear(ActionEvent event) {
		autoStartButton.setDisable(false);
		autoStopButton.setDisable(true);
		autoClearButton.setDisable(true);
		manualTab.setDisable(false);
		issueLog.clear();
		pings.reset();
		appendToStatusLog("Results Cleared");
	}

	@FXML
	protected void durationStart(ActionEvent event) {
		clearErrorFill();
		boolean cont = true;
		String target = pingTarget.getText();
		int rawDuration = 0;

		if(target.isEmpty()) {
			appendToConsoleLog("Please enter a ping target.");
			pingTarget.pseudoClassStateChanged(errorClass, true);
			pingTarget.clear();
			cont = false;
		}

		try {
			rawDuration = Integer.parseInt(pingDuration.getText());
			if(rawDuration <= 0|| rawDuration >= 100) {
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException ne) {
			appendToConsoleLog("Please enter positive numeric duration.");
			pingDuration.pseudoClassStateChanged(errorClass, true);
			pingDuration.clear();
			cont = false;
		}

		String durationSelection = durationChoice.getValue();

		int duration = 0;
		int delay = 0;
		if(durationSelection != null) {
			switch (durationSelection) {
				case "seconds":
					if(rawDuration <= 60) {
						duration = rawDuration;
						delay = 1;
					}
					break;
				case "minutes":
					if(rawDuration <= 60) {
						duration = rawDuration * 60;
						delay = 10;
					}
					break;
				case "hours":
					if(rawDuration <= 12) {
						duration = rawDuration * 3600;
						delay = 300;
					}
					break;
				default:
					cont = false;
					appendToConsoleLog("Invalid time combination.");
					break;
			}
		}
		else {
			appendToConsoleLog("Please select a duration.");
			durationChoice.pseudoClassStateChanged(errorClass, true);
			cont = false;
		}

		if(cont) {
			analysisTab.setDisable(true);
			durationStartButton.setDisable(true);
			durationStopButton.setDisable(false);
			pw = new PingWorker(target, duration, delay, true, pings);
			pw.addPropertyChangeListener(new PingerPropertyChangeHandler());
			pw.execute();
		}

	}

	@FXML
	protected void durationStop(ActionEvent event){
		if(pw != null){
			pw.cancel(true);
		}
		durationStartButton.setDisable(false);
		durationClearButton.setDisable(false);
		analysisTab.setDisable(false);
		appendToConsoleLog(new Date().toString() + " - Stopping manual mode");
	}

	@FXML
	protected void durationClearClick(ActionEvent event){
		durationStartButton.setDisable(false);
		durationClearButton.setDisable(true);
		pings.reset();
		appendToConsoleLog("Results Cleared");
	}

	@FXML
	protected void savePingResults(ActionEvent event){
		if(pings == null || pings.getTotalPings() == 0){
			Alert alert = new Alert(Alert.AlertType.ERROR, "Run the automatic analysis " +
					"process or manual mode to capture packet information.");
			alert.setHeaderText("No Data");
			alert.setHeight(400);
			alert.show();
		}
		else{
			appendToConsoleLog("Exporting Data");
			appendToStatusLog("Exporting Data");

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Ping Data File");
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("NetUse Files", "*.nu2"));
			File selectedFile = fileChooser.showSaveDialog(null);
			try (FileWriter fileWriter = new FileWriter(selectedFile); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
				for (Ping p : pings.getPingList()) {
					bufferedWriter.write(p.getTimestamp() + "," + p.getReplyTime()
							+ "," + p.isgoodPing() + "\n");
				}
				appendToStatusLog("Export successful");
				appendToConsoleLog("Export successful");
			} catch (IOException ioe) {
				new ExceptionWindow(ioe.getMessage());
			}
		}
	}

	@FXML
	protected void loadData(){
		pings.reset();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Ping Data File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("NetUse Files", "*.nu2"));
		File selectedFile = fileChooser.showOpenDialog(null);

		DataLoadWorker dlw = new DataLoadWorker(selectedFile, pings);
		dlw.execute();
	}

	@FXML
	protected void showAboutDialog(ActionEvent event){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About NetUse2");
		alert.setHeaderText("NetUse2 Network Diagnostic Tool");
		alert.setContentText("Jon Dallmeyer SIGNIFICANTBits 2019");

		alert.showAndWait();
	}

	@FXML
	protected void showHelpViewer(ActionEvent event){
		WebView browser = new WebView();
		WebEngine webEngine = browser.getEngine();
		URL url = this.getClass().getResource("/help/index.html");
		webEngine.load(url.toString());

		StackPane secondaryLayout = new StackPane();
		secondaryLayout.getChildren().add(browser);

		Scene helpScene = new Scene(secondaryLayout, 800, 600);
		Stage newWindow = new Stage();
		newWindow.initModality(Modality.APPLICATION_MODAL);
		newWindow.setTitle("NetUse2 Help");
		newWindow.setScene(helpScene);
		newWindow.show();
	}

	private void clearErrorFill() {
		pingTarget.pseudoClassStateChanged(errorClass, false);
		pingDuration.pseudoClassStateChanged(errorClass, false);
		durationChoice.pseudoClassStateChanged(errorClass, false);
	}

	public void appendToStatusLog(String message) {
		Platform.runLater(() -> statusLog.appendText(message + "\n"));
	}

	public void appendToConsoleLog(String message) {
		Platform.runLater(() -> consoleLog.appendText(message + "\n"));
	}

	public void appendTodetectedIssueLog(String message) {
		Platform.runLater(() -> issueLog.appendText(message + "\n"));
	}

	public void loadSystemParameters(ArrayList<SystemParameter> params) {
		parameters = FXCollections.observableList(params);
		parameterTable.setItems(parameters);
	}

	private void pingComplete() {
		autoStopButton.setDisable(true);
		autoClearButton.setDisable(false);
		durationStopButton.setDisable(true);
		durationClearButton.setDisable(false);
		manualTab.setDisable(false);
		analysisTab.setDisable(false);
	}

	private class PingerPropertyChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("state".equalsIgnoreCase(evt.getPropertyName())) {
				PingWorker p = (PingWorker)evt.getSource();
				if(p.isDone()){
					pingComplete();
				}
			}
		}
	}

	private class AnalysisPropertyChangeHandler implements PropertyChangeListener{

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("state".equalsIgnoreCase(evt.getPropertyName())) {
				AnalysisWorker a = (AnalysisWorker)evt.getSource();
				if(a.isDone()) {
					try {
						if(a.get()) {
						issueLog.appendText("No problems detected!");	
						}
					}
					catch(InterruptedException | ExecutionException | CancellationException e) {
						Thread.currentThread().interrupt();
						new ExceptionWindow(e.getMessage());
					}
					pingComplete();
				}
			}

		}

	}
}
