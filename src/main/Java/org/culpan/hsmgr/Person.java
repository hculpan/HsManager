package org.culpan.hsmgr;

/**
 * Created by USUCUHA on 12/8/2016.
 */
public class Person {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCon() {
        return con;
    }

    public void setCon(int con) {
        this.con = con;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getRec() {
        return rec;
    }

    public void setRec(int rec) {
        this.rec = rec;
    }

    public int getStun() {
        return stun;
    }

    public void setStun(int stun) {
        this.stun = stun;
    }

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getPd() {
        return pd;
    }

    public void setPd(int pd) {
        this.pd = pd;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public int getDcv() {
        return dcv;
    }

    public void setDcv(int dcv) {
        this.dcv = dcv;
    }

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

    static public Person createPerson(String name, int con, int dex, int rec,
                                      int body, int stun, int spd, int pd, int ed, int dcv) {
        Person p = new Person();
        p.name = name;
        p.con = con;
        p.dex = dex;
        p.rec = rec;
        p.body = body;
        p.stun = stun;
        p.spd = spd;
        p.pd = pd;
        p.ed = ed;
        p.dcv = dcv;

        return p;
    }
}
