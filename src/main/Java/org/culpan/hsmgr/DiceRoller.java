package org.culpan.hsmgr;

import java.util.Random;

/**
 * Created by USUCUHA on 12/9/2016.
 */
public class DiceRoller {
    public final static Random random = new Random();

    public int[] rollDice(float numDice, int numSides) {
        int[] result;
        if (numDice % 1 != 0) {
            result = new int[(int)numDice + 1];
        } else {
            result = new int[(int)numDice];
        }

        for (int i = 0; i < (int)numDice; i++) {
            result[i] = random.nextInt(numSides) + 1;
        }

        if (numDice % 1 != 0) {
            result[(int)numDice] = (int)(((double)(random.nextInt(numSides) + 1) * (numDice % 1)) + 0.5);
        }

        return result;
    }

    public int[] rollDice(int numDice, int numSides) {
        int[] result = new int[numDice];

        for (int i = 0; i < numDice; i++) {
            result[i] = random.nextInt(numSides) + 1;
        }

        return result;
    }

    public int rollTotal(int numDice, int numSides) {
        return total(rollDice(numDice, numSides));
    }

    public int total(int dice[]) {
        int total = 0;
        for (int i = 0; i < dice.length; i++) {
            total += dice[i];
        }
        return total;
    }
}
