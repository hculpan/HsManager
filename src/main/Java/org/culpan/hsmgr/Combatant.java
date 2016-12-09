package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;

/**
 * Created by USUCUHA on 12/8/2016.
 */
public class Combatant extends Person {
    volatile int currentStun;

    volatile int currentBody;

    public int getCurrentStun() {
        return currentStun;
    }

    public void setCurrentStun(int currentStun) {
        this.currentStun = currentStun;
    }

    public int getCurrentBody() {
        return currentBody;
    }

    public void setCurrentBody(int currentBody) {
        this.currentBody = currentBody;
    }

    public boolean hasActed() {
        return acted.getValue();
    }

    public void setActed(boolean acted) {
        this.acted.setValue(acted);
    }

    BooleanProperty acted = new SimpleBooleanProperty();

    public static Callback<Combatant, Observable[]> extractor() {
        return param -> new Observable[]{param.acted};
    }

    static public Combatant createCombatant(String name, int con, int dex, int rec,
                                            int body, int stun, int spd, int pd, int ed,
                                            int dcv) {
        Combatant c = new Combatant();
        c.name = name;
        c.con = con;
        c.dex = dex;
        c.rec = rec;
        c.body = body;
        c.stun = stun;
        c.spd = spd;
        c.pd = pd;
        c.ed = ed;
        c.dcv = dcv;
        c.currentBody = body;
        c.currentStun = stun;

        return c;
    }
}
