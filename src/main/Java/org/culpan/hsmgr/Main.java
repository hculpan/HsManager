package org.culpan.hsmgr;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.culpan.hsmgr.dialog.AddPersonDialog;
import org.culpan.hsmgr.dialog.DamagePersonDialog;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    static public final HsMgrModel hsMgrModel = new HsMgrModel();

    static Image actedImage = new Image(Main.class.getResourceAsStream("/acted.png"));

    static Image heldImage = new Image(Main.class.getResourceAsStream("/held.png"));

    static Image notActedImage = new Image(Main.class.getResourceAsStream("/not_acted.png"));

    static Image stunnedImage = new Image(Main.class.getResourceAsStream("/stunned.png"));

    static Image unconsciousImage = new Image(Main.class.getResourceAsStream("/unconscious.png"));

    static Image recoverImage = new Image(Main.class.getResourceAsStream("/recover.png"));

    static Image recoverFlagImage = new Image(Main.class.getResourceAsStream("/recover_flag.png"));

    static Image abortedImage = new Image(Main.class.getResourceAsStream("/aborted.png"));

    static Image flashedImage = new Image(Main.class.getResourceAsStream("/flash.png"));

    static class CombatantCell extends ListCell<Combatant> {
        @Override
        public void updateItem(Combatant item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                Canvas canvas = new Canvas(340, 35);
                GraphicsContext gc = canvas.getGraphicsContext2D();

                if (item.isActive() && !item.isFullStun()) {
                    gc.drawImage(recoverImage, canvas.getWidth() - 35, 2);
                }

                // Select font
                if (item.isPlayer() && item.isActive()) {
                    gc.setFont(Font.font("Verdana", 22));
                } else if (!item.isPlayer() && item.isActive()) {
                    gc.setFont(Font.font("Verdana", FontPosture.ITALIC, 22));
                } else if (item.isPlayer()) {
                    gc.setFont(Font.font("Verdana", 16));
                } else {
                    gc.setFont(Font.font("Verdana", FontPosture.ITALIC, 16));
                }

                // Modify text based on status
                Text text = new Text();
                if (item.hasHeldAction()) {
                    text.setText(item.getName() + " (Held)");
                } else if (item.hasAborted()) {
                    text.setText(item.getName() + " (Aborted)");
                } else {
                    text.setText(item.getName());
                }

                double width = text.getBoundsInLocal().getWidth() * 1.25;

                gc.fillText(text.getText(), 35, 27);
                width += 70;

                if (item.isConStunned()) {
                    gc.drawImage(stunnedImage, width, 3);
                    width += 40;
                } else if (item.isUnconscious()) {
                    gc.drawImage(unconsciousImage, width, 3);
                    width += 40;
                }

                if (item.isFlashed()) {
                    gc.drawImage(flashedImage, width, 3);
                }

                if (item.hasActed() || item.isConStunned()) {
                    gc.drawImage(actedImage, 1, 2);
                } else if (item.hasHeldAction()) {
                    gc.drawImage(heldImage, 1, 2);
                } else if (item.hasAborted()) {
                    gc.drawImage(abortedImage, 1, 2);
                } else if (item.hasRecovered()) {
                    gc.drawImage(recoverFlagImage, 1, 2);
                } else if (item.isActive()) {
                    gc.drawImage(notActedImage, 1, 2);
                    gc.strokeRoundRect(0, 0, canvas.getWidth(), canvas.getHeight(), 20, 20);
                    gc.strokeRoundRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2, 20, 20);
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

    protected Button nextButton;

    protected MenuItem abortItem;

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
        actionsMenu.getItems().addAll(createActionsMenu());

        result.getMenus().addAll(fileMenu, editMenu, actionsMenu);

        return result;
    }

    private List<MenuItem> createActionsMenu() {
        MenuItem damagePersonItem = new MenuItem("Damage Person");
        damagePersonItem.setOnAction(event -> damagePerson());

        MenuItem pushAttackItem = new MenuItem("Push Attack");
        pushAttackItem.setOnAction(event -> pushAttack(selectedCombatant));

        abortItem = new MenuItem("Abort");
        abortItem.setOnAction(event -> abort());

        MenuItem flashItem = new MenuItem("Flash");
        flashItem.setOnAction(event -> flash());

        MenuItem simpleStunDamage = new MenuItem("Damage Stun");
        simpleStunDamage.setOnAction( event -> damageStun() );

        MenuItem simpleBodyDamage = new MenuItem("Damage Body");
        simpleBodyDamage.setOnAction( event -> damageBody() );

        MenuItem simpleStunHeal = new MenuItem("Heal Stun");
        simpleStunHeal.setOnAction( event -> healStun() );

        MenuItem simpleBodyHeal = new MenuItem("Heal Body");
        simpleBodyHeal.setOnAction( event -> healBody() );

        MenuItem stunItem = new MenuItem("Stun Combatant");
        stunItem.setOnAction(event -> stun());

        MenuItem unstunItem = new MenuItem("Unstun Combatant");
        unstunItem.setOnAction(event -> unstun());

        List<MenuItem> result = new ArrayList<>();
        result.add(damagePersonItem);
        result.add(pushAttackItem);
        result.add(abortItem);
        result.add(flashItem);
        result.add(new SeparatorMenuItem());
        result.add(simpleStunDamage);
        result.add(simpleBodyDamage);
        result.add(simpleStunHeal);
        result.add(simpleBodyHeal);
        result.add(new SeparatorMenuItem());
        result.add(stunItem);
        result.add(unstunItem);

        return result;
    }

    private void stun() {
        if (selectedCombatant == null || selectedCombatant.isUnconscious()) return;

        selectedCombatant.stun();
        if (selectedCombatant.isActive()) {
            hsMgrModel.updateActiveList(true);
        }
    }

    private void unstun() {
        if (selectedCombatant == null || selectedCombatant.isUnconscious()) return;

        selectedCombatant.unstun();
    }

    private void flash() {
        if (selectedCombatant == null || selectedCombatant.isUnconscious()) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Flash Attack");
        dlg.setHeaderText("Flash attack on " + selectedCombatant.getName() + ":");
        dlg.setContentText("Segments:");

        final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!newValue.matches("\\d+"));
        });

        Optional<String> numText = dlg.showAndWait();
        numText.ifPresent(value -> selectedCombatant.setFlashed(Integer.parseInt(value)) );
        hsMgrModel.updateActiveList();
    }

    private void abort() {
        if (selectedCombatant == null) return;
        if (!selectedCombatant.canAbort(hsMgrModel.getCurrentSegement())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Abort");
            alert.setHeaderText("Cannot Abort");
            alert.setContentText(selectedCombatant.getName() + " cannot abort at this time");

            alert.showAndWait();
            return;
        }

        selectedCombatant.abort(hsMgrModel.getCurrentSegement());
        if (selectedCombatant.isActive()) {
            hsMgrModel.updateActiveList(true);
        }
    }

    private void healStun() {
        if (selectedCombatant == null || selectedCombatant.getStun() == selectedCombatant.getCurrentStun()) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Stun Heal");
        dlg.setHeaderText(String.format(selectedCombatant.getName() + " has %1$d STUN out of %2$d.",
                selectedCombatant.getCurrentStun(), selectedCombatant.getStun()));
        dlg.setContentText("Heal Stun:");

        final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!newValue.matches("\\d+"));
        });

        Optional<String> numText = dlg.showAndWait();
        numText.ifPresent(value -> selectedCombatant.heal(Integer.parseInt(value), 0));
    }

    private void healBody() {
        if (selectedCombatant == null || selectedCombatant.getBody() == selectedCombatant.getCurrentBody()) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Stun Body");
        dlg.setHeaderText(String.format(selectedCombatant.getName() + " has %1$d BODY out of %2$d.",
                selectedCombatant.getCurrentBody(), selectedCombatant.getBody()));
        dlg.setContentText("Heal Body:");

        final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!newValue.matches("\\d+"));
        });

        Optional<String> numText = dlg.showAndWait();
        numText.ifPresent(value -> selectedCombatant.heal(0, Integer.parseInt(value)));
    }

    private void damageStun() {
        if (selectedCombatant == null) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Stun Damage");
        dlg.setHeaderText("Do stun damage to " + selectedCombatant.getName() + ":");
        dlg.setContentText("Damage (no defenses apply):");

        final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!newValue.matches("\\d+"));
        });

        Optional<String> numText = dlg.showAndWait();
        numText.ifPresent(value -> hsMgrModel.damage(selectedCombatant, Integer.parseInt(value), 0));
    }

    private void damageBody() {
        if (selectedCombatant == null) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Body Damage");
        dlg.setHeaderText("Do BODY damage to " + selectedCombatant.getName() + ":");
        dlg.setContentText("Damage (no defenses apply):");

        final Button okButton = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        dlg.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(!newValue.matches("\\d+"));
        });

        Optional<String> numText = dlg.showAndWait();
        numText.ifPresent(value -> hsMgrModel.damage(selectedCombatant, 0, Integer.parseInt(value)));
    }

    private void pushAttack(Combatant selectedCombatant) {
        if (selectedCombatant == null) return;

        selectedCombatant.damage(10, 00);
    }

    private void damagePerson() {
        if (selectedCombatant == null) return;

        Dialog<Pair<Integer, Integer>> dialog = DamagePersonDialog.init(selectedCombatant);

        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
        if (result.isPresent()) {
            selectedCombatant.damage(result.get().getKey(), result.get().getValue());
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
        //primaryStage.setResizable(false);
        tableView = buildTableView();
        rootPane.getChildren().addAll(createMenu(), root);
        Scene scene = new Scene(rootPane, 1000, 800);
        primaryStage.setScene(scene);

        HBox buttonGroup = new HBox();
        nextButton = new Button("Next");
        nextButton.setDefaultButton(true);
        nextButton.setOnAction(e -> next());
        final ChangeListener<Boolean> nextChangeListener = (ObservableValue<? extends Boolean> observer, Boolean oldValue, Boolean newValue) -> {
            nextButton.setDisable(!newValue);
        };
        hsMgrModel.doneWithPhase.addListener(nextChangeListener);
        nextButton.setDisable(true);
        final Button startButton = new Button("Start");
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> hsMgrModel.onQuit());
        final Button resetButton = new Button("Reset");
        resetButton.setDisable(true);

        // Setup start/reset button enabling/disabling
        resetButton.setOnAction(e -> {
            nextButton.setDisable(true);
            startButton.setDisable(false);
            resetButton.setDisable(true);
            hsMgrModel.reset();
        });
        startButton.setOnAction(e -> {
            startButton.setDisable(true);
            resetButton.setDisable(false);
            hsMgrModel.start();
        });

        buttonGroup.getChildren().addAll(startButton, resetButton, quitButton, nextButton);
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
            Combatant c = active.getSelectionModel().getSelectedItem();

            MouseButton mouseButton = event.getButton();
            if (mouseButton.equals(MouseButton.PRIMARY) && event.getX() > 315) {
                c.recover();
                hsMgrModel.updateActiveList(true);
            } else if (c.isConStunned()) {
                // do nothing
            } else if (c.isActive() && mouseButton.equals(MouseButton.PRIMARY)) {
                c.status.set(Combatant.Status.acted);
                hsMgrModel.updateActiveList(true);
            } else if (c.isActive() && mouseButton.equals(MouseButton.SECONDARY)) {
                c.status.set(Combatant.Status.heldAction);
                hsMgrModel.updateActiveList(true);
            } else if (c.hasHeldAction() && mouseButton.equals(MouseButton.PRIMARY)) {
                c.status.set(Combatant.Status.acted);
                hsMgrModel.updateActiveList(false);
            } else if (c.hasActed() && mouseButton.equals(MouseButton.SECONDARY)) {
                c.status.set(Combatant.Status.heldAction);
                hsMgrModel.updateActiveList(false);
            }
        });

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

//        SplitPane splitPane = new SplitPane();
//        splitPane.getItems().add(active);
//        splitPane.getItems().addAll(tableView);
//        root.setCenter(splitPane);

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
        int nextSeg = Integer.parseInt(hsMgrModel.currentSegment.getValue()) + 1;
        if (nextSeg == 13) nextSeg = 1;

        List<Combatant> heldActions = hsMgrModel.getAllWithHeldActions(nextSeg);

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

        if (hsMgrModel.anyUnacted()) {
            nextButton.setDisable(true);
        } else {
            nextButton.setDisable(false);
        }
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

        result.getColumns().addAll(name, currStun, stun, rec, con, body, currBody);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(createActionsMenu());
        result.setContextMenu(contextMenu);

        return result;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
