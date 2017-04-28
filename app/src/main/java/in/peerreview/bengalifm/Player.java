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
    static final int STATE_NONE = -1;
    static final int STATE_TRYPLAYING = 1;
    static final int STATE_PLAYING = 2;
    static final int STATE_PLAYING_ERROR = 3;
    static final int STATE_PLAYING_SUCCESS = 4;

    static int m_state = STATE_NONE;
    static int getState(){
        return  m_state;
    }
    static{
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static boolean play(Channel ci){
        if(trying == true) return false;
        m_state = STATE_TRYPLAYING;
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
        }
        catch (IllegalArgumentException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IllegalArgumentException");
            callback.onPlayError();
            trying = false;
            m_state = STATE_PLAYING_ERROR;
            return false;
        } catch (SecurityException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("SecurityException");
            callback.onPlayError();
            trying = false;
            m_state = STATE_PLAYING_ERROR;
            return false;
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            m_state = STATE_PLAYING_ERROR;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            m_state = STATE_PLAYING_ERROR;
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
            m_state = STATE_PLAYING_ERROR;
            return false;
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
            msg+=("IOException");
            callback.onPlayError();
            trying = false;
            m_state = STATE_PLAYING_ERROR;
            return false;
        }
        //SendMsg("mPlayer.start");
        mPlayer.start();
        //buttonCha.setText(m_current_channel_name);
        Loading.hide();
        callback.onPlay();
        trying = false;
        m_state = STATE_PLAYING_SUCCESS;
        return true;
    }
    public static boolean pause(){
        if(mPlayer != null){
            mPlayer.stop();
            callback.onPause();
            mPlayer = null;
        }
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
        if(cache == null) return;
        List<Channel> all  = ChannelList.getAllChannelForCategories(cache.getCategory());
        if(all.size() <= 1) return;
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
        if(cache == null) return;
        List<Channel> all  = ChannelList.getAllChannelForCategories(cache.getCategory());
        if(all.size() <= 1) return;
        for(int i =0;i<all.size();i++){
            if(all.get(i).getName().equals(cache.getName())){
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
