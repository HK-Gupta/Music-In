package com.example.musemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class SongPlayer extends AppCompatActivity {

    Button btn_play_pause, btn_next, btn_prev, btn_fast_forward, btn_fast_rewind;
    TextView txt_player_song_name, txt_seekBar_start, txt_seekBar_end;
    SeekBar seekBar;
    BarVisualizer visual_player;
    ImageView img_player_song_pic;
    String songName;
    Thread updateSeekBar;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#333949"));
        actionBar.setBackgroundDrawable(colorDrawable);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar));

        btn_play_pause = findViewById(R.id.btn_play_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_prev = findViewById(R.id.btn_prev);
        btn_fast_forward = findViewById(R.id.btn_fast_forward);
        btn_fast_rewind = findViewById(R.id.btn_fast_rewind);
        txt_player_song_name = findViewById(R.id.txt_player_song_name);
        txt_seekBar_end = findViewById(R.id.txt_seekBar_end);
        txt_seekBar_start = findViewById(R.id.txt_seekBar_start);
        img_player_song_pic = findViewById(R.id.img_player_song_pic);
        seekBar = findViewById(R.id.seekBar);
        visual_player = findViewById(R.id.visual_player);

        if(mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songName");
        position = bundle.getInt("position", 0);
        txt_player_song_name.setSelected(true);
        startSong(position);

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);

                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.test2), PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String finalTime = findTime(mediaPlayer.getDuration());
        txt_seekBar_end.setText(finalTime);
        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = findTime(mediaPlayer.getCurrentPosition());
                txt_seekBar_start.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_play_pause.setBackgroundResource(R.drawable.ic_play);
                } else {
                    mediaPlayer.start();
                    btn_play_pause.setBackgroundResource(R.drawable.ic_pause);

                    //Provide the animation.
                    TranslateAnimation animation = new TranslateAnimation(-25, 25, -25, 25);
                    animation.setInterpolator(new AccelerateInterpolator());
                    animation.setDuration(800);
                    animation.setFillEnabled(true);
                    animation.setFillAfter(true);
                    animation.setRepeatMode(Animation.REVERSE);
                    animation.setRepeatCount(1);
                    img_player_song_pic.startAnimation(animation);


                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btn_next.performClick();
            }
        });
        startVisualiser();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                startSong(position);
                startAnimation(img_player_song_pic, 360f);
                startVisualiser();
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)%mySongs.size());
                startSong(position);
                startAnimation(img_player_song_pic, -360f);
                startVisualiser();
            }
        });

        btn_fast_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10*1000);
                }
            }
        });
        btn_fast_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10*1000);
                }
            }
        });
    }

    private void startVisualiser() {
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if(audioSessionId != -1) {
            visual_player.setAudioSessionId(audioSessionId);
        }
    }

    private void startSong(int position) {
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txt_player_song_name.setText(songName);

        setAlbumArtwork(uri);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
    }

    private void setAlbumArtwork(Uri audioFileUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(audioFileUri.getPath());

        byte[] artwork = retriever.getEmbeddedPicture();

        if (artwork != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            img_player_song_pic.setImageBitmap(bitmap);
        } else {
            // If no album artwork is available, set a default image
            img_player_song_pic.setImageResource(R.drawable.music_default);
        }
    }

    public void startAnimation(View view, Float degree) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0.f, degree);
        objectAnimator.setDuration(800);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }

    public String findTime(int duration) {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time + min + ":";
        if(sec < 10) {
            time += "0";
        }
        time += sec;
        return time;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(visual_player != null) {
            visual_player.release();
        }
        super.onDestroy();
    }

}