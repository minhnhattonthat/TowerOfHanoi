package com.nhatton.towerofhanoi;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView playPauseButton;
    private ImageView resetButton;
    private TextView inputNumber;
    private TextView speedText;
    private GamePanel gamePanel;
    private FrameLayout container;

    private SolvingTask task;

    private int numberOfDisks = 3;
    private static final int BASE_SPEED = 1000;
    private int speed = BASE_SPEED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Immersive mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        gamePanel = new GamePanel(this, numberOfDisks);

        container.addView(gamePanel);

        playPauseButton = findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseButton.isActivated()) {
                    pause();
                } else {
                    play();
                }
                playPauseButton.setActivated(!playPauseButton.isActivated());

            }
        });

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task != null) {
                    task.cancel(true);
                    task = null;
                }

                gamePanel = new GamePanel(MainActivity.this, numberOfDisks);
                resetGamePanel();

                playPauseButton.setActivated(false);
            }
        });

        inputNumber = findViewById(R.id.number_of_disk_text);
        inputNumber.setText(String.valueOf(numberOfDisks));

        ImageButton minusButton = findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfDisks--;
                inputNumber.setText(String.valueOf(numberOfDisks));
                resetButton.performClick();
            }
        });

        ImageButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfDisks++;
                inputNumber.setText(String.valueOf(numberOfDisks));
                resetButton.performClick();
            }
        });

        speedText = findViewById(R.id.speed_value);
        speedText.setText("1.0");

        SeekBar speedBar = findViewById(R.id.speed_bar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float floatVal = .5f * i + 0.5f;
                speedText.setText(String.valueOf(floatVal));
                speed = (int) (BASE_SPEED / floatVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (playPauseButton.isActivated()) {
                    playPauseButton.performClick();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void gameComplete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.finish_text);

        TextView textView = new TextView(this);
        textView.setText(R.string.play_again_text);
        builder.setView(textView);

        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetButton.performClick();
                //playPauseButton.performClick();
            }
        });

        builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void play() {
        if (task != null && task.isPaused) {
            task.resume();
        } else {
            task = new SolvingTask();
            task.execute();
        }
    }

    private void pause() {
        if (task != null) {
            task.pause();
        }
    }

    private void resetGamePanel() {
        container.removeAllViews();
        container.addView(gamePanel);
    }

    class SolvingTask extends AsyncTask<Void, Void, Void> {

        private boolean isPaused = false;

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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (gamePanel.getPegC().getSize() == numberOfDisks) {
                gameComplete();
            }
        }

        private void move(int num, Peg source, Peg target, Peg auxiliary) {
            if (num > 0 && !isCancelled()) {
                while (isPaused) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //move n - 1 disks from source to auxiliary, so they are out of the way
                move(num - 1, source, auxiliary, target);

                try {
                    Thread.sleep(speed);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //move the nth disk from source to target
                target.move(source);
                target.drop(source);

                //Display our progress
                publishProgress();
                Log.e("Move: ", gamePanel.toString());

                //move the n - 1 disks that we left on auxiliary onto target
                move(num - 1, auxiliary, target, source);
            }
        }

        void pause() {
            isPaused = true;
        }

        void resume() {
            this.isPaused = false;
        }
    }
}
