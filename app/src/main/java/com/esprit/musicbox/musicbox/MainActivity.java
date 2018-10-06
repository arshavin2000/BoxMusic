package com.esprit.musicbox.musicbox;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private MediaPlayer mediaPlayer;
    private Button mNext , mPrevious, mPlay ;
    private TextView mDuration , mReverseDuration ;
    private SeekBar mSeekBar ;
    private ImageView mImage ;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mNext = findViewById(R.id.next);
        mPrevious = findViewById(R.id.previous);
        mPlay = findViewById(R.id.play);
        mDuration =findViewById(R.id.duration);
        mReverseDuration = findViewById(R.id.reverseDuration);
        mSeekBar = findViewById(R.id.seekBar);
        mImage =findViewById(R.id.image);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.booba);
        mSeekBar.setMax(mediaPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(b)
                {
                    mediaPlayer.seekTo(i);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.FRANCE);
                int duration = mediaPlayer.getDuration();
                int currentPos = mediaPlayer.getCurrentPosition();
                mDuration.setText(dateFormat.format(new Date(currentPos)));
                mReverseDuration.setText(dateFormat.format(new Date(duration-currentPos)));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.next:
                nextMusic();
                break;
            case R.id.previous:
                previousMusic();
                break;
            case R.id.play:
                if(mediaPlayer.isPlaying())
                {
                    pauseMusic();
                }
                else
                {
                    playMusic();
                }
                break;
        }


    }


    public void updateUIThread()
    {
        thread = new Thread()
        {
            @Override
            public void run() {

                while(mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    try {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newDuration = mediaPlayer.getDuration();
                                int newCurrentDuration  = mediaPlayer.getCurrentPosition();
                                mSeekBar.setMax(newDuration);
                                mSeekBar.setProgress(newCurrentDuration);
                                mDuration.setText(String.valueOf(new SimpleDateFormat("mm:ss",Locale.FRANCE).format(new Date(mediaPlayer.getCurrentPosition()))));
                                mReverseDuration.setText(String.valueOf(new SimpleDateFormat("mm:ss",Locale.FRANCE).format(new Date(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                super.run();
            }
        };
        thread.start();
    }


    public void pauseMusic()
    {
        if(mediaPlayer != null)
        {
           mediaPlayer.pause();
           mPlay.setBackgroundResource(R.mipmap.ic_pause);
        }
    }

    public void playMusic()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.start();
            updateUIThread();
            mPlay.setBackgroundResource(R.mipmap.ic_play);
        }

    }

    public void nextMusic()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(mediaPlayer.getDuration()-1000);
        }

    }

    public void previousMusic()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(0);
        }

    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread =null;
        super.onDestroy();
    }
}
