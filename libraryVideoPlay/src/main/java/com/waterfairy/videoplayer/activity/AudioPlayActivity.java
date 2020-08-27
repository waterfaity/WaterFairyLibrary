package com.waterfairy.videoplayer.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.waterfairy.videoplayer.R;
import com.waterfairy.videoplayer.listener.OnMediaPlayListener;
import com.waterfairy.videoplayer.widget.AudioPlayerView;

import androidx.appcompat.app.AppCompatActivity;

public class AudioPlayActivity extends AppCompatActivity implements View.OnClickListener, OnMediaPlayListener {
    public static final String EXTRA_PATH = "audio_path";
    public static final String EXTRA_TITLE = "audio_title";
    private AudioPlayerView audioPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_audio_play);
        audioPlayerView = findViewById(R.id.audio_player);
        audioPlayerView.setAutoPlay(true);
        audioPlayerView.setOnPlayListener(this);
        audioPlayerView.setPath(getIntent().getStringExtra(EXTRA_PATH));


        initView();
    }

    private void initView() {
        findViewById(R.id.img_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getIntent().getStringExtra(EXTRA_TITLE));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMediaError(String errMsg) {
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMediaPrepared() {

    }

    @Override
    public void onMediaPlayComplete() {

    }

    @Override
    public void onMediaPause() {

    }

    @Override
    public void onMediaPlay() {

    }

    @Override
    public void onMediaRelease() {

    }

    @Override
    protected void onPause() {
        super.onPause();
//        audioPlayerView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        audioPlayerView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayerView != null) {
            audioPlayerView.release();
        }
    }
}
