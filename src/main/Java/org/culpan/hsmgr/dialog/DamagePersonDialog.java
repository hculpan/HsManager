package org.culpan.hsmgr.dialog;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.culpan.hsmgr.Combatant;
import org.culpan.hsmgr.DiceRoller;

import java.util.Arrays;

/**
 * Created by harryculpan on 12/11/16.
 */
public class DamagePersonDialog<T> extends Dialog<Pair<Integer, Integer>> {
    static TextField stunOutputField;

    static TextField bodyOutputField;

    static TextField kbOutputField;

    static ToggleGroup defensesToggleGroup;

    static ToggleGroup kbToggleGroup;

    static ToggleGroup damageTypeGroup;

    static Label diceOutput;

    static Label conStunnedOutput;

    final static int NND = Integer.MAX_VALUE;

    final static int NO_DEF_FULL_BODY = 0;

    static int lastDice[];

    static int lastKnockback[];

    static Combatant combatant;

    static int finalStunDamage;

    static int finalBodyDamage;

    public static DamagePersonDialog<Pair<Integer, Integer>> init(Combatant selectedCombatant) {
        finalStunDamage = 0;
        finalBodyDamage = 0;

        DamagePersonDialog<Pair<Integer, Integer>> dialog = new DamagePersonDialog<>();
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

        damageTypeGroup = new ToggleGroup();
        RadioButton normalButton = new RadioButton("Normal Damage");
        normalButton.setToggleGroup(damageTypeGroup);
        normalButton.setSelected(true);
        grid.add(normalButton, 0, 0);
        RadioButton killingButton = new RadioButton("Killing Damage");
        killingButton.setToggleGroup(damageTypeGroup);
        killingButton.setDisable(true);
        grid.add(killingButton, 0, 1);

        defensesToggleGroup = new ToggleGroup();
        grid.add(buildRadioButton("Full PD [" + selectedCombatant.getPd() + "]", defensesToggleGroup, true), 0, 2);
        grid.add(buildRadioButton("Full ED [" + selectedCombatant.getEd() + "]", defensesToggleGroup), 0, 3);
        grid.add(buildRadioButton("Half PD [" + Math.round((selectedCombatant.getPd()/2.0) + 0.1) + "]", defensesToggleGroup), 0, 4);
        grid.add(buildRadioButton("Half ED [" + Math.round((selectedCombatant.getPd()/2.0) + 0.1) + "]", defensesToggleGroup), 0, 5);
        grid.add(buildRadioButton("NND [0]", defensesToggleGroup), 0, 6);
        grid.add(buildRadioButton("No Def - Full Body [0]", defensesToggleGroup), 0, 7);

        HBox customDmgBox = new HBox();
        Label customDmgLabel = new Label("Custom Dmg: ");
        customDmgLabel.setAlignment(Pos.CENTER_RIGHT);
        Button rollButton = new Button("Roll");
        rollButton.setDisable(true);
        TextField customDmgField = new TextField();
        customDmgField.setPrefWidth(125);
        customDmgField.textProperty().addListener((observable, oldValue, newValue) -> {
            rollButton.setDisable(!newValue.matches("\\d+"));
        });
        rollButton.setOnAction(event -> rollDamage(Integer.parseInt(customDmgField.getText()), selectedCombatant));
        customDmgBox.getChildren().addAll(customDmgLabel, customDmgField, rollButton);
        grid.add(customDmgBox, 1, 0);

        diceOutput = new Label();
        diceOutput.setAlignment(Pos.CENTER);
        grid.add(diceOutput, 1, 1);

        stunOutputField = new TextField();
        grid.add(buildResponseField("Stun: ", stunOutputField), 1, 2);
        conStunnedOutput = new Label();
        conStunnedOutput.setAlignment(Pos.CENTER);
        grid.add(conStunnedOutput, 1, 3);
        bodyOutputField = new TextField();
        grid.add(buildResponseField("Body: ", bodyOutputField), 1, 4);
        kbOutputField = new TextField();
        grid.add(buildResponseField("KB: ", kbOutputField), 1, 6);

        kbToggleGroup = new ToggleGroup();
        RadioButton kb1diceRButton = new RadioButton("1d6  ");
        kb1diceRButton.setToggleGroup(kbToggleGroup);
        kb1diceRButton.setOnAction(event -> displayDamage());
        kb1diceRButton.setUserData(1);
        RadioButton kb2diceRButton = new RadioButton("2d6  ");
        kb2diceRButton.setToggleGroup(kbToggleGroup);
        kb2diceRButton.setSelected(true);
        kb2diceRButton.setOnAction(event -> displayDamage());
        kb2diceRButton.setUserData(2);
        RadioButton kb3diceRButton = new RadioButton("3d6");
        kb3diceRButton.setToggleGroup(kbToggleGroup);
        kb3diceRButton.setOnAction(event -> displayDamage());
        kb3diceRButton.setUserData(3);
        HBox kbBox = new HBox();
        kbBox.setAlignment(Pos.CENTER);
        kbBox.getChildren().addAll(kb1diceRButton, kb2diceRButton, kb3diceRButton);
        grid.add(kbBox, 1, 7);

        grid.add(buildDiceButton(9, selectedCombatant), 2, 0);
        grid.add(buildDiceButton(10, selectedCombatant), 2, 1);
        grid.add(buildDiceButton(11, selectedCombatant), 2, 2);
        grid.add(buildDiceButton(12, selectedCombatant), 2, 3);
        grid.add(buildDiceButton(14, selectedCombatant), 2, 4);
        grid.add(buildDiceButton(16, selectedCombatant), 2, 5);
        grid.add(buildDiceButton(18, selectedCombatant), 2, 6);
        grid.add(buildDiceButton(20, selectedCombatant), 2, 7);

        borderPane.setCenter(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Pair<Integer, Integer> result = new Pair<>(finalStunDamage, finalBodyDamage);
                return result;
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
        spacer.setPrefWidth(75);
        hBox.getChildren().addAll(label, textField, spacer);
        textField.setEditable(false);
        return hBox;
    }

    protected static void rollDamage(final int numDice, final Combatant c) {
        DiceRoller diceRoller = new DiceRoller();
        lastDice = diceRoller.rollDice(numDice, 6);
        Arrays.sort(lastDice);
        lastKnockback = diceRoller.rollDice(3, 6);
        combatant = c;
        displayDamage();
    }

    protected static void displayDamage() {
        if (lastDice == null || lastKnockback == null || combatant == null) return;

        DiceRoller diceRoller = new DiceRoller();
        int defense = getDefenseValue(combatant);
        int stunTotal = diceRoller.total(lastDice);
        int bodyTotal = diceRoller.bodyTotal(lastDice);
        int bodyDamage;
        if (defense == NND) {
            bodyDamage = 0;
            defense = 0;
        } else {
            bodyDamage = (bodyTotal - defense > 0 ? bodyTotal - defense : 0);
        }
        int stunDamage = (stunTotal - defense > 0 ? stunTotal - defense : 0);

        finalStunDamage = stunDamage;
        finalBodyDamage = bodyDamage;

        stunOutputField.setText(Integer.toString(stunDamage));
        bodyOutputField.setText(Integer.toString(bodyDamage));
        StringBuilder msg = new StringBuilder("[");
        for (int i = 0; i < lastDice.length; i++) {
            msg.append(lastDice[i]);
            if (i < lastDice.length - 1) {
                msg.append(",");
            }
        }
        msg.append("] Avg = ");
        msg.append(String.format("%1$.1f", ((float)stunTotal / (float)lastDice.length)));
        diceOutput.setText(msg.toString());

        if (stunDamage > combatant.getCon()) {
            conStunnedOutput.setText("will be CON stunned");
        } else {
            conStunnedOutput.setText("");
        }

        displayKnockback();
    }

    private static void displayKnockback() {
        DiceRoller diceRoller = new DiceRoller();
        int bodyTotal = diceRoller.bodyTotal(lastDice);
        int numKbDice = (Integer)kbToggleGroup.getSelectedToggle().getUserData();
        int kbResistance = diceRoller.total(Arrays.copyOf(lastKnockback, numKbDice));

        StringBuilder kbMsg = new StringBuilder();
        kbMsg.append((bodyTotal - kbResistance > 0 ? bodyTotal - kbResistance : 0));
        kbMsg.append(" [");
        kbMsg.append(lastKnockback[0]);
        if (numKbDice > 1) {
            kbMsg.append(",");
            kbMsg.append(lastKnockback[1]);
        }
        if (numKbDice > 2){
            kbMsg.append(",");
            kbMsg.append(lastKnockback[2]);
        }
        kbMsg.append("]");
        kbOutputField.setText(kbMsg.toString());
    }

    protected static Button buildDiceButton(final int numDice, Combatant c) {
        Button result = new Button();

        result.setText(Integer.toString(numDice) + "d6");
        result.setPrefWidth(100);
        result.setOnAction(event -> rollDamage(numDice, c));

        return result;
    }

    private static int getDefenseValue(Combatant c) {
        Toggle toggle = defensesToggleGroup.getSelectedToggle();
        String text = toggle.getUserData().toString();
        int result = NO_DEF_FULL_BODY;
        if (text.startsWith("Full PD")) {
            result = c.getPd();
        } else if (text.startsWith("Full ED")) {
            result = c.getEd();
        } else if (text.startsWith("Half PD")) {
            result = (int)Math.round((c.getPd()/2.0) + 0.1);
        } else if (text.startsWith("Half ED")) {
            result = (int)Math.round((c.getEd()/2.0) + 0.1);
        } else if (text.startsWith("NND")) {
            result = NND;
        }

        return result;
    }

    protected static RadioButton buildRadioButton(String text, ToggleGroup group) {
        return buildRadioButton(text, group, false);
    }

    protected static RadioButton buildRadioButton(String text, ToggleGroup group, boolean toggled) {
        RadioButton result = new RadioButton(text);
        result.setToggleGroup(group);
        result.setSelected(toggled);
        result.setUserData(text);
        result.setOnAction(event -> displayDamage());
        return result;
    }
}
