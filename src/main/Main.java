package main;

import data.SystemInfo;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {

	private static MainGUIController mgc;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
			BorderPane root = loader.load();
			mgc = loader.getController();
			Scene scene = new Scene(root,1200,800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.getIcons().add(new Image("main/nu2.png"));
			primaryStage.setTitle("NetUse2 v1.0");
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(Exception e) {
			new ExceptionWindow(e.getMessage());
		}

		SystemInfo sysinfo = new SystemInfo();
		mgc.loadSystemParameters(sysinfo.getParams());
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static MainGUIController getMGC() {
		return mgc;
	}
}
