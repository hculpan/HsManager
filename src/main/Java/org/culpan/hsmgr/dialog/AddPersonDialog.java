package org.culpan.hsmgr.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.culpan.hsmgr.Combatant;

import java.util.Optional;

/**
 * Created by harryculpan on 12/11/16.
 */
public class AddPersonDialog<T> extends Dialog<Combatant> {
    public static AddPersonDialog<Combatant> init(Combatant c, String title) {
        AddPersonDialog<Combatant> dialog = new AddPersonDialog<>();
        dialog.setTitle(title);
        if (c == null) {
            dialog.setHeaderText("Enter the new combatant's information");
        } else {
            dialog.setHeaderText("Enter " + c.getName() + "'s information");
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = addFieldToPersonDialog(grid, "Name", 0, (c != null ? c.getName() : null));
        TextField con = addFieldToPersonDialog(grid, "CON", 1, (c != null ? c.getCon() : null));
        TextField dex = addFieldToPersonDialog(grid, "DEX", 2, (c != null ? c.getDex() : null));
        TextField rec = addFieldToPersonDialog(grid, "REC", 3, (c != null ? c.getRec() : null));
        TextField spd = addFieldToPersonDialog(grid, "SPD", 4, (c != null ? c.getSpd() : null));
        TextField stun = addFieldToPersonDialog(grid, "STUN", 5, (c != null ? c.getStun() : null));
        TextField body = addFieldToPersonDialog(grid, "BODY", 6, (c != null ? c.getBody() : null));
        TextField pd = addFieldToPersonDialog(grid, "PD", 7, (c != null ? c.getPd() : null));
        TextField ed = addFieldToPersonDialog(grid, "ED", 8, (c != null ? c.getEd() : null));
        TextField dcv = addFieldToPersonDialog(grid, "DCV", 9, (c != null ? c.getDcv() : null));
        CheckBox player = addCheckBoxToPersonDialog(grid, "Player", 10, (c != null ? c.isPlayer() : null));

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
                        player.isSelected());
            }
            return null;
        });

        return dialog;
    }

    protected static CheckBox addCheckBoxToPersonDialog(GridPane grid, String name, int row, Boolean value) {
        CheckBox result = new CheckBox(name);

        grid.add(result, 1, row);
        if (value != null) {
            result.setSelected(value);
        }

        return result;
    }

    protected static TextField addFieldToPersonDialog(GridPane grid, String name, int row, String value) {
        TextField result = new TextField();

        if (value != null) {
            result.setText(value);
        } else {
            result.setPromptText(name);
        }

        grid.add(new Label(name + ": "), 0, row);
        grid.add(result, 1, row);

        return result;
    }

    protected static TextField addFieldToPersonDialog(GridPane grid, String name, int row, Integer value) {
        TextField result = new TextField();

        if (value != null) {
            result.setText(value.toString());
        } else {
            result.setPromptText(name);
        }

        grid.add(new Label(name + ": "), 0, row);
        grid.add(result, 1, row);

        return result;
    }

}
