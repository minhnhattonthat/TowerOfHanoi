package com.nhatton.towerofhanoi;

/**
 * Created by nhatton on 24/2/18.
 */

interface OnGameCompleteListener {
    void onGameComplete(boolean isOptimal, int moves, int numDisks);
}
