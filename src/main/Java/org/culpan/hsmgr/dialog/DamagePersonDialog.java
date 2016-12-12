package org.culpan.hsmgr.dialog;

import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.culpan.hsmgr.Combatant;
import org.culpan.hsmgr.DiceRoller;

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

/*        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = addFieldToPersonDialog(grid, "Name", 0, (String)null);
        TextField con = addFieldToPersonDialog(grid, "CON", 1, (Integer)null);
        TextField dex = addFieldToPersonDialog(grid, "DEX", 2, (Integer)null);
        TextField rec = addFieldToPersonDialog(grid, "REC", 3, (Integer)null);
        TextField spd = addFieldToPersonDialog(grid, "SPD", 4, (Integer)null);
        TextField stun = addFieldToPersonDialog(grid, "STUN", 5, (Integer)null);
        TextField body = addFieldToPersonDialog(grid, "BODY", 6, (Integer)null);
        TextField pd = addFieldToPersonDialog(grid, "PD", 7, (Integer)null);
        TextField ed = addFieldToPersonDialog(grid, "ED", 8, (Integer)null);
        TextField dcv = addFieldToPersonDialog(grid, "DCV", 9, (Integer)null);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return Combatant.createCombatant(name.getText(),
                        Integer.parseInt(con.getText()),
                        Integer.parseInt(dex.getText()),
                        Integer.parseInt(rec.getText()),
                        Integer.parseInt(body.getText()),
                        Integer.parseInt(stun.getText()),
                        Integer.parseInt(spd.getText()),
                        Integer.parseInt(pd.getText()),
                        Integer.parseInt(ed.getText()),
                        Integer.parseInt(dcv.getText()),
                        false);
            }
            return null;
        });*/

        return dialog;
    }

}
