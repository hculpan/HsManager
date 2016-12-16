package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    public ObservableList<Combatant> allCombatants = FXCollections.observableArrayList(Combatant.extractor());

    public StringProperty currentTurn = new SimpleStringProperty("0");

    public StringProperty currentSegment = new SimpleStringProperty("11");

    public BooleanProperty doneWithPhase = new SimpleBooleanProperty(true);

    protected boolean startingNextPhase = true;

    public HsMgrModel() {
        addActiveListChangeListener();
        onNext();
    }

    public int getCurrentSegement() {
        return Integer.parseInt(currentSegment.get());
    }

    public int getCurrentTurn() {
        return Integer.parseInt(currentTurn.get());
    }

    public int currentActiveIndex() {
        int result = -1;

        int i = 0;
        for (Combatant c : currentActive) {
            if (c.isActive()) {
                result = i;
                break;
            }
            i++;
        }

        return result;
    }

    public Combatant getCurrentActive() {
        Combatant result = null;
        int index = currentActiveIndex();
        if (index >= 0) {
            result = currentActive.get(index);
        }
        return result;
    }

    protected void nextActive() {
        int activeIndex = currentActiveIndex();

        if (activeIndex >= 0) {
            currentActive.get(activeIndex).getActiveProperty().set(false);
            activeIndex++;
        } else {
            activeIndex = 0;
        }

        boolean haveNextActive = false;
        for (int i = activeIndex; i < currentActive.size(); i++) {
            Combatant c = currentActive.get(i);
            if (c.hasNotActed()) {
                c.getActiveProperty().set(true);
                c.startingAction(getCurrentSegement());
                haveNextActive = true;
                break;
            }
            c.startingAction(getCurrentSegement());
        }

        doneWithPhase.setValue(!haveNextActive);
    }

    protected void updateActiveList() {
        updateActiveList(false);
    }

    protected void updateActiveList(boolean nextAction) {
        int currSeg = Integer.parseInt(currentSegment.getValue());

        List<Combatant> active = new ArrayList<>();
        for (Combatant c : allCombatants) {
            if (c.isInPhase(currSeg)) {
                active.add(c);
                if (startingNextPhase) {
                    c.startingPhase(currSeg);
                }
            } else if (c.hasHeldAction()) {
                active.add(c);
            }
        }
        active.sort((o1, o2) -> o1.actsBefore(o2));

        currentActive.clear();
        currentActive.addAll(active);

        if (nextAction || startingNextPhase) nextActive();
        startingNextPhase = false;
    }

    public void addActiveListChangeListener() {
        currentSegment.addListener((Observable o) ->  updateActiveList());

        allCombatants.addListener((Observable o) -> updateActiveList());
    }

    public void onNext() {
        int currSeg = Integer.parseInt(currentSegment.getValue());
        int currTurn = Integer.parseInt(currentTurn.getValue());

        startingNextPhase = true;

        for (Combatant c : allCombatants) {
            c.reduceFlash();
        }

        doneWithPhase.set(false);

        currSeg++;
        if (currSeg > 12) {
            for (Combatant c : allCombatants) {
                c.postSegment12();
            }

            currSeg -= 12;
        } else if (currSeg == 12) {
            currTurn++;
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
            c.conStunnedAwaitingRecovery = false;
            c.getActiveProperty().set(false);
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

    public void damage(Combatant c, int stun, int body) {
        c.damage(stun, body);
        if (c.isActive() && (c.isConStunned() || c.isUnconscious())) {
            updateActiveList(true);
        } else {
            updateActiveList();
        }
    }

    public List<Combatant> getAllWithHeldActions(int segment) {
        List<Combatant> heldActions = new ArrayList<>();

        for (Combatant c : allCombatants) {
            if (c.isInPhase(segment) && c.hasHeldAction()) {
                heldActions.add(c);
            }
        }

        return heldActions;
    }

    public void start() {
        updateActiveList(true);
    }
}
