package com.waterfairy.libraryaudiorecorddialog

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.waterfairy.recordaudiolibrary.AudioRecordTool
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020-03-06 14:12
 * @info:
 */
class RecordDialog @JvmOverloads constructor(
    var activity: Activity,
    var canSelectFile: Boolean = false
) : BaseDialog(activity, R.layout.dialog_record_record, R.style.recordDialogSelf) {


    private lateinit var tv_noti: TextView
    private lateinit var tv_record: TextView
    private lateinit var rel_select_audio: RelativeLayout
    private lateinit var iv_record: ImageView
    var clickAutoDismiss = true
    var simpleDateFormat: SimpleDateFormat? = null
    var handler: Handler? = null
    var recordTime = 0
    var MIN_TIME = 1500
    var LIMIT_TIME = 10 * 60 * 1000

    lateinit var recordTool: AudioRecordTool

    var isRecording = false

    var onRecordListener: OnRecordListener? = null

    var loading2Dialog: LoadingDialog? = null

    init {
        findView()
        initView()
        initData()
    }

    private fun findView() {

        tv_noti = findViewById<TextView>(R.id.tv_noti)
        rel_select_audio = findViewById<RelativeLayout>(R.id.rel_select_audio)
        iv_record = findViewById<ImageView>(R.id.iv_record)
        tv_record = findViewById<TextView>(R.id.tv_record)
    }

    fun setNotiText(noti: String) {
        tv_noti.text = noti
        tv_noti.hint = noti
    }

    private fun initData() {
        var cacheFilePath =
            context.externalCacheDir?.absolutePath + "/audio" + "/" + System.currentTimeMillis() + ".pcm"
        recordTool = AudioRecordTool(cacheFilePath)
        recordTool.convertToWav = true
        recordTool.onRecordListener = object : AudioRecordTool.OnRecordListener {
            override fun onComplete(pcmPath: String?) {
                createHandler().removeMessages(0)
                resetView()
                if (clickAutoDismiss) dismiss()
                onRecordListener?.onRecordAudio(this@RecordDialog, pcmPath!!)
            }

            override fun onError(e: IOException?) {
                show("录音失败")
                resetView();
            }
        }
    }

    private fun show(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun resetView() {
        if (canSelectFile) rel_select_audio.visibility = View.VISIBLE
        else rel_select_audio.visibility = View.GONE
        tv_record.text = "点击开始录音"
        tv_noti.text = tv_noti.hint
        isRecording = false
        setCanClickBGDismiss(true)
        setCancelable(true)
        loading2Dialog?.dismiss()
//        iv_record.setImageResource(R.drawable.speech_evaluation_record_decibe4)
        iv_record.setImageResource(R.drawable.record_dialog_bg_record_normal)
    }

    private fun createSimpleDateFormat(): SimpleDateFormat {
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat("mm:ss")
        }
        return simpleDateFormat!!
    }

    private fun showTime() {
        tv_noti.text = createSimpleDateFormat().format(recordTime)
    }

    private fun initView() {
        resetView()
        iv_record.setOnClickListener {
            onRecordClick()
        }
        rel_select_audio.setOnClickListener {
            //选择音频
            if (clickAutoDismiss) dismiss()
            onRecordListener?.onGetRecordFromFileClick(this@RecordDialog)
        }
    }

    private fun onRecordClick() {
        //点击录音
        if (isRecording) {
            stopRecord()
        } else {
            startRecord()
        }
    }

    private fun stopRecord() {
        if (isRecording) {
            if (MIN_TIME > recordTime) {
                show("录音太短了")
            } else {
                showLoading()
                //点击停止录音
                recordTool.isRecording = false
            }
        }
    }

    private fun startRecord() {
        if (!isRecording)

        //检查权限
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //申请权限
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    123
                );
            } else {
                isRecording = true;
                //点击开始录音
                showLoading()
                recordTool.start()

                rel_select_audio.visibility = View.GONE
                tv_record.text = "点击完成录音"
                //开始录音
                loading2Dialog?.dismiss()
                recordTime = 0

                createHandler().removeMessages(0)
                createHandler().sendEmptyMessage(0)


                setCanClickBGDismiss(false)
                setCancelable(false)
                iv_record.setImageResource(R.drawable.record_dialog_bg_record_normal_2)
            }
    }

    private fun showLoading() {
        if (loading2Dialog == null) {
            loading2Dialog = LoadingDialog(activity, "");
            loading2Dialog?.setCanClickBGDismiss(false)
            loading2Dialog?.setCancelable(false)
        }
        loading2Dialog?.show()
    }

    private fun createHandler(): Handler {
        if (handler == null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    showTime()
                    if (recordTime >= LIMIT_TIME) {
                        stopRecord()
                    } else {
                        recordTime += 1000
                        sendEmptyMessageDelayed(0, 1000)
                    }
                }
            }
        }
        return handler!!
    }

    interface OnRecordListener {
        /**
         * 录音获取到音频
         */
        fun onRecordAudio(dialog: RecordDialog, path: String)

        /**
         * 本地选择音频
         */
        fun onGetRecordFromFileClick(dialog: RecordDialog)
    }

}
