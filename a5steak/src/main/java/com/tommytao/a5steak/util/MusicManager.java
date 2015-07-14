package com.tommytao.a5steak.util;

import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
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

	private MediaPlayer mediaPlayer;

	public MediaPlayer getMediaPlayer() {

		if (mediaPlayer == null)
			mediaPlayer = new MediaPlayer();

		return mediaPlayer;
	}


    /**
     *
     * Release media player which is playing
     *
     * Ref: http://stackoverflow.com/questions/7816551/java-lang-illegalstateexception-what-does-it-mean
     *
     *
     * @return TRUE: succeed without exception caught; FALSE: failed with exception caught and printed to logcat
     */
    public boolean releasePlayingMediaPlayer(){

        try {
            if (!getMediaPlayer().isPlaying())
                return true;

            getMediaPlayer().stop();
            getMediaPlayer().release();

            return  true;

        } catch (Exception e){
            e.printStackTrace();
        }

        return false;

    }

	public boolean playAssets(final String fileName) {
		try {

            boolean succeed = releasePlayingMediaPlayer();
            if (!succeed)
                return false;

            mediaPlayer = new MediaPlayer();
			AssetFileDescriptor descriptor = ((ContextWrapper) appContext).getAssets().openFd(fileName);
			getMediaPlayer().reset();
			getMediaPlayer().setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();
			getMediaPlayer().prepare();
			getMediaPlayer().setLooping(false);
			getMediaPlayer().start();

            return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

        return false;
	}

    public boolean playRaw(final int resId) {
        try {

            boolean succeed = releasePlayingMediaPlayer();
            if (!succeed)
                return false;

            mediaPlayer = MediaPlayer.create(appContext, resId);
            getMediaPlayer().start();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
