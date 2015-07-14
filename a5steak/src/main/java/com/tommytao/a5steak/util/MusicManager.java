package com.tommytao.a5steak.util;

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

    public static final String BLINK_MP3_URL = "http://www.xamuel.com/blank-mp3-files/1sec.mp3";


    @Override
    public MediaPlayer getMediaPlayer() {
        return super.getMediaPlayer();
    }

    @Override
    public void playRaw(final int resId) {

        super.playRaw(resId);

    }

    public void playAssets(final String fileName) {

        super.playAssets(fileName);

    }

    /**
     *
     * Play sound from url
     *
     * Note: Play it few seconds before requirement because prepareAsync() (i.e. buffering, etc.) takes time
     *
     * @param url
     */
    public void playUrl(final String url) {

        super.playUrl(url);

    }

}
