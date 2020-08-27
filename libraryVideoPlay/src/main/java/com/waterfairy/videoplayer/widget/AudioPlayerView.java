package com.waterfairy.videoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.waterfairy.videoplayer.R;
import com.waterfairy.videoplayer.listener.OnMediaPlayListener;
import com.waterfairy.videoplayer.listener.OnPlayProgressListener;
import com.waterfairy.videoplayer.tool.AudioTool;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 16:49
 * @info:
 */
public class AudioPlayerView extends RelativeLayout implements PlayButtonView.OnPlayClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "audioPlayView";
    private SimpleDateFormat simpleDateFormat;
    //view
    private PlayButtonView playButtonView;  //播放按钮
    private TextView mTVTime; //时间
    private SeekBar mSeekBar;//进度条

    private MediaPlayer mediaPlayer;//播放器

    private String path;// 路径
    private String totalTimeStr = "";//总时间用于 mTVTime

    private OnMediaPlayListener onMediaPlayListener;//播放监听
    private OnPlayProgressListener onPlayProgressListener;//播放进度监听

    private final int STATE_INIT = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSING = 2;
    private int videoState;//播放状态
    private int seekTime;//跳转时间  毫秒


    private boolean isPreparing;//准备中
    private boolean autoPlay;//自动播放默认 false
    private boolean isResumeCanPlay = true;//恢复时是否可以播放 默认 true
    private boolean isPrepare;//已经准备好播放 默认 false
    private boolean hasFocus = false;//音频焦点

    public AudioPlayerView(Context context) {
        this(context, null);
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(LayoutInflater.from(context).inflate(R.layout.layout_video_player_audio_play, this, false));
        findView();
        initView();
    }

    private void initView() {
        playButtonView.setOnPlayClickListener(this);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void findView() {
        playButtonView = findViewById(R.id.bt_play);
        mTVTime = findViewById(R.id.time);
        mSeekBar = findViewById(R.id.progress_bar);

    }

    /**
     * 设置路径 并 初始化
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
        isPrepare = true;
        initMediaPlayer();
    }

    /**
     * 播放/暂停 按钮
     */
    @Override
    public void onPlayClick() {
        if (videoState == STATE_INIT) {
            initMediaPlayer();
        } else if (videoState == STATE_PLAYING) {
            pause();
        } else if (videoState == STATE_PAUSING) {
            requestPlay();
        }
    }

    /**
     * 初始化
     */
    private void initMediaPlayer() {
        if (!isPreparing) {
            isPreparing = true;
            if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                if (mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(getContext(), Uri.fromFile(new File(path)));
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
            } else {
                if (onMediaPlayListener != null) onMediaPlayListener.onMediaError("文件不存在");
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared: ");
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaPrepared();
        isPreparing = false;
        videoState = STATE_PAUSING;

        if (seekTime != 0) {
            seek(seekTime);
            seekTime = 0;
        } else {
            freshTime();
            freshSeekBar();
        }

        //第一次初始化
        if (isPrepare) {
            isPrepare = false;
            if (autoPlay) {
                requestPlay();
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        handler.removeMessages(1);
        if (videoState == STATE_PLAYING) {
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaPause();
            mediaPlayer.pause();
            videoState = STATE_PAUSING;
            playButtonView.setState(true);
        }
    }

    /**
     * 释放
     */
    public void release() {
        handler.removeMessages(1);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaRelease();
        }
        videoState = STATE_INIT;
    }

    public synchronized void requestPlay() {
        if (videoState != STATE_PLAYING && (hasFocus || AudioTool.requestPlay(getContext(), this))) {
            hasFocus = true;
            play();
        }
    }

    /**
     * 播放
     */
    private void play() {
        handler.removeMessages(1);
        if (videoState != STATE_PLAYING) {
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaPlay();
            mediaPlayer.start();
            videoState = STATE_PLAYING;
            playButtonView.setState(false);
            handler.sendEmptyMessageDelayed(1, 1000);
        }

    }

    /**
     * 播放完成
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.removeMessages(1);
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaPlayComplete();
        playButtonView.setState(true);
        mSeekBar.setProgress(0);
        mTVTime.setText(getTimeStr(0, 0));
        videoState = STATE_PAUSING;
    }

    /**
     * 播放错误
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        handler.removeMessages(1);
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaError(what + " - " + extra);
        videoState = STATE_INIT;
        return false;
    }

    /**
     * 延时获取进度 1s刷新
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mediaPlayer != null) {
                    mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    mTVTime.setText(getTimeStr(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
                    if (onPlayProgressListener != null)
                        onPlayProgressListener.onPlayProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };

    /**
     * 时间转换
     *
     * @param currentPosition
     * @param totalTime
     * @return
     */
    private String getTimeStr(int currentPosition, int totalTime) {
        if (simpleDateFormat == null) {
            if (totalTime > 60 * 60 * 1000) {
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            } else {
                simpleDateFormat = new SimpleDateFormat("mm:ss");
            }
            totalTimeStr = simpleDateFormat.format(new Date(totalTime));
        }
        return simpleDateFormat.format(new Date(currentPosition)) + "/" + totalTimeStr;
    }

    /**
     * 播放监听
     *
     * @param onMediaPlayListener
     */
    public void setOnPlayListener(OnMediaPlayListener onMediaPlayListener) {
        this.onMediaPlayListener = onMediaPlayListener;
    }

    /**
     * 进度监听
     *
     * @param onPlayProgressListener
     */
    public void setOnPlayProgressListener(OnPlayProgressListener onPlayProgressListener) {
        this.onPlayProgressListener = onPlayProgressListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * seekBar¬跳到指定位置
     *
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (videoState != STATE_INIT)
            seek(seekBar.getProgress());
        else mSeekBar.setProgress(0);
    }

    /**
     * 跳到指定位置
     *
     * @param time
     */
    private void seek(int time) {
        if (mediaPlayer != null) {
            try {
                time -= 1000;
                if (time < 0) time = 0;
                mediaPlayer.seekTo(time);
                freshTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新seekBar
     */
    private void freshSeekBar() {
        if (mSeekBar != null) {
            try {
                mSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                mSeekBar.setMax(mediaPlayer.getDuration());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新时间显示
     */
    private void freshTime() {
        if (mediaPlayer != null) {
            try {
                mTVTime.setText(getTimeStr(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化播放开始时间
     *
     * @param seekTime
     */
    public void initSeekTime(int seekTime) {
        if (seekTime < 0) seekTime = 0;
        this.seekTime = seekTime;
    }

    /**
     * 自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    /**
     * 恢复播放 按需调用
     */
    public void onResume() {
        if (isPreparing) return;
        if (isResumeCanPlay) {
            requestPlay();
        }
        isResumeCanPlay = true;
    }

    /**
     * 暂停播放  按需调用
     */
    public void onPause() {
        if (mediaPlayer != null) {
            seekTime = mediaPlayer.getCurrentPosition();
        }
        isResumeCanPlay = videoState == STATE_PLAYING;
        pause();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            hasFocus = false;
            pause();
        }
    }
}
