package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USUCUHA on 12/8/2016.
 */
public class HsMgrModel {
    public ObservableList<Combatant> currentActive = FXCollections.observableArrayList();

    public ObservableList<Combatant> allCombatants = FXCollections.observableArrayList();

    public StringProperty currentTurn = new SimpleStringProperty("1");

    public StringProperty currentSegment = new SimpleStringProperty("11");

    public HsMgrModel() {
        Combatant c = Combatant.createCombatant("Pulsar", 20, 18, 15, 70, 15, 9, 15, 15, 9);
        allCombatants.add(c);
        c = Combatant.createCombatant("Night Shadow", 45, 18, 50, 100, 20, 6, 35, 35, 9);
        allCombatants.add(c);

        addActiveListChangeListener();

        onNext();
    }

    public void addActiveListChangeListener() {
        currentSegment.addListener((Observable o) -> {
            int currSeg = Integer.parseInt(currentSegment.getValue());
            System.out.printf("Change listener called, segment = %d\n", currSeg);

            List<Combatant> active = new ArrayList<>();
            for (Combatant c : allCombatants) {
                if (c.isInPhase(currSeg)) {
                    active.add(c);
                }
            }

            currentActive.clear();
            currentActive.addAll(active);
        });
    }

    public void onNext() {
        int currSeg = Integer.parseInt(currentSegment.getValue());
        int currTurn = Integer.parseInt(currentTurn.getValue());

        currSeg++;
        if (currSeg > 12) {
            currTurn++;
            currSeg -= 12;
        }

        currentSegment.setValue(Integer.toString(currSeg));
        currentTurn.setValue(Integer.toString(currTurn));
    }

    public void onQuit() {
        System.exit(0);
    }
}
