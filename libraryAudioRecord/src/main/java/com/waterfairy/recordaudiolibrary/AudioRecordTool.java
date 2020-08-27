package com.waterfairy.recordaudiolibrary;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/4/3 14:01
 * @info:
 */
public class AudioRecordTool {

    private OnDecibelListener onDecibelListener;
    private OnRecordListener onRecordListener;
    private OnRecordingListener onRecordingListener;


    public AudioRecordTool(String pcmPath) {
        this.pcmPath = pcmPath;
    }

    public boolean isRecording = true;
    //采样率(只能在4000到192000的范围内取值)
    public int sampleRateInHz = 16000;
    //声道(单声道)
    public int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    //音频格式(16位PCM编码)
    public int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //音频来源(录音)
    public int audioSource = MediaRecorder.AudioSource.MIC;

    //传入pcm 地址 后缀  .pcm
    public String pcmPath;

    public boolean convertToWav;

    public AudioRecordTool start() {

        new AsyncTask<Void, Object, IOException>() {

            @Override
            protected IOException doInBackground(Void... voids) {
                try {
                    int minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
                    AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat,
                            minBufferSize);
                    audioRecord.startRecording();
                    if (TextUtils.isEmpty(pcmPath)) throw new IOException("路径为空");
                    File pcmFile = new File(pcmPath);
                    if (!pcmFile.exists()) {
                        File parentFile = pcmFile.getParentFile();
                        if (!parentFile.exists()) {
                            //创建父类路径
                            if (!parentFile.mkdirs())
                                throw new IOException("文件夹创建失败:" + parentFile.getAbsolutePath());
                        }
                        //创建文件
                        if (!pcmFile.createNewFile()) throw new IOException("文件创建失败:" + pcmPath);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(pcmPath);
                    byte[] buffers = new byte[minBufferSize];
                    while (isRecording) {
                        //读取音频流
                        int readLength = audioRecord.read(buffers, 0, buffers.length);
                        if (readLength != -1) {
                            if (onDecibelListener != null) {
                                //计算分贝
                                publishProgress(handleDecibel(buffers, readLength));
                            }
                            if (onRecordingListener != null) {
                                //流 回调
                                publishProgress(Arrays.copyOf(buffers, readLength));
                            }
                            //写入文件
                            fileOutputStream.write(buffers, 0, readLength);
                            fileOutputStream.flush();
                        }
                    }
                    audioRecord.stop();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }

                if (convertToWav) {
                    boolean b = PcmToWav.makePCMFileToWAVFile(pcmPath, pcmPath.substring(0, pcmPath.length() - 3) + "wav", false);
                    if (!b) {
                        return new IOException("格式转化错误");
                    }
                }
                isRecording = false;
                return null;
            }


            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);

                if (values != null && values.length > 0) {
                    if (values[0] instanceof Double) {
                        if (onDecibelListener != null)
                            onDecibelListener.onDecibel((Double) values[0]);
                    } else if (values[0] instanceof byte[]) {
                        if (onRecordingListener != null)
                            onRecordingListener.onRecording((byte[]) values[0]);
                    }
                }
            }

            @Override
            protected void onPostExecute(IOException e) {
                super.onPostExecute(e);
                if (onRecordListener != null) {
                    if (e != null) {
                        onRecordListener.onError(e);
                    } else {
                        onRecordListener.onComplete(convertToWav ? (pcmPath.substring(0, pcmPath.length() - 3) + "wav") : pcmPath);
                    }
                }
            }
        }.execute();


        return this;
    }

    private double handleDecibel(byte[] buffers, int readLength) {
        long total = 0;
        for (int i = 0; i < buffers.length; i += 2) {
            int data = (short) ((buffers[i] & 0xff) | (buffers[i + 1] & 0xff) << 8);
            total += (long) data * data;
        }
        return 10 * Math.log10(total / ((double) readLength / 2));
    }

    public interface OnDecibelListener {
        void onDecibel(double decibel);
    }


    public interface OnRecordingListener {
        void onRecording(byte[] buffers);
    }

    public interface OnRecordListener {
        void onError(IOException e);

        void onComplete(String pcmPath);
    }

    public OnDecibelListener getOnDecibelListener() {
        return onDecibelListener;
    }

    public OnRecordListener getOnRecordListener() {
        return onRecordListener;
    }

    public OnRecordingListener getOnRecordingListener() {
        return onRecordingListener;
    }

    public void setOnRecordingListener(OnRecordingListener onRecordingListener) {
        this.onRecordingListener = onRecordingListener;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    public void setOnDecibelListener(OnDecibelListener onDecibelListener) {
        this.onDecibelListener = onDecibelListener;
    }
}
