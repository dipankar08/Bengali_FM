package com.prgguru.example;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by ddutta on 4/23/2017.
 */
public class Player {

    static MediaPlayer mPlayer;
    static Channel cache;
    static ICallback callback;
    static{
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static boolean play(Channel ci){
        //restart.
        cache = ci;
        Loading.showPlayProgressDialog();
        stop();
        String msg="";
        String url = ci.getUrl();
        msg+=("buttonPlay.setOnClickListener");
        if(mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            msg+=("mPlayer.setDataSource");
            mPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IllegalArgumentException");
            return false;
        } catch (SecurityException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("SecurityException");
            return false;
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            msg+=("IOException");
            return false;
        }
        try {
            msg+=("mPlayer.prepare");
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IllegalStateException");
            return false;
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            return false;
        }
        //SendMsg("mPlayer.start");
        mPlayer.start();
        //buttonCha.setText(m_current_channel_name);
        Loading.hide();
        callback.onPlay();
        return true;
    }
    public static boolean pause(){
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer = null;
        }
        callback.onPause();
        return true;
    }
    public static boolean resume(){
        play(cache);
        callback.onPlay();
        return true;
    }

    public static boolean stop(){
        // TODO Auto-generated method stub
        if(mPlayer!=null && mPlayer.isPlaying()){
            //SendMsg("mPlayer.stop");
            mPlayer.stop();
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            callback.onStop();
            return true;
        }
        return false;
    }

    public static void Destroy() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        callback.onStop();
    }

    public static void fastForward() {
    }
    public static void  setCallback(ICallback cb){
        callback = cb;
    }
    public static Channel nowPlaying(){
        return cache;
    }
}
