package org.culpan.hsmgr;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by USUCUHA on 12/8/2016.
 */
@XmlRootElement
@XmlType(propOrder={"name", "player", "con", "dex", "rec", "spd", "stun", "body", "pd", "ed", "dcv"})
public class Combatant {
    public enum Status { unacted, heldAction, aborted, acted, recovered, conStunned, unconscious, dead };

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


    StringProperty name = new SimpleStringProperty();

    IntegerProperty con = new SimpleIntegerProperty();

    IntegerProperty dex = new SimpleIntegerProperty();

    IntegerProperty rec = new SimpleIntegerProperty();

    IntegerProperty stun = new SimpleIntegerProperty();

    IntegerProperty body = new SimpleIntegerProperty();

    IntegerProperty pd = new SimpleIntegerProperty();

    IntegerProperty ed = new SimpleIntegerProperty();

    IntegerProperty spd = new SimpleIntegerProperty();

    IntegerProperty dcv = new SimpleIntegerProperty();

    BooleanProperty player = new SimpleBooleanProperty(false);

    volatile boolean conStunnedAwaitingRecovery = false;

    volatile int abortSegment = 0;

    volatile int flashed = 0;

    volatile int turnsUnconscious = 0;

    volatile int lastRecoverAmount = 0;

    final BooleanProperty active = new SimpleBooleanProperty(false);

    @XmlTransient
    public boolean isActive() { return active.get(); }

    public BooleanProperty getActiveProperty() { return active; }

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
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @XmlElement
    public int getCon() {
        return con.get();
    }

    public void setCon(int con) {
        this.con.set(con);
    }

    @XmlElement
    public int getDex() {
        return dex.get();
    }

    public void setDex(int dex) {
        this.dex.set(dex);
    }

    @XmlElement
    public int getRec() {
        return rec.get();
    }

    public void setRec(int rec) {
        this.rec.set(rec);
    }

    @XmlElement
    public int getStun() {
        return stun.get();
    }

    public void setStun(int stun) {
        this.stun.set(stun);
    }

    @XmlElement
    public int getBody() {
        return body.get();
    }

    public void setBody(int body) {
        this.body.set(body);
    }

    @XmlElement
    public int getPd() {
        return pd.get();
    }

    public void setPd(int pd) {
        this.pd.set(pd);
    }

    @XmlElement
    public int getEd() {
        return ed.get();
    }

    public void setEd(int ed) {
        this.ed.set(ed);
    }

    @XmlElement
    public int getSpd() {
        return spd.get();
    }

    public void setSpd(int spd) {
        this.spd.set(spd);
    }

    @XmlElement
    public int getDcv() {
        return dcv.get();
    }

    public void setDcv(int dcv) {
        this.dcv.set(dcv);
    }

    @XmlElement
    public boolean isPlayer() {
        return player.get();
    }

    public void setPlayer(boolean player) {
        this.player.set(player);
    }

    @XmlTransient
    public boolean isFlashed() {
        return flashed > 0;
    }

    @XmlTransient
    public int getFlashed() {
        return flashed;
    }

    public void setFlashed(int flashed) {
        this.flashed = flashed;
    }

    @XmlTransient
    public boolean isConStunnedAwaitingRecovery() {
        return conStunnedAwaitingRecovery;
    }

    public void setConStunnedAwaitingRecovery(boolean conStunnedAwaitingRecovery) {
        this.conStunnedAwaitingRecovery = conStunnedAwaitingRecovery;
    }

    public boolean hasHeldAction() { return this.status.get().equals(Status.heldAction); }

    public boolean hasActed() { return this.status.get().equals(Status.acted); }

    public boolean hasNotActed() { return this.status.get().equals(Status.unacted); }

    public boolean hasRecovered() { return this.status.get().equals(Status.recovered); }

    public boolean hasAborted() { return this.status.get().equals(Status.aborted); }

    @XmlTransient
    public boolean isConStunned() { return this.status.get().equals(Status.conStunned); }

    @XmlTransient
    public boolean isUnconscious() { return this.status.get().equals(Status.unconscious); }

    @XmlTransient
    public boolean isFullStun() { return getCurrentStun() == getStun(); }

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

