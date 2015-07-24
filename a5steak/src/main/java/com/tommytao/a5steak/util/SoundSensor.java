package com.tommytao.a5steak.util;

import android.media.MediaRecorder;

/**
 * Responsible to listen sound
 * <p/>
 * Note: Permission <uses-permission android:name="android.permission.RECORD_AUDIO" /> is required
 * Ref: <a href"http://androidexample.com/Detect_Noise_Or_Blow_Sound_-_Set_Sound_Frequency_Thersold/index.php?view=article_discription&aid=108&aaid=130">here</a>
 */
public class SoundSensor {

    private static SoundSensor instance;

    public static SoundSensor getInstance() {

        if (instance == null)
            instance = new SoundSensor();

        return instance;
    }

    private SoundSensor() {

    }

    // --

    private MediaRecorder mediaRecorder;

    private MediaRecorder getMediaRecorder() {

        return mediaRecorder;
    }

    public boolean isConnected() {
        return mediaRecorder != null;
    }

    public void connect() {

        if (isConnected())
            return;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    public void disconnect() {

        if (!isConnected())
            return;

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

    }

    public double getMagnitude() {
        if (!isConnected())
            return -1;

        return (mediaRecorder.getMaxAmplitude() / 2700.0);
    }


}
