package org.culpan.hsmgr;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    static protected HsMgrModel hsMgrModel = new HsMgrModel();

    static class CombatantCell extends ListCell<Combatant> {
        @Override
        public void updateItem(Combatant item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                Canvas canvas = new Canvas(100, 20);
                if (item.isInPhase(Integer.parseInt(hsMgrModel.currentSegment.getValue()))) {
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.strokeText(item.getName(), 5, 15);
                }

                setGraphic(canvas);
            } else {
                setGraphic(null);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hero System Manager");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 800, 500));

        HBox buttonGroup = new HBox();
        Button nextButton = new Button("Next");
        nextButton.setDefaultButton(true);
        nextButton.setOnAction(e -> hsMgrModel.onNext());
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> hsMgrModel.onQuit());

        buttonGroup.getChildren().addAll(quitButton, nextButton);
        buttonGroup.setPadding(new Insets(15, 12, 15, 12));
        buttonGroup.setAlignment(Pos.CENTER);
        buttonGroup.setSpacing(10);

        HBox topBox = new HBox();
        Label turnLabel = new Label("Turn");
        Text turnText = new Text();
        turnText.textProperty().bind(hsMgrModel.currentTurn);
        Label segmentLabel = new Label("Segment");
        Text segmentText = new Text();
        segmentText.textProperty().bind(hsMgrModel.currentSegment);
        topBox.getChildren().addAll(turnLabel, turnText, segmentLabel, segmentText);
        topBox.setPadding(new Insets(15, 12, 15, 12));
        topBox.setAlignment(Pos.CENTER);
        topBox.setSpacing(10);
        ListView<Combatant> active = new ListView<>(hsMgrModel.currentActive);

        TableView tableView = buildTableView();

        root.setCenter(tableView);
        root.setLeft(active);
        root.setBottom(buttonGroup);
        root.setTop(topBox);

        active.setCellFactory(list -> new CombatantCell()
        );

        primaryStage.show();
    }

    protected TableView buildTableView() {
        TableView result = new TableView(hsMgrModel.allCombatants);

        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new PropertyValueFactory("name"));
        name.setPrefWidth(150);
        TableColumn rec = new TableColumn("Rec");
        rec.setCellValueFactory(new PropertyValueFactory("rec"));
        rec.setPrefWidth(50);
        rec.setStyle( "-fx-alignment: CENTER;");
        TableColumn stun = new TableColumn("Stun");
        stun.setCellValueFactory(new PropertyValueFactory("stun"));
        stun.setPrefWidth(75);
        stun.setStyle( "-fx-alignment: CENTER;");
        TableColumn currStun = new TableColumn("Curr Stun");
        currStun.setCellValueFactory(new PropertyValueFactory("currentStun"));
        currStun.setPrefWidth(75);
        currStun.setStyle( "-fx-alignment: CENTER;");
        TableColumn body = new TableColumn("Body");
        body.setCellValueFactory(new PropertyValueFactory("body"));
        body.setPrefWidth(75);
        body.setStyle( "-fx-alignment: CENTER;");
        TableColumn currBody = new TableColumn("Curr Body");
        currBody.setCellValueFactory(new PropertyValueFactory("currentBody"));
        currBody.setPrefWidth(75);
        currBody.setStyle( "-fx-alignment: CENTER;");

        result.getColumns().addAll(name, rec, stun, currStun, body, currBody);

        return result;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
