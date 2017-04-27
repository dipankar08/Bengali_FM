package in.peerreview.bengalifm;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;

/**
 * Created by ddutta on 4/23/2017.
 */
public class Player {

    static MediaPlayer mPlayer;
    static Channel cache;//now playing.
    static ICallback callback;
    static boolean trying = false;
    static{
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static boolean play(Channel ci){
        if(trying == true) return false;
        trying = true;
        //restart.
        callback.beforePlay();
        cache = ci;
        //Loading.showPlayProgressDialog();
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
            callback.onPlayError();
            trying = false;
            return false;
        } catch (SecurityException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("SecurityException");
            callback.onPlayError();
            trying = false;
            return false;
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            return false;
        }
        try {
            msg+=("mPlayer.prepare");
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IllegalStateException");
            callback.onPlayError();
            trying = false;
            return false;
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            return false;
        }
        //SendMsg("mPlayer.start");
        mPlayer.start();
        //buttonCha.setText(m_current_channel_name);
        Loading.hide();
        callback.onPlay();
        trying = false;
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

    public static void playNext() {
        List<Channel> all  = ChannelList.getAllChannelForCategories(cache.getCategory());
        for(int i =0;i<all.size();i++){
            if(all.get(i) == cache){
                if(i == all.size()){
                    play(all.get(0));
                } else{
                    play(all.get(i+1));
                }
            }
        }
    }

    public static void playPrevious() {
        List<Channel> all  = ChannelList.getAllChannelForCategories(cache.getCategory());
        for(int i =0;i<all.size();i++){
            if(all.get(i) == cache){
                if(i == 0 ){
                    play(all.get(all.size() -1));
                } else{
                    play(all.get(i-1));
                }
            }
        }
    }
    public static void  setCallback(ICallback cb){
        callback = cb;
    }
    public static Channel nowPlaying(){
        return cache;
    }
}
