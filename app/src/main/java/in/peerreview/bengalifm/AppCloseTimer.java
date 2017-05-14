package in.peerreview.bengalifm;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

/**
 * Created by ddutta on 5/8/2017.
 */
public class AppCloseTimer {
    private static CountDownTimer s_CountDownTimer = null;
    public static void start(int min){
        if(s_CountDownTimer != null){
            Toast.makeText(MusicAndroidActivity.Get(), "Timer already started, cancel..", Toast.LENGTH_LONG).show();
            cancel();
        }
        s_CountDownTimer = new CountDownTimer(1000*60*min, 1000) {
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                MusicAndroidActivity.m_timerText.setText("The app will be closed in " +sec/60+":"+sec%60 +" min.");
            }
            public void onFinish() {
                MusicAndroidActivity.m_timerText.setVisibility(View.GONE);
                Intent intent = new Intent(MusicAndroidActivity.Get(), BackgroundSoundService.class);
                MusicAndroidActivity.Get().stopService(intent);
                MusicAndroidActivity.Get().finish();
                s_CountDownTimer = null;
            }
        }.start();
        MusicAndroidActivity.m_timerText.setVisibility(View.VISIBLE);
        Toast.makeText(MusicAndroidActivity.Get(), " App Close timer Started!", Toast.LENGTH_LONG).show();
    }
    public static void cancel() {
        s_CountDownTimer.cancel();
        Toast.makeText(MusicAndroidActivity.Get(), " App Close timer canceled!", Toast.LENGTH_LONG).show();
        s_CountDownTimer=  null;
        MusicAndroidActivity.m_timerText.setVisibility(View.GONE);
    }

}
