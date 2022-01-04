package ch.get;

import ch.get.util.LoggerUtil;
import ch.get.view.RootLayoutController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainApp extends Application {
	
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("������ ä�� ���� v1.0");
		primaryStage.setWidth(600);
		primaryStage.setHeight(450);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UTILITY);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				RootLayoutController.getInstance().runServer();
			}
		});
		
		// ���� ���̾ƿ�
		initMain();
		
		// �������� ���̱�
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initMain() {
		FXMLLoader loader = new FXMLLoader();
		
		try {
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			BorderPane borderPane = (BorderPane) loader.load();
			Scene scene = new Scene(borderPane);
			this.primaryStage.setScene(scene);
			
			LoggerUtil.info("����...");
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage());
		}
	}
}
