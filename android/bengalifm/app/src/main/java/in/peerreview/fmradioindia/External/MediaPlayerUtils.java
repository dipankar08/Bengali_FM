package in.peerreview.fmradioindia.External;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ddutta on 9/1/2017.
 */
public class MediaPlayerUtils {
    private static final String TAG = "MediaPlayerUtils";
    private static Activity mContext;
    private static MediaPlayer mPlayer;
    private static boolean s_playing = false;

    public static void setup(Activity cx) {
        mContext = cx;
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static boolean isPlaying() {
        return s_playing;
    }

    public interface IPlayerCallback {
        void tryPlaying();

        void success(String msg);

        void error(String msg, Exception e);

        void complete(String msg);
    }

    /***********************************************************************************************
     * Please Write your logic Here.
     **********************************************************************************************/
    public static void play(String url, final IPlayerCallback callback) {
        if (url == null) {
            callback.error("Invalid URL passed", null);
            return;
        }
        callback.tryPlaying();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
        }
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
        } catch (final Exception e) {
            callback.error("Not able to play music", e);
            stop();
            e.printStackTrace();
        } finally {

        }
        //if we ave an excpetion at this points let's return.
        if (mPlayer == null) return;

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
                callback.success("Music is playing successfully..");
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                callback.complete("Music complted");
            }
        });
        mPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, final int extra) {
                        callback.error("MediaPlayer error happened", null);
                        return true;
                    }
                }
        );
        s_playing = true;
    }

    public static void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer = null;
        }
        s_playing = false;
    }

    /***********************************************************************************************
     * Please write a sample text to confirm this module is working fine.
     **********************************************************************************************/
    public static void test() {
        //write test
        Log.d(TAG, "Staring Test....");
        Log.d(TAG, "Ending Test....");
    }
}