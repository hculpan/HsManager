package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by USUCUHA on 12/8/2016.
 */
public class HsMgrModel {
    public ObservableList<Combatant> currentActive = FXCollections.observableArrayList(Combatant.listExtractor());

    public ObservableList<Combatant> allCombatants = FXCollections.observableArrayList();

    public StringProperty currentTurn = new SimpleStringProperty("1");

    public StringProperty currentSegment = new SimpleStringProperty("11");

    protected boolean startingNextPhase = true;

    public HsMgrModel() {
        addActiveListChangeListener();
        onNext();
    }

    protected void updateActiveList() {
        System.out.println("updateActiveList called");
        int currSeg = Integer.parseInt(currentSegment.getValue());

        List<Combatant> active = new ArrayList<>();
        for (Combatant c : allCombatants) {
            if (c.isInPhase(currSeg)) {
                active.add(c);
                if (startingNextPhase && (c.hasActed() || c.hasHeldAction())) {
                    c.status.set(Combatant.Status.unacted);
                }
            } else if (c.hasHeldAction()) {
                active.add(c);
            }
        }
        startingNextPhase = false;
        active.sort((o1, o2) -> o1.actsBefore(o2));

        currentActive.clear();
        currentActive.addAll(active);
    }

    public void addActiveListChangeListener() {
        currentSegment.addListener((Observable o) ->  updateActiveList());

        allCombatants.addListener((Observable o) -> updateActiveList());
    }

    public void onNext() {
        int currSeg = Integer.parseInt(currentSegment.getValue());
        int currTurn = Integer.parseInt(currentTurn.getValue());

        startingNextPhase = true;

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

    public void reset() {
        currentSegment.setValue("12");
        currentTurn.setValue("1");

        for (Combatant c : allCombatants) {
            c.status.set(Combatant.Status.unacted);
            c.currentBody.setValue(c.getBody());
            c.currentStun.setValue(c.getStun());
        }

        updateActiveList();
    }

    public Combatant getCombatantByName(String name) {
        Combatant result = null;
        for (Combatant c : allCombatants) {
            if (c.getName().equalsIgnoreCase(name)) {
                result = c;
                break;
            }
        }
        return result;
    }

    public boolean allActed() {
        for (Combatant c : currentActive) {
            if (c.hasNotActed()) {
                return false;
            }
        }
        return true;
    }

    public boolean anyUnacted() {
        for (Combatant c : currentActive) {
            if (c.hasNotActed()) {
                return true;
            }
        }
        return false;
    }
}
