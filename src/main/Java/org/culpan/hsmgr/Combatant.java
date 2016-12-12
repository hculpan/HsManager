package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by USUCUHA on 12/8/2016.
 */
@XmlRootElement
@XmlType(propOrder={"name", "player", "con", "dex", "rec", "spd", "stun", "body", "pd", "ed", "dcv"})
public class Combatant {
    public enum Status { unacted, heldAction, acted, conStunned, unconscious, dead };

    public final static int[][] PHASES = {
            {},
            { 7 },
            { 6, 12 },
            { 4, 8, 12 },
            { 3, 6, 9, 12 },
            { 3, 5, 8, 10, 12 },
            { 2, 4, 6, 8, 10, 12 },
            { 2, 4, 6, 7, 9, 11, 12 },
            { 2, 3, 5, 6, 8, 9, 11, 12 },
            { 2, 3, 4, 6, 7, 8, 10, 11, 12 },
            { 2, 3, 4, 5, 6, 8, 9, 10, 11, 12 },
            { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
            { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 } };


    String name;

    int con;

    int dex;

    int rec;

    int stun;

    int body;

    int pd;

    int ed;

    int spd;

    int dcv;

    boolean player;

    final IntegerProperty currentStun = new SimpleIntegerProperty();

    final IntegerProperty currentBody = new SimpleIntegerProperty();

    final ObjectProperty<Status> status = new SimpleObjectProperty<>();

    public int getCurrentStun() {
        return currentStun.getValue();
    }

    public int getCurrentBody() {
        return currentBody.getValue();
    }

    public Status getStatus() { return status.get(); }

    public IntegerProperty getCurrentStunProperty() {
        return currentStun;
    }

    public IntegerProperty getCurrentBodyProperty() {
        return currentBody;
    }

    public ObjectProperty<Status> getStatusProperty() { return status; }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    @XmlElement
    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    @XmlElement
    public int getRec() {
        return rec;
    }

    public void setRec(int rec) {
        this.rec = rec;
    }

    @XmlElement
    public int getStun() {
        return stun;
    }

    public void setStun(int stun) {
        this.stun = stun;
    }

    @XmlElement
    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    @XmlElement
    public int getPd() {
        return pd;
    }

    public void setPd(int pd) {
        this.pd = pd;
    }

    @XmlElement
    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    @XmlElement
    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    @XmlElement
    public int getDcv() {
        return dcv;
    }

    public void setDcv(int dcv) {
        this.dcv = dcv;
    }

    @XmlElement
    public boolean isPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public boolean hasHeldAction() { return this.status.get().equals(Status.heldAction); }

    public boolean hasActed() { return this.status.get().equals(Status.acted); }

    public boolean hasNotActed() { return this.status.get().equals(Status.unacted); }

    public int[] getPhases() {
        return PHASES[getSpd()];
    }

    public boolean isInPhase(int segment) {
        for (int phase : getPhases()) {
            if (phase == segment) {
                return true;
            }
        }

        return false;
    }

    public static Callback<Combatant, Observable[]> listExtractor() {
        return param -> new Observable[]{param.status};
    }

    public static Callback<Combatant, Observable[]> tableExtractor() {
        return param -> new Observable[]{param.currentStun, param.currentBody};
    }

    static public Combatant createCombatant(String name, int con, int dex, int rec,
                                            int body, int stun, int spd, int pd, int ed,
                                            int dcv) {
        return createCombatant(name, con, dex, rec, body, stun, spd, pd, ed, dcv, false);
    }

    static public Combatant createCombatant(String name, int con, int dex, int rec,
                                            int body, int stun, int spd, int pd, int ed,
                                            int dcv, boolean player) {
        Combatant c = new Combatant();
        c.status.setValue(Status.unacted);

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
        c.currentBody.setValue(body);
        c.currentStun.setValue(stun);
        c.player = player;

        return c;
    }

    public void reset() {
        currentBody.setValue(getBody());
        currentStun.setValue(getStun());
        status.setValue(Status.unacted);
    }

    public Combatant clone() {
        Combatant result = new Combatant();

        result.setName(this.getName());
        result.setCon(this.getCon());
        result.setDcv(this.getDcv());
        result.setDex(this.getDex());
        result.setRec(this.getRec());
        result.setSpd(this.getSpd());
        result.setStun(this.getStun());
        result.setBody(this.getBody());
        result.setPlayer(this.isPlayer());
        result.setPd(this.getPd());
        result.setEd(this.getEd());
        result.status.set(this.status.get());
        result.currentBody.setValue(this.currentBody.getValue());
        result.currentStun.setValue(this.currentStun.getValue());

        return result;
    }

    /**
     * Returns -1 if this acts before the specified combatant; or
     * returns 1 if specified combatant acts before this.
     * @param c
     * @return
     */
    public int actsBefore(Combatant c) {
        int result = 0;

        if (!this.getStatus().equals(c.getStatus())) {
            if (this.getStatus().ordinal() < c.getStatus().ordinal()) {
                result = -1;
            } else {
                result = 1;
            }
        } else {
            if (this.getDex() > c.getDex()) {
                result = -1;
            } else if (this.getDex() < c.getDex()) {
                result = 1;
            } else {
                DiceRoller diceRoller = new DiceRoller();
                int n1 = diceRoller.rollTotal(1, 6);
                int n2 = diceRoller.rollTotal(1, 6);
                do {
                    if (n1 > n2) {
                        result = -1;
                    } else if (n1 < n2) {
                        result = 1;
                    }
                    n1 = diceRoller.rollTotal(1, 6);
                    n2 = diceRoller.rollTotal(1, 6);
                } while (n1 == n2);
            }
        }

        return result;
    }

    public void damage(int stun, int body) {
        this.currentStun.setValue(this.currentStun.getValue() - stun);
        if (currentStun.get() <= 0) {
            status.set(Status.unconscious);
        }
        this.currentBody.setValue(this.currentBody.getValue() - body);
    }

    public void heal(int stun, int body) {
        damage(stun * -1, body * -1);
    }
}
