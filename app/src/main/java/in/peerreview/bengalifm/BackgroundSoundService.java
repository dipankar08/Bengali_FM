package in.peerreview.bengalifm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by ddutta on 4/24/2017.
 */
public class BackgroundSoundService extends Service {
    private static final String TAG = null;
    LocalBroadcastManager broadcaster = null;

    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_RESUME = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TO DO
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }
    @Override
    public void onDestroy() {
        Log.d("Dipankar","BackgroundSoundService stoped");
        Player.stop();
    }
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
        super.onCreate();
        Player.setCallback(new ICallback(){
                                        @Override
                                        public void onPlay() {
                                            Log.e( "MediaPlayerService", "onPlay");
                                            buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
                                            Loading.hide();
                                        }
                                        @Override
                                        public void onPause() {
                                            Log.e( "MediaPlayerService", "onPause");
                                            buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_RESUME));
                                            Loading.hide();
                                        }
                                        @Override
                                        public void onStop() {
                                            Log.e( "MediaPlayerService", "onStop");
                                            //Stop media player here
                                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManager.cancel( 1 );
                                            //Intent intent = new Intent( MusicAndroidActivity.Get().getApplicationContext(), BackgroundSoundService.this );
                                           // stopService( intent );
                                            Loading.hide();
                                        }
                                    }
        );
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //sendResult("PLAY_START");
        Log.d("Dipankar","BackgroundSoundService stared");
        handleIntent(intent);
        return START_STICKY;
    }
    @Override
    public void onLowMemory() {

    }
    // Own methodfs and helpers

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    //Notifuicatison...
    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();
        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            String name = intent.getStringExtra("Name");
            Player.play(ChannelList.getChannelDetails(name));
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            Player.pause();
        } else if( action.equalsIgnoreCase( ACTION_FAST_FORWARD ) ) {
            Player.fastForward();
        } else if( action.equalsIgnoreCase( ACTION_RESUME ) ) {
            Player.resume();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
           // Player.skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            //Player.skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            Player.stop();
        }
    }
    @SuppressLint("NewApi")
    private Notification.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), BackgroundSoundService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    @SuppressLint("NewApi")
    private void buildNotification(Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), BackgroundSoundService.class );
        //intent.setAction( ACTION_STOP );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(in.peerreview.bengalifm.R.drawable.ic_launcher)
                .setContentTitle(Player.nowPlaying().getName())
                .setContentText( Player.nowPlaying().getCategory())
                .setDeleteIntent( pendingIntent )
                .setStyle(style);

        builder.addAction( generateAction( android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS ) );
        //builder.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ) );
        builder.addAction( action );
        builder.addAction( generateAction( android.R.drawable.ic_media_pause,"Stop", ACTION_STOP ) );
        builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", ACTION_NEXT ) );
        style.setShowActionsInCompactView(0,1,2,3,4);

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( 1, builder.build() );
    }




}