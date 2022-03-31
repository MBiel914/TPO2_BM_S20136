package zad1;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientGUI extends Application{
	private static Client _client;
	
	Label labelWord;
	Label labelLanguageAcronym;
	Label labelResult;
	
	TextField textFieldWord;
	TextField textFieldLanguageAcronym;
	TextField textFieldResult;
	
	Button buttonSendRequest;
	
	@Override
	public void start(Stage mainStage) throws Exception {
		_client = new Client("127.0.0.1", 10666, 100);
		GridPane gridPane = new GridPane();
		
		labelWord = new Label("Insert word:");
		labelLanguageAcronym = new Label("Insert language acronym:");
		labelResult = new Label("Result:");
		
		textFieldWord = new TextField("dom");
		textFieldLanguageAcronym = new TextField("EN");
		textFieldResult = new TextField("");
		textFieldResult.setEditable(false);
		
		buttonSendRequest = new Button("Send request");
		buttonSendRequest.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0)
			{
				textFieldResult.setText(_client.SendRequestForTransaction(textFieldWord.getText(), textFieldLanguageAcronym.getText()));
			}
		});
		
		gridPane.setMinSize(400, 200);

		gridPane.setPadding(new Insets(2, 2, 2, 2));

		gridPane.setVgap(5);
		gridPane.setHgap(5);

		gridPane.setAlignment(Pos.TOP_LEFT);
		
		gridPane.add(labelWord, 0, 0);
		gridPane.add(textFieldWord, 1, 0);

		gridPane.add(labelLanguageAcronym, 0, 1);
		gridPane.add(textFieldLanguageAcronym, 1, 1);
		
		gridPane.add(buttonSendRequest, 1, 2);
		
		gridPane.add(labelResult, 0, 3);
		gridPane.add(textFieldResult, 1, 3);
		
		Scene scene = new Scene(gridPane, 400, 200);
		mainStage.setTitle("Client");
		mainStage.setScene(scene);
		mainStage.show();
	}
}
