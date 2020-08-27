package com.waterfairy.media.audio.play;

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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.waterfairy.play.R;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 16:49
 * @info:
 */
public abstract class AbAudioPlayerView extends RelativeLayout implements AudioPlayButtonView.OnPlayClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener, View.OnClickListener {

    private static final String TAG = "audioPlayView";
    private SimpleDateFormat simpleDateFormat;
    //view
    private AbAudioPlayButtonView playButtonView;  //播放按钮
    private TextView mTVTimeCurrent; //时间
    private TextView mTVTimeTotal; //时间
    private ImageView mIVLoading; //加载
    private SeekBar mSeekBar;//进度条

    private MediaPlayer mediaPlayer;//播放器

    private String path;// 路径
    private String totalTimeStr = "";//总时间用于 mTVTime

    private OnPlayClickListener onPlayClickListener;//点击监听
    private OnMediaPlayListener onMediaPlayListener;//播放监听
    private OnPlayProgressListener onPlayProgressListener;//播放进度监听


    private final int STATE_INIT = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSING = 2;
    private int videoState;//播放状态
    private int seekTime;//跳转时间  毫秒


    private boolean isPreparing;//准备中
    private boolean autoPlay;//自动播放默认 false
    private boolean isResumeCanPlay = false;//恢复时是否可以播放 默认 true
    private boolean isPrepare;//已经准备好播放 默认 false
    private boolean hasFocus = false;//音频焦点
    private boolean click;

    public AbAudioPlayerView(Context context) {
        this(context, null);
    }

    public AbAudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(LayoutInflater.from(context).inflate(getResLayout(), this, false));
        findView();
        initView();
    }

    protected abstract int getResLayout();

    private void initView() {
        playButtonView.setOnPlayClickListener(this);
        if (findViewById(R.id.iv_close) != null) {
            findViewById(R.id.iv_close).setOnClickListener(this);
        }
        if (mSeekBar != null) {
            mSeekBar.setMax(100);
            mSeekBar.setProgress(0);
            mSeekBar.setOnSeekBarChangeListener(this);
        }
    }

    private void findView() {
        playButtonView = getPlayButton();
        mTVTimeCurrent = getTvTimeCurrent();
        mTVTimeTotal = getTvTimeTotal();
        mSeekBar = getSeekBar();
        mIVLoading = getIvLoading();
    }

    protected AbAudioPlayButtonView getPlayButton() {
        return null;
    }

    protected ImageView getIvLoading() {
        return null;
    }

    protected SeekBar getSeekBar() {
        return null;
    }

    protected TextView getTvTimeTotal() {
        return null;
    }

    protected TextView getTvTimeCurrent() {
        return null;
    }

    private void startLoading() {
        if (mIVLoading != null) {
            mIVLoading.setVisibility(VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0, 359,
                    Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5F
            );
            rotateAnimation.setDuration(1000);
            rotateAnimation.setRepeatCount(-1);
            mIVLoading.setAnimation(rotateAnimation);
            rotateAnimation.start();
        }
    }

    /**
     * 设置路径 并 初始化
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
        isPrepare = true;
        seekTime = 0;
        //释放
        if (videoState != STATE_INIT)
            release();
        initMediaPlayer();
    }


    /**
     * 设置路径 并 初始化
     *
     * @param path
     */
    public void play(String path) {
        autoPlay = true;
        setPath(path);
    }


    /**
     * 播放/暂停 按钮
     */
    @Override
    public void onPlayClick() {
        if (!click) return;
        if (videoState == STATE_INIT) {
            initMediaPlayer();
        } else if (videoState == STATE_PLAYING) {
            if (onPlayClickListener != null) onPlayClickListener.onPauseClick();
            pause();
        } else if (videoState == STATE_PAUSING) {
            if (onPlayClickListener != null) onPlayClickListener.onPlayClick();
            requestPlay();
        }
    }

    /**
     * 初始化
     */
    private void initMediaPlayer() {
        if (!isPreparing) {
            isPreparing = true;
            if (!TextUtils.isEmpty(path) && (!path.startsWith("http") && new File(path).exists()) || (path.startsWith("http") || (path.startsWith("file")))) {
                mediaPlayer = new MediaPlayer();
                try {
                    startLoading();
                    mediaPlayer.setDataSource(getContext(), Uri.parse(path));
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.setOnCompletionListener(this);
                    mediaPlayer.setOnErrorListener(this);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (onMediaPlayListener != null)
                        onMediaPlayListener.onMediaError("setDataSource 异常");
                }
            } else {
                if (onMediaPlayListener != null) onMediaPlayListener.onMediaError("文件不存在");
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared: ");
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaPrepared();
        if (mIVLoading != null) {
            mIVLoading.clearAnimation();
            mIVLoading.setVisibility(GONE);
        }
        initTimeFormat();

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

    private void initTimeFormat() {
        if (mediaPlayer != null && mediaPlayer.getDuration() != 0) {
            if (mediaPlayer.getDuration() > 60 * 60 * 1000) {
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            } else {
                simpleDateFormat = new SimpleDateFormat("mm:ss");
            }
            totalTimeStr = simpleDateFormat.format(new Date(mediaPlayer.getDuration()));
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
        if (mediaPlayer != null) {
            handler.removeMessages(1);
            mediaPlayer.release();
            videoState = STATE_INIT;
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaRelease();
        }
        mediaPlayer = null;
    }

    public synchronized void requestPlay() {
        if (videoState != STATE_PLAYING && (hasFocus || AudioTool.requestPlay(getContext(), this))) {
            hasFocus = true;
            isResumeCanPlay = false;
            play();
        }
    }

    /**
     * 播放
     */
    private void play() {
        handler.removeMessages(1);
        if (videoState != STATE_PLAYING && mediaPlayer != null) {
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
        if (mTVTimeCurrent != null)
            mTVTimeCurrent.setText(getTimeStr(0));
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
                    if (mTVTimeCurrent != null)
                        mTVTimeCurrent.setText(getTimeStr(mediaPlayer.getCurrentPosition()));
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
     * @return
     */
    private String getTimeStr(int currentTime) {

        if (simpleDateFormat == null) return "00:00";
        return simpleDateFormat.format(new Date(currentTime));
    }

    /**
     * 播放监听
     *
     * @param onMediaPlayListener
     */
    public void setMediaPlayListener(OnMediaPlayListener onMediaPlayListener) {
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
                if (mTVTimeCurrent != null)
                    mTVTimeCurrent.setText(getTimeStr(mediaPlayer.getCurrentPosition()));
                if (mTVTimeTotal != null)
                    mTVTimeTotal.setText(totalTimeStr);
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

    @Override
    public void onClick(View v) {
        if (!click) return;
        if (v.getId() == R.id.iv_close) {
            release();
            if (onPlayClickListener != null) onPlayClickListener.onCloseClick();
        }
    }

    public void setClick(boolean click) {
        this.click = click;
    }

    public boolean getClick() {
        return click;
    }

    public OnPlayClickListener getOnPlayClickListener() {
        return onPlayClickListener;
    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }
}
