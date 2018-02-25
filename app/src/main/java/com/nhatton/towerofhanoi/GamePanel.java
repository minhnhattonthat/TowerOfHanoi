package com.nhatton.towerofhanoi;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by nhatton on 24/2/18.
 */

public class GamePanel extends View {

    private Peg pegA, pegB, pegC;
    private int zone = -1;                        //Section of the GamePanel where user is touching
    private Peg startPeg;
    private Peg lastPeg;                          //The Peg where current disk was picked up from; the last Peg the disk was contained in
    int moves = 0;                                //The number of moves
    int numDisks;                                 //The number of disks for a certain game
    private boolean newGame;                      //Whether the GamePanel should recreate for new game
    private OnGameCompleteListener onGameCompleteListener;
    public OnGameStartedListener onGameStartedListener;
    private Disk[][] diskArrays;
    private int[] sizes;
    private boolean gameStarted;
    //    private GameActivity mainActivity;
    private Context mContext;
    static int density;

    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public GamePanel(Context context) {
        super(context);
    }

    /**
     * View that contains Tower of Hanoi game.
     *
     * @param context       .
     * @param numberOfDisks the number of disks
     */
    public GamePanel(Context context, int numberOfDisks) {
        super(context);
        setWillNotDraw(false);
        numDisks = numberOfDisks;
        mContext = context;

        density = context.getResources().getDisplayMetrics().densityDpi;

        setBackgroundColor(Color.TRANSPARENT);

        newGame = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        pegA.resetDisks();
        pegB.resetDisks();
        pegC.resetDisks();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int pegHeight = dpToPx(Peg.DISK_HEIGHT_DP) * (numDisks + 1);
        if (newGame) {
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth());
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth());
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth());

            int maxDiskSize;
            int spacing;
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                maxDiskSize = getWidth() / 3 - getWidth() / 30;
                spacing = (maxDiskSize - getHeight() / 20) / (numDisks - 1);
            } else {
                maxDiskSize = getHeight() / 3 - getHeight() / 30;
                spacing = (maxDiskSize - getWidth() / 20) / (numDisks - 1);
            }

            pegA.populateDisks(numDisks, maxDiskSize, spacing);

            newGame = false;
        } else {
            pegA = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 6, pegHeight, getHeight(), getWidth());
            pegB = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), getWidth() / 2, pegHeight, getHeight(), getWidth());
            pegC = new Peg(BitmapFactory.decodeResource(getResources(), R.drawable.peg), 5 * getWidth() / 6, pegHeight, getHeight(), getWidth());

            pegA.setSize(sizes[0]);
            pegB.setSize(sizes[1]);
            pegC.setSize(sizes[2]);
            pegA.setDisks(diskArrays[0]);
            pegB.setDisks(diskArrays[1]);
            pegC.setDisks(diskArrays[2]);
        }

        lastPeg = pegA;
        startPeg = pegA;
        update();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pegA.draw(canvas);
        pegB.draw(canvas);
        pegC.draw(canvas);
    }

    public Peg[] getPegs() {
        return new Peg[]{pegA, pegB, pegC};
    }

    public Peg getPegA() {
        return pegA;
    }

    public Peg getPegB() {
        return pegB;
    }

    public Peg getPegC() {
        return pegC;
    }

    /**
     * Check if the puzzle has been completed.
     */
    private void update() {
        if (pegC.getSize() == numDisks) {
            if (moves == Math.pow(2, numDisks) - 1)
                onGameCompleteListener.onGameComplete(true, moves, numDisks);
            else
                onGameCompleteListener.onGameComplete(false, moves, numDisks);
        }
    }

    static int dpToPx(int dp) {
        return (density / DisplayMetrics.DENSITY_DEFAULT) * dp;
    }

    public void setOnGameCompleteListener(OnGameCompleteListener eventListener) {
        onGameCompleteListener = eventListener;
    }

    public void setOnGameStartedListener(OnGameStartedListener eventListener) {
        onGameStartedListener = eventListener;
    }

    @Override
    public String toString() {
        return "PegA: " + pegA.getSize() + ", PegB: " + pegB.getSize() + ", PegC: " + pegC.getSize();
    }
}

