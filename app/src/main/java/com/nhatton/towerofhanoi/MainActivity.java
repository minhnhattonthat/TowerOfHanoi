package com.nhatton.towerofhanoi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private GamePanel gamePanel;
    private FrameLayout container;

    private Peg pegA;
    private Peg pegB;
    private Peg pegC;

    private SolvingTask task;

    private int numberOfDisks = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);
        gamePanel = new GamePanel(this, numberOfDisks);

        container.addView(gamePanel);

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pegA = gamePanel.getPegA();
                pegB = gamePanel.getPegB();
                pegC = gamePanel.getPegC();

                task = new SolvingTask();
                task.execute();
            }
        });

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task != null) {
                    task.cancel(true);
                }

                container.removeAllViews();
                gamePanel = new GamePanel(MainActivity.this, numberOfDisks);
                container.addView(gamePanel);

            }
        });

    }

    class SolvingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            move(numberOfDisks, gamePanel.getPegA(), gamePanel.getPegC(), gamePanel.getPegB());
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            gamePanel.invalidate();
            gamePanel.performClick();
        }

        private void move(int num, Peg source, Peg target, Peg auxiliary) {
            if (num > 0 && !isCancelled()) {
                //move n - 1 disks from source to auxiliary, so they are out of the way
                move(num - 1, source, auxiliary, target);

                //move the nth disk from source to target
                target.move(source);
                target.drop(source);

                //Display our progress
                try {
                    Thread.sleep(2000);
                    publishProgress();
                    Log.e("Move: ", gamePanel.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //move the n - 1 disks that we left on auxiliary onto target
                move(num - 1, auxiliary, target, source);
            }
        }
    }
}
