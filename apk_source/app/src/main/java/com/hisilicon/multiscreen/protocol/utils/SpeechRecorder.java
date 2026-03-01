package com.hisilicon.multiscreen.protocol.utils;

import android.content.Context;
import android.media.AudioRecord;
import android.media.SoundPool;
import com.iflytek.cloud.ErrorCode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class SpeechRecorder {
    private static final String AudioName = "/sdcard/audioData.raw";
    private Context mContext;
    private int mMusic;
    private AudioRecord mRecord;
    private int minBufSize;
    private static int sampleRateInHz = ErrorCode.MSP_ERROR_LMOD_BASE;
    private static int channelConfig = 2;
    private static int audioFormat = 2;
    private ISpeechMsgDealer mISpeechMsgDealer = null;
    FileOutputStream fos = null;
    private LinkedBlockingQueue<byte[]> mAudioDataQueue = null;
    private int audioSource = 1;
    private int mAudioSeq = 0;
    private boolean isRecording = false;
    private Thread recordThread = null;
    private SoundPool mSp = null;
    ExecutorService mSendThreadPool = null;
    private Runnable sendRunnable = new Runnable() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecorder.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                if (SpeechRecorder.this.mAudioDataQueue != null && !SpeechRecorder.this.mAudioDataQueue.isEmpty()) {
                    SpeechRecorder.this.mAudioSeq++;
                    byte[] audioData = (byte[]) SpeechRecorder.this.mAudioDataQueue.take();
                    SpeechRecorder.this.mISpeechMsgDealer.pushAudioData(audioData, SpeechRecorder.this.mAudioSeq);
                }
            } catch (InterruptedException e) {
                LogTool.e(e.getMessage());
            }
        }
    };
    private Runnable recordRunnable = new Runnable() { // from class: com.hisilicon.multiscreen.protocol.utils.SpeechRecorder.2
        @Override // java.lang.Runnable
        public void run() throws IllegalStateException, InterruptedException, IOException {
            LogTool.d("");
            SpeechRecorder.this.openFile();
            SpeechRecorder.this.mISpeechMsgDealer.startSpeaking();
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e1) {
                LogTool.e(e1.getMessage());
            }
            SpeechRecorder.this.mAudioSeq = 0;
            SpeechRecorder.this.mRecord.startRecording();
            while (SpeechRecorder.this.isRecording) {
                byte[] audiodata = new byte[SpeechRecorder.this.minBufSize];
                int readsize = SpeechRecorder.this.mRecord.read(audiodata, 0, SpeechRecorder.this.minBufSize);
                LogTool.i(" record size" + String.valueOf(readsize));
                if (-3 != readsize && -2 != readsize) {
                    SpeechRecorder.this.writeFile(audiodata);
                    try {
                        SpeechRecorder.this.mAudioDataQueue.put(audiodata);
                    } catch (InterruptedException e) {
                        LogTool.e(e.getMessage());
                    }
                    SpeechRecorder.this.mSendThreadPool.execute(SpeechRecorder.this.sendRunnable);
                }
            }
            try {
                SpeechRecorder.this.mSendThreadPool.shutdown();
                SpeechRecorder.this.mSendThreadPool.awaitTermination(1000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {
                LogTool.e(ignored.getMessage());
            }
            SpeechRecorder.this.mISpeechMsgDealer.stopSpeaking();
            SpeechRecorder.this.closeFile();
        }
    };

    public void initRecorder(ISpeechMsgDealer dealer, Context context) {
        this.audioSource = 1;
        sampleRateInHz = ErrorCode.MSP_ERROR_LMOD_BASE;
        channelConfig = 2;
        audioFormat = 2;
        this.minBufSize = 0;
        this.isRecording = false;
        this.minBufSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        this.mISpeechMsgDealer = dealer;
        this.mContext = context;
        initSoundPool();
    }

    public void startRecord() {
        playTouchSound();
        this.mRecord = new AudioRecord(this.audioSource, sampleRateInHz, channelConfig, audioFormat, this.minBufSize);
        LogTool.d("");
        this.mSendThreadPool = Executors.newFixedThreadPool(3);
        this.mAudioDataQueue = new LinkedBlockingQueue<>(20);
        this.isRecording = true;
        this.recordThread = new Thread(this.recordRunnable);
        this.recordThread.start();
    }

    public void stopRecord() throws IllegalStateException, InterruptedException {
        LogTool.d("");
        this.mRecord.stop();
        playTouchSound();
        this.isRecording = false;
        try {
            this.recordThread.join();
        } catch (InterruptedException e) {
            LogTool.e(e.getMessage());
        }
        this.mRecord.release();
        this.mRecord = null;
        this.recordThread = null;
        this.mAudioDataQueue.clear();
        this.mAudioDataQueue = null;
    }

    private void initSoundPool() {
        this.mSp = new SoundPool(10, 1, 5);
        int iResID = this.mContext.getResources().getIdentifier("speech", "raw", this.mContext.getPackageName());
        this.mMusic = this.mSp.load(this.mContext, iResID, 1);
    }

    private void playTouchSound() {
        if (this.mSp != null) {
            this.mSp.play(this.mMusic, 1.0f, 1.0f, 0, 0, 2.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openFile() {
        try {
            File file = new File(AudioName);
            if (file.exists()) {
                file.delete();
            }
            this.fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeFile() throws IOException {
        try {
            this.fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeFile(byte[] audioDate) throws IOException {
        try {
            this.fos.write(audioDate, 0, audioDate.length);
        } catch (Exception e) {
            LogTool.e(e.getMessage());
        }
    }
}