        c.name.set(name);
        c.con.set(con);
        c.dex.set(dex);
        c.rec.set(rec);
        c.body.set(body);
        c.stun.set(stun);
        c.spd.set(spd);
        c.pd.set(pd);
        c.ed.set(ed);
        c.dcv.set(dcv);
        c.currentBody.setValue(body);
        c.currentStun.setValue(stun);
        c.player.set(player);

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
        result.conStunnedAwaitingRecovery = this.conStunnedAwaitingRecovery;
        result.abortSegment = this.abortSegment;
        result.turnsUnconscious = this.turnsUnconscious;
        result.active.set(this.active.get());
        result.flashed = this.flashed;

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

        if (this.getDex() > c.getDex()) {
            result = -1;
        } else if (this.getDex() < c.getDex()) {
            result = 1;
        } else if (this.getName().hashCode() > c.getName().hashCode()) {
            result = -1;
        } else {
            return 1;
        }

        return result;
    }

    public void damage(int stun, int body) {
        this.currentStun.setValue(this.currentStun.getValue() - stun);
        if (currentStun.get() <= 0) {
            conStunnedAwaitingRecovery = false;
            status.set(Status.unconscious);
        } else if (stun > getCon()) {
            stun();
        }
        this.currentBody.setValue(this.currentBody.getValue() - body);
    }

    public void stun() {
        if (!isConStunned()) {
            conStunnedAwaitingRecovery = (!hasHeldAction() && !isActive());
            status.set(Status.conStunned);
        }
    }

    public void unstun() {
        if (isConStunned()) {
            if (conStunnedAwaitingRecovery) {
                status.set(Status.unacted);
            } else {
                status.set(Status.heldAction);
            }
            conStunnedAwaitingRecovery = false;
        }
    }

    public void heal(int stun) {
        heal(stun, 0);
    }

    public void heal(int stun, int body) {
        if (stun + getCurrentStun() > getStun()) {
            currentStun.setValue(getStun());
        } else {
            currentStun.set(getCurrentStun() + stun);
        }

        if (body + getCurrentBody() > getBody()) {
            currentBody.set(getBody());
        } else {
            currentBody.set(body + getCurrentBody());
        }
    }

    /**
     * This method is called at the start of the combatant's
     * turn in the phase
     * @param phase
     */
    public void startingAction(int phase) {
        if (isConStunned() && conStunnedAwaitingRecovery) {
            conStunnedAwaitingRecovery = false;
        } else if (hasAborted() && abortSegment == 0) {
            abortSegment = phase;
        } else if (isUnconscious() && getCurrentStun() > -11) {
            heal(getRec());
        }
    }

    public void startingPhase(int phase) {
        if (isConStunned() && !conStunnedAwaitingRecovery) {
            status.set(Status.unacted);
        } else if (hasAborted() && abortSegment > 0) {
            status.set(Status.unacted);
        } else if (hasActed() || hasHeldAction()) {
            status.set(Status.unacted);
        } else if (isUnconscious() && getCurrentStun() > 0) {
            status.set(Status.unacted);
        } else if (hasRecovered()) {
            status.set(Status.unacted);
        }
    }

    public void postSegment12() {
        if (isUnconscious() && getCurrentStun() < -20 && getCurrentStun() > -31 && Main.hsMgrModel.getCurrentTurn() > 1) {
            turnsUnconscious++;
            if (turnsUnconscious == 5) {
                heal(getRec());
                turnsUnconscious = 0;
            }
        } else if (isUnconscious() && getCurrentStun() < -31) {
            // do nothing
        } else {
            heal(getRec());
        }
    }

    public void recover() {
        if (this.getStun() - this.getCurrentStun() < getRec()) {
            lastRecoverAmount = this.getStun() - this.getCurrentStun();
        } else {
            lastRecoverAmount = getRec();
        }
        heal(getRec());
        status.set(Status.recovered);
    }

    public boolean canAbort(int phase) {
        if (hasActed() && isInPhase(phase)) {
            return false;
        } else if (isUnconscious()) {
            return false;
        } else if (isConStunned() && conStunnedAwaitingRecovery) {
            return false;
        } else if (hasAborted() && abortSegment == 0) {
            return false;
        } else if (hasAborted() && abortSegment == phase) {
            return false;
        }

        return true;
    }

    public void abort(int currPhase) {
        if (isActive() || hasHeldAction()) {
            abortSegment = currPhase;
        } else {
            abortSegment = 0;
        }
        status.set(Status.aborted);
    }

    public void reduceFlash() {
        if (flashed > 0) flashed--;
    }

    public static Callback<Combatant, Observable[]> extractor() {
        return (Combatant c) -> new Observable[] {
                c.name, c.con, c.dex, c.rec, c.stun, c.body, c.pd, c.ed, c.dcv, c.player
        };
    }
}
