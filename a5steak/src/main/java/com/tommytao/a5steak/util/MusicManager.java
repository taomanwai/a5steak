package com.tommytao.a5steak.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;

public class MusicManager extends Foundation {

    private static MusicManager instance;

    public static MusicManager getInstance() {

        if (instance == null)
            instance = new MusicManager();

        return instance;
    }

    private MusicManager() {

    }

    // --

    public static final String BLINK_MP3_LINK = "http://www.xamuel.com/blank-mp3-files/1sec.mp3";

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }


    @Override
    public MediaPlayer getMediaPlayer() {
        return super.getMediaPlayer();
    }

    @Override
    public void playRaw(final int resId, MusicManager.OnPlayListener listener) {

        super.playRaw(resId, listener);

    }

    public void playAssets(final String fileName, MusicManager.OnPlayListener listener) {

        super.playAssets(fileName, listener);

    }

    /**
     * Play sound from url
     * <p/>
     * Note: Play it few seconds before requirement because prepareAsync() (i.e. buffering, etc.) takes time
     *
     * @param link
     */
    public void playLink(final String link, MusicManager.OnPlayListener listener) {

        super.playLink(link, listener);

    }


    /**
     * Note: In MacBook Pro Genymotion simulator, max freq is 14,000
     *
     * @param freqInHz
     * @param durationInMs
     * @return
     */
    public void playSoundAtFreq(final double freqInHz, final double durationInMs, final MusicManager.OnPlayListener listener) {


        new Thread() {

            @Override
            public void run() {
                final int SAMPLE_RATE = 44100;

                double dnumSamples = durationInMs * 1000 * SAMPLE_RATE;
                dnumSamples = Math.ceil(dnumSamples);
                int numSamples = (int) dnumSamples;
                double sample[] = new double[numSamples];
                byte generatedSnd[] = new byte[2 * numSamples];


                for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
                    sample[i] = Math.sin(freqInHz * 2 * Math.PI * i / (SAMPLE_RATE));
                }

                // convert to 16 bit pcm sound array
                // assumes the sample buffer is normalized.
                // convert to 16 bit pcm sound array
                // assumes the sample buffer is normalised.
                int idx = 0;
                int i = 0;

                int ramp = numSamples / 20;                                    // Amplitude ramp as a percent of sample count


                for (i = 0; i < ramp; ++i) {                                     // Ramp amplitude up (to avoid clicks)
                    double dVal = sample[i];
                    // Ramp up to maximum
                    final short val = (short) ((dVal * 32767 * i / ramp));
                    // in 16 bit wav PCM, first byte is the low order byte
                    generatedSnd[idx++] = (byte) (val & 0x00ff);
                    generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
                }


                for (i = i; i < numSamples - ramp; ++i) {                        // Max amplitude for most of the samples
                    double dVal = sample[i];
                    // scale to maximum amplitude
                    final short val = (short) ((dVal * 32767));
                    // in 16 bit wav PCM, first byte is the low order byte
                    generatedSnd[idx++] = (byte) (val & 0x00ff);
                    generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
                }

                for (i = i; i < numSamples; ++i) {                               // Ramp amplitude down
                    double dVal = sample[i];
                    // Ramp down to zero
                    final short val = (short) ((dVal * 32767 * (numSamples - i) / ramp));
                    // in 16 bit wav PCM, first byte is the low order byte
                    generatedSnd[idx++] = (byte) (val & 0x00ff);
                    generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
                }

                boolean succeed = true;
                AudioTrack audioTrack = null;                                   // Get audio track
                try {
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, (int) numSamples * 2,
                            AudioTrack.MODE_STATIC);
                    audioTrack.write(generatedSnd, 0, generatedSnd.length);     // Load the track

                    if (audioTrack.getState()==AudioTrack.STATE_INITIALIZED){
                        // TODO MVP, 100ms is used to make listener.onStart(); running behind listener.onStart();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                listener.onStart();

                            }
                        }, 100);
                        audioTrack.play();                                          // Play the track
                    } else {
                        succeed = false;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    succeed = false;
                }


                if (succeed) {
                    int x = 0;
                    do
                    {                                                     // Montior playback to find when done
                        if (audioTrack != null)
                            x = audioTrack.getPlaybackHeadPosition();
                        else
                            x = numSamples;
                    } while (x < numSamples);

                    if (audioTrack != null)
                        audioTrack.release();           // Track play done. Release track.


                }


                final boolean succeedFinal = succeed;
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        listener.onComplete(succeedFinal);

                    }
                });
            }
        }.start();


    }

}
