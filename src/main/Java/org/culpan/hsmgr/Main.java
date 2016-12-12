package org.culpan.hsmgr;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.culpan.hsmgr.dialog.AddPersonDialog;
import org.culpan.hsmgr.dialog.DamagePersonDialog;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
                if (item.isPlayer()) {
                    gc.setFont(Font.font("Verdana", 22));
                } else {
                    gc.setFont(Font.font("Verdana", FontPosture.ITALIC, 22));
                }
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

    protected Combatant selectedCombatant;

    protected TableView tableView;

    protected Stage primaryStage;

    protected MenuBar createMenu() {
        MenuBar result = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(event -> save());
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(event -> open());

        fileMenu.getItems().addAll(openItem, saveItem);

        Menu editMenu = new Menu("Edit");
        MenuItem addPersonItem = new MenuItem("Add Person");
        addPersonItem.setOnAction(event -> addPerson(null));
        MenuItem addMinionsItem = new MenuItem("Add Minions");
        addMinionsItem.setOnAction(event -> addMinions());
        MenuItem editPersonItem = new MenuItem("Edit Person");
        editPersonItem.setOnAction(event -> {
            if (selectedCombatant != null) {
                addPerson(selectedCombatant);
            }
        });
        MenuItem deletePersonItem = new MenuItem("Delete Person");
        deletePersonItem.setOnAction(event -> {
            if (selectedCombatant != null) {
                deletePerson(selectedCombatant);
            }
        });
        MenuItem deleteNonPlayersItem = new MenuItem("Delete Non-Players");
        deleteNonPlayersItem.setOnAction(event -> {
            deleteNonPlayers();
        });
        MenuItem deleteAllItem = new MenuItem("Delete All");
        deleteAllItem.setOnAction(event -> {
            deleteAll();
        });

        editMenu.getItems().addAll(addPersonItem, addMinionsItem, editPersonItem,
                new SeparatorMenuItem(), deletePersonItem, deleteNonPlayersItem, deleteAllItem);

        Menu actionsMenu = new Menu("Actions");
        MenuItem damagePersonItem = new MenuItem("Damage Person");
        damagePersonItem.setOnAction(event -> damagePerson());
        MenuItem pushAttackItem = new MenuItem("Push Attack");
        pushAttackItem.setOnAction(event -> pushAttack(selectedCombatant));
        actionsMenu.getItems().addAll(damagePersonItem, pushAttackItem);

        result.getMenus().addAll(fileMenu, editMenu, actionsMenu);

        return result;
    }

    private void pushAttack(Combatant selectedCombatant) {
        if (selectedCombatant == null) return;

        selectedCombatant.damage(10, 00);
    }

    private void damagePerson() {
        if (selectedCombatant == null) return;

        Dialog<Combatant> dialog = DamagePersonDialog.init(selectedCombatant);

        Optional<Combatant> result = dialog.showAndWait();
        if (result.isPresent()) {
        }
    }

    private void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Combat");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Combat.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                Combat c = (Combat) jaxbUnmarshaller.unmarshal(file);
                if (c != null && c.getCombatants() != null) {
                    for (Combatant combatant : c.getCombatants()) {
                        combatant.reset();
                    }
                    hsMgrModel.allCombatants.addAll(c.getCombatants());
                }
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("You are going to delete all combatants");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            tableView.getSelectionModel().clearSelection();
            this.selectedCombatant = null;
            hsMgrModel.allCombatants.clear();
        }
    }

    private void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Combat");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            Combat combat = new Combat();
            combat.getCombatants().addAll(hsMgrModel.allCombatants);
            try {
                JAXBContext contextObj = JAXBContext.newInstance(Combat.class);

                Marshaller marshallerObj = contextObj.createMarshaller();
                marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                marshallerObj.marshal(combat, new FileOutputStream(file));
            } catch (PropertyException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteNonPlayers() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("You are going to delete all non-players");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            tableView.getSelectionModel().clearSelection();
            this.selectedCombatant = null;
            hsMgrModel.allCombatants.removeIf(c -> !c.isPlayer());
        }
    }

    private void deletePerson(Combatant selectedCombatant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("You are going to delete " + selectedCombatant.getName());
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            tableView.getSelectionModel().clearSelection();
            hsMgrModel.allCombatants.remove(selectedCombatant);
            this.selectedCombatant = null;
        }
    }

    protected void addPerson(Combatant c) {
        Dialog<Combatant> dialog;

        if (c == null) {
            dialog = AddPersonDialog.init(c, "Add Person");
        } else {
            dialog = AddPersonDialog.init(c, "Edit Person");
        }

        Optional<Combatant> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (c != null && hsMgrModel.allCombatants.contains(c)) {
                int index = hsMgrModel.allCombatants.indexOf(c);
                hsMgrModel.allCombatants.set(index, result.get());
            } else {
                hsMgrModel.allCombatants.add(result.get());
            }
            selectedCombatant = result.get();
        }
    }

    private void addMinions() {
        Dialog<Combatant> dialog = AddPersonDialog.init(null, "Add Minions");

        Optional<Combatant> result = dialog.showAndWait();
        if (result.isPresent()) {
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("Number of Minions");
            dlg.setHeaderText("How many minions do you want to add?");
            dlg.setContentText("Number:");

            final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setDisable(true);

            dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                okButton.setDisable(!newValue.matches("\\d+"));
            });

            Optional<String> numText = dlg.showAndWait();
            numText.ifPresent(value -> {
                int num = Integer.parseInt(value);
                for (int i = 0; i < num; i++) {
                    Combatant m = result.get().clone();
                    m.setName(result.get().getName() + " " + Integer.toString(i + 1));
                    hsMgrModel.allCombatants.add(m);
                }
            });
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        VBox rootPane = new VBox();
        BorderPane root = new BorderPane();
        rootPane.setVgrow(root, Priority.ALWAYS);
        primaryStage.setTitle("Hero System Manager");
        primaryStage.setResizable(false);
        rootPane.getChildren().addAll(createMenu(), root);
        Scene scene = new Scene(rootPane, 1000, 800);
        primaryStage.setScene(scene);

        HBox buttonGroup = new HBox();
        Button nextButton = new Button("Next");
        nextButton.setDefaultButton(true);
        nextButton.setOnAction(e -> next());
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
            if (active.getSelectionModel().getSelectedItem() != null) {
                active.getSelectionModel().getSelectedItem().acted.setValue(!active.getSelectionModel().getSelectedItem().acted.getValue());
                active.getSelectionModel().getSelectedItem().held.setValue(false);
            }
        });

        tableView = buildTableView();
        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            //Check whether item is selected and set value of selected item to Label
            if(tableView.getSelectionModel().getSelectedItem() != null) {
                TableView.TableViewSelectionModel selectionModel = tableView.getSelectionModel();
                ObservableList selectedCells = selectionModel.getSelectedCells();
                TablePosition tablePosition = (TablePosition) selectedCells.get(0);
                Object val = tablePosition.getTableColumn().getCellData(newValue);
                selectedCombatant = hsMgrModel.getCombatantByName(val.toString());
            }
        });

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

    private void next() {
        List<Combatant> heldActions = new LinkedList<>();
        int nextSeg = Integer.parseInt(hsMgrModel.currentSegment.getValue()) + 1;
        if (nextSeg == 13) nextSeg = 1;

        for (Combatant c : hsMgrModel.allCombatants) {
            if ((c.isInPhase(nextSeg) && c.held.getValue()) ||
                    (c.isInPhase(nextSeg) && c.isInPhase(nextSeg - 1 == 0 ? 12 : nextSeg - 1) && !c.acted.getValue())) {
                heldActions.add(c);
            }
        }

        if (heldActions.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Held Actions");
            alert.setHeaderText("The following combatants are about to lose their \nHeld Actions in Phase " + nextSeg);
            String msg = "";
            for (Combatant c : heldActions) {
                msg += c.getName() + "\n";
            }
            alert.setContentText(msg);

            alert.showAndWait();
        }

        hsMgrModel.onNext();
    }

    protected TableView buildTableView() {
        TableView<Combatant> result = new TableView(hsMgrModel.allCombatants);

        TableColumn<Combatant, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory("name"));
        name.setPrefWidth(240);
        TableColumn rec = new TableColumn("Rec");
        rec.setCellValueFactory(new PropertyValueFactory("rec"));
        rec.setPrefWidth(50);
        rec.setStyle( "-fx-alignment: CENTER;");
        TableColumn con = new TableColumn("Con");
        con.setCellValueFactory(new PropertyValueFactory("con"));
        con.setPrefWidth(50);
        con.setStyle( "-fx-alignment: CENTER;");
        TableColumn stun = new TableColumn("Stun");
        stun.setCellValueFactory(new PropertyValueFactory("stun"));
        stun.setPrefWidth(75);
        stun.setStyle( "-fx-alignment: CENTER;");
        TableColumn<Combatant, Integer> currStun = new TableColumn<>("Curr Stun");
        currStun.setCellValueFactory(cellData -> cellData.getValue().getCurrentStunProperty().asObject());
        currStun.setPrefWidth(75);
        currStun.setStyle( "-fx-alignment: CENTER;");
        TableColumn body = new TableColumn("Body");
        body.setCellValueFactory(new PropertyValueFactory("body"));
        body.setPrefWidth(75);
        body.setStyle( "-fx-alignment: CENTER;");
        TableColumn<Combatant, Integer> currBody = new TableColumn<>("Curr Body");
        currBody.setCellValueFactory(cellData -> cellData.getValue().getCurrentBodyProperty().asObject());
        currBody.setPrefWidth(75);
        currBody.setStyle( "-fx-alignment: CENTER;");

/*        name.setCellFactory(column ->
            new TableCell<Combatant, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);

                    TableRow<Combatant> currentRow = getTableRow();

                    if (!isEmpty()) {

                        if(currentRow.getItem().currentStun.getValue() < 0)
                            currentRow.setStyle("-fx-background-color:red");
                        else
                            currentRow.setStyle("-fx-background-color:white");
                    }

                }
        });*/

        result.getColumns().addAll(name, stun, currStun, rec, con, body, currBody);

        return result;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
