package com.waterfairy.libraryexoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.appcompat.app.AppCompatActivity;

public class ExoVideoPlayerActivity extends AppCompatActivity {


    private static final String TAG = "exoPlayer";
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private RelativeLayout relTitle;
    private ImageView imgBack;
    private TextView tvTitle;

    public final static String VIDEO_PATH = "video_path";
    public final static String VIDEO_TITLE = "video_title";
    private String videoPath;
    private String videoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_video_player);
        getExtra();
        findView();
        initView();
        initData();
    }

    private void getExtra() {
        videoPath = getIntent().getStringExtra(VIDEO_PATH);
        videoTitle = getIntent().getStringExtra(VIDEO_TITLE);
    }

    private void initData() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),
                new DefaultLoadControl()
        );
        playerView.setPlayer(player);

        //
        String userAgent = Util.getUserAgent(this, "my-app");

        MediaSource mediaSource;
        if (videoPath.startsWith("http")) {
            mediaSource =
                    new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                            .createMediaSource(Uri.parse(videoPath));
        } else {
            mediaSource =
                    new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this,
                            userAgent, new DefaultBandwidthMeter()))
                            .createMediaSource(Uri.parse(videoPath));
        }
        player.setPlayWhenReady(true);
        player.prepare(mediaSource);
    }

    private void initView() {
        if (!TextUtils.isEmpty(videoTitle)) {
            tvTitle.setText(videoTitle);
        }
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                relTitle.setVisibility(visibility);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
    }

    private void onBackClick() {
        player.stop();
        player.release();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void findView() {
        playerView = findViewById(R.id.exo_play_view);
        relTitle = findViewById(R.id.rel_back);
        tvTitle = findViewById(R.id.tv_title);
        imgBack = findViewById(R.id.img_back);
    }
}