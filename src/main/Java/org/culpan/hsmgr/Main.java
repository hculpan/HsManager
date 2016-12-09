package org.culpan.hsmgr;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    static protected HsMgrModel hsMgrModel = new HsMgrModel();

    static Image actedImage = new Image(Main.class.getResourceAsStream("/acted.png"));

    static Image notActedImage = new Image(Main.class.getResourceAsStream("/not_acted.png"));

    static class CombatantCell extends ListCell<Combatant> {
        @Override
        public void updateItem(Combatant item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                Canvas canvas = new Canvas(340, 35);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setFont(Font.font("Verdana", 22));
                if (item.held.getValue()) {
                    gc.fillText(item.getName() + " (Held Action)", 35, 27);
                } else {
                    gc.fillText(item.getName(), 35, 27);
                }
                if (item.acted.getValue()) {
                    gc.drawImage(actedImage, 0, 3);
                } else {
                    gc.drawImage(notActedImage, 0, 3);
                }

                setGraphic(canvas);
            } else {
                setGraphic(null);
            }
        }
    }

    protected MenuBar createMenu() {
        MenuBar result = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem addPersonItem = new MenuItem("Add Person");

        fileMenu.getItems().addAll(addPersonItem);

        Menu editMenu = new Menu("Edit");

        result.getMenus().addAll(fileMenu, editMenu);

        return result;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hero System Manager");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, 1000, 800);
        ((BorderPane)scene.getRoot()).getChildren().addAll(createMenu());
        primaryStage.setScene(scene);

        HBox buttonGroup = new HBox();
        Button nextButton = new Button("Next");
        nextButton.setDefaultButton(true);
        nextButton.setOnAction(e -> hsMgrModel.onNext());
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> hsMgrModel.onQuit());
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> hsMgrModel.reset());

        buttonGroup.getChildren().addAll(resetButton, quitButton, nextButton);
        buttonGroup.setPadding(new Insets(15, 12, 15, 12));
        buttonGroup.setAlignment(Pos.CENTER);
        buttonGroup.setSpacing(30);

        HBox topBox = new HBox();
        Font topFont = Font.font("Verdana", 24);
        Label turnLabel = new Label("Turn");
        turnLabel.setFont(topFont);
        Text turnText = new Text();
        turnText.setFont(topFont);
        turnText.textProperty().bind(hsMgrModel.currentTurn);
        Label segmentLabel = new Label("Segment");
        segmentLabel.setFont(topFont);
        Text segmentText = new Text();
        segmentText.setFont(topFont);
        segmentText.textProperty().bind(hsMgrModel.currentSegment);

        Label noLabel = new Label();
        noLabel.setPrefWidth(250);

        topBox.getChildren().addAll(segmentLabel, segmentText, noLabel, turnLabel, turnText);
        topBox.setPadding(new Insets(15, 12, 15, 12));
        topBox.setAlignment(Pos.CENTER);
        topBox.setSpacing(10);

        ListView<Combatant> active = new ListView<>(hsMgrModel.currentActive);
        active.setPrefSize(360, 0);
        active.setOnMouseClicked(event -> {
            active.getSelectionModel().getSelectedItem().acted.setValue(!active.getSelectionModel().getSelectedItem().acted.getValue());
        });

        TableView tableView = buildTableView();

        root.setCenter(tableView);
        root.setLeft(active);
        root.setBottom(buttonGroup);
        root.setTop(topBox);

        active.setCellFactory(list -> {
            CombatantCell result = new CombatantCell();
            result.setStyle("-fx-background-color: white;");
            return result;
        });

        primaryStage.show();
    }

    protected TableView buildTableView() {
        TableView result = new TableView(hsMgrModel.allCombatants);

        TableColumn name = new TableColumn("Name");
        name.setCellValueFactory(new PropertyValueFactory("name"));
        name.setPrefWidth(250);
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
