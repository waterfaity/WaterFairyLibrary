package com.waterfairy.videoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.waterfairy.videoplayer.R;
import com.waterfairy.videoplayer.listener.OnBackClickListener;
import com.waterfairy.videoplayer.listener.OnButtonDismissListener;
import com.waterfairy.videoplayer.listener.OnClickMaxWindowListener;
import com.waterfairy.videoplayer.listener.OnMediaPlayListener;
import com.waterfairy.videoplayer.listener.OnPlayProgressListener;
import com.waterfairy.videoplayer.tool.AudioTool;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 10:38
 * @info:
 */
public class VideoPlayerView extends RelativeLayout implements PlayButtonView.OnPlayClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = "VideoPlayerView";
    private SimpleDateFormat simpleDateFormat;
    //view
    private VideoView videoView;
    private RelativeLayout mRLButton;
    private RelativeLayout mRLBack;
    private TextView mTVTitle;
    private PlayButtonView playButtonView;
    private ImageView mIVMaxWindow;
    private ImageView mIVBack;
    private TextView mTVTime;
    private SeekBar mSeekBar;
    private Toast toast;


    private MediaPlayer mediaPlayer;

    private String title;
    private String path;//
    private String totalTimeStr = "";

    private OnMediaPlayListener onMediaPlayListener;
    private OnClickMaxWindowListener onMaxWindowClickListener;
    private OnPlayProgressListener onPlayProgressListener;
    private OnBackClickListener onBackClickListener;
    private OnButtonDismissListener onButtonDismissListener;

    private final int STATE_INIT = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSING = 2;

    private int videoState;
    private int seekTime;
    private boolean isResumeCanPlay = true;
    private boolean isPrepare;//准备
    private boolean hasFocus;
    private boolean showBack;
    private boolean isPreparing;
    private boolean autoPlay;

    private final int freshDelay = 250;
    private boolean isMaxWindow;
    private boolean jumpMaxActivity;

    public void setJumpMaxActivity(boolean jumpMaxActivity) {
        this.jumpMaxActivity = jumpMaxActivity;
    }

    public VideoPlayerView(Context context) {
        this(context, null);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(LayoutInflater.from(context).inflate(R.layout.layout_video_player_video_play, this, false));
        findView();
        initView();
    }

    public void dismissMaxButton() {
        mIVMaxWindow.setVisibility(GONE);
        LayoutParams layoutParams = (LayoutParams) mTVTime.getLayoutParams();
        layoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
        mTVTime.setPadding(0, 0, (int) (getContext().getResources().getDisplayMetrics().density * 10), 0);
    }

    private void initView() {
        playButtonView.setOnPlayClickListener(this);
        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        videoView.setOnClickListener(this);
        findViewById(R.id.bg_view).setOnClickListener(this);
        mIVMaxWindow.setOnClickListener(this);
        mIVBack.setOnClickListener(this);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
        mRLBack.setVisibility(GONE);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void findView() {
        playButtonView = findViewById(R.id.bt_play);
        videoView = findViewById(R.id.video_view);
        mRLButton = findViewById(R.id.rel_play);
        mIVMaxWindow = findViewById(R.id.img_max_window);
        mTVTime = findViewById(R.id.time);
        mSeekBar = findViewById(R.id.progress_bar);
        mRLBack = findViewById(R.id.rel_back);
        mIVBack = findViewById(R.id.img_back);
        mTVTitle = findViewById(R.id.tv_title);
    }


    public void setPath(String path) {
        this.path = path;
        isPrepare = true;
        initVideo();
    }

    @Override
    public void onPlayClick() {
        if (videoState == STATE_INIT) {
            initVideo();
        } else if (videoState == STATE_PLAYING) {
            pause();
        } else if (videoState == STATE_PAUSING) {
            requestPlay();
        }
    }

    private void initVideo() {
        if (!isPreparing) {
            isPreparing = true;
            if (!TextUtils.isEmpty(path)) {
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
                videoView.setVideoPath(path);
            } else {
                if (onMediaPlayListener != null) onMediaPlayListener.onMediaError("文件不存在");
            }
        }
    }


    private void showToast(String content) {
        if (toast != null) {
            toast.setText(content);
            toast.show();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaPrepared();
        mediaPlayer = mp;
        mediaPlayer.setOnBufferingUpdateListener(this);
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
            return;
        }
        //onResume 处理
        if (isResumeCanPlay) {
            requestPlay();
        }
        isResumeCanPlay = true;
    }

    public void pause() {
        handler.removeMessages(1);
        if (videoState == STATE_PLAYING) {
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaPause();
            videoView.pause();
            videoState = STATE_PAUSING;
            playButtonView.setState(true);
        }
    }

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

    public void play() {
        handler.removeMessages(1);
        if (videoState != STATE_PLAYING) {
            if (onMediaPlayListener != null) onMediaPlayListener.onMediaPlay();

            videoView.start();
            videoState = STATE_PLAYING;
            playButtonView.setState(false);
            handler.sendEmptyMessageDelayed(1, freshDelay);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaPlayComplete();
        playButtonView.setState(true);
        mSeekBar.setProgress(0);
        mTVTime.setText(getTimeStr(0, 0));
        videoState = STATE_PAUSING;
        handler.removeMessages(1);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        handler.removeMessages(1);
        if (onMediaPlayListener != null) onMediaPlayListener.onMediaError("文件不存在");
        videoState = STATE_INIT;
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.video_view || v.getId() == R.id.bg_view) {
            if (mRLButton.getVisibility() == GONE) {
                mRLButton.setVisibility(VISIBLE);
                if (onButtonDismissListener != null) {
                    onButtonDismissListener.onButtonDismiss(VISIBLE);
                }
                if (showBack) {
                    mRLBack.setVisibility(VISIBLE);
                }
            } else {
                mRLBack.setVisibility(GONE);
                mRLButton.setVisibility(GONE);
                if (onButtonDismissListener != null) {
                    onButtonDismissListener.onButtonDismiss(GONE);
                }
            }
        } else if (R.id.img_max_window == v.getId()) {
            if (isMaxWindow) {
                mIVMaxWindow.setImageResource(R.mipmap.icon_window_max);
            } else {
                mIVMaxWindow.setImageResource(R.mipmap.icon_window_min);
                if (jumpMaxActivity) {
                    pause();
                }
            }
            if (onMaxWindowClickListener != null)
                onMaxWindowClickListener.onMaxWindowClick(isMaxWindow = !isMaxWindow);

        } else if (R.id.img_back == v.getId()) {
            handler.removeMessages(1);
            if (onBackClickListener != null) onBackClickListener.onBackClick();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                handler.removeMessages(1);
                if (mediaPlayer != null) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        mSeekBar.setProgress(currentPosition);
                        mTVTime.setText(getTimeStr(currentPosition, duration));
                        if (onPlayProgressListener != null)
                            onPlayProgressListener.onPlayProgress(currentPosition, duration);
                        if (duration - currentPosition > freshDelay)
                            handler.sendEmptyMessageDelayed(1, freshDelay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
     * @param onPlayClickListener
     */
    public void setOnPlayListener(OnMediaPlayListener onPlayClickListener) {
        this.onMediaPlayListener = onPlayClickListener;
    }

    /**
     * 最大化监听
     *
     * @param onMaxWindowClickListener
     */
    public void setOnMaxWindowClickListener(OnClickMaxWindowListener onMaxWindowClickListener) {
        this.onMaxWindowClickListener = onMaxWindowClickListener;
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
                time -= 10;
                if (time < 0) time = 0;
                mediaPlayer.seekTo(time);
                freshTime();
                freshSeekBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
     * 返回按钮
     *
     * @param onBackClickListener
     */
    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        showBack = onBackClickListener != null;
        if (showBack) mRLBack.setVisibility(VISIBLE);
        this.onBackClickListener = onBackClickListener;
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
     * title  和  控制view  隐藏
     */
    public void dismissControlView() {
        mRLBack.setVisibility(GONE);
        mRLButton.setVisibility(GONE);
    }

    /**
     * title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        if (!TextUtils.isEmpty(title)) {
            mTVTitle.setText(title);
        }
    }


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

    public void setOnPlayProgressListener(OnPlayProgressListener onPlayProgressListener) {
        this.onPlayProgressListener = onPlayProgressListener;
    }

    public void setOnButtonDismissListener(OnButtonDismissListener onButtonDismissListener) {
        this.onButtonDismissListener = onButtonDismissListener;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mSeekBar.setSecondaryProgress((int) (mediaPlayer.getDuration() * (percent / 100F)));
    }
}
