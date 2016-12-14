package org.culpan.hsmgr.dialog;

import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.culpan.hsmgr.Combatant;
import org.culpan.hsmgr.DiceRoller;

import javax.swing.*;

/**
 * Created by harryculpan on 12/11/16.
 */
public class DamagePersonDialog<T> extends Dialog<Combatant> {
    public static DamagePersonDialog<Combatant> init(Combatant selectedCombatant) {
        DamagePersonDialog<Combatant> dialog = new DamagePersonDialog<>();
        dialog.setTitle("Damage " + selectedCombatant.getName());
        dialog.setHeaderText("Determine the damage done to " + selectedCombatant.getName());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        BorderPane borderPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(5);

        HBox topBox = new HBox();
        topBox.setSpacing(10);
        Button rollAttack = new Button("Roll Attack");
        rollAttack.setDisable(false);
        TextField dcvField = new TextField();
        dcvField.textProperty().addListener((observable, oldValue, newValue) -> {
            rollAttack.setDisable(!newValue.matches("\\d+"));
        });

        dcvField.setText(Integer.toString(selectedCombatant.getDcv()));
        TextField attackResult = new TextField();
        attackResult.setEditable(false);
        rollAttack.setOnAction(event -> {
            DiceRoller diceRoller = new DiceRoller();
            int dcv = Integer.parseInt(dcvField.getText()) - 11;
            int dice[] = diceRoller.rollDice(3, 6);
            int total = diceRoller.total(dice);
            attackResult.setText("OCV " + Integer.toString(dcv + total) + "+ [" +
                    dice[0] + "," +
                    dice[1] + "," +
                    dice[2] + "]");
        });
        topBox.getChildren().addAll(new Label("DCV:"), dcvField, rollAttack, attackResult);

        Separator sep = new Separator();
        sep.setOrientation(Orientation.HORIZONTAL);
        sep.setValignment(VPos.CENTER);
        vBox.getChildren().addAll(topBox, sep);

        borderPane.setTop(vBox);
        dialog.getDialogPane().setContent(borderPane);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.CENTER);
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.RIGHT);
        column2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(column2);

        ToggleGroup defensesGroup = new ToggleGroup();
        grid.add(buildRadioButton("Full PD", defensesGroup, true), 0, 2);
        grid.add(buildRadioButton("Full ED", defensesGroup), 0, 3);
        grid.add(buildRadioButton("Half PD", defensesGroup), 0, 4);
        grid.add(buildRadioButton("Half ED", defensesGroup), 0, 5);
        grid.add(buildRadioButton("No Defense", defensesGroup), 0, 6);

        TextField stunOutput = new TextField();
        grid.add(buildResponseField("Stun: ", stunOutput), 1, 2);
        TextField bodyOutput = new TextField();
        grid.add(buildResponseField("Body: ", bodyOutput), 1, 4);
        TextField kbOutput = new TextField();
        grid.add(buildResponseField("KB: ", kbOutput), 1, 6);

        grid.add(buildDiceButton(8), 2, 0);
        grid.add(buildDiceButton(10), 2, 1);
        grid.add(buildDiceButton(12), 2, 2);
        grid.add(buildDiceButton(14), 2, 3);
        grid.add(buildDiceButton(16), 2, 4);
        grid.add(buildDiceButton(18), 2, 5);
        grid.add(buildDiceButton(20), 2, 6);

        borderPane.setCenter(grid);

        //Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
            }
            return null;
        });

        return dialog;
    }

    protected static HBox buildResponseField(String text, TextField textField) {
        HBox hBox = new HBox();
        Label label = new Label(text);
        label.setPrefWidth(75);
        label.setAlignment(Pos.CENTER_RIGHT);
        Label spacer = new Label();
        spacer.setPrefWidth(50);
        hBox.getChildren().addAll(label, textField, spacer);
        textField.setEditable(false);
        return hBox;
    }

    protected static Button buildDiceButton(int dice) {
        Button result = new Button();

        result.setText(Integer.toString(dice) + "d6");
        result.setPrefWidth(100);

        return result;
    }

    protected static RadioButton buildRadioButton(String text, ToggleGroup group) {
        return buildRadioButton(text, group, false);
    }

    protected static RadioButton buildRadioButton(String text, ToggleGroup group, boolean toggled) {
        RadioButton result = new RadioButton(text);
        result.setToggleGroup(group);
        result.setSelected(toggled);
        return result;
    }
}
