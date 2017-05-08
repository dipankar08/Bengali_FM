package in.peerreview.bengalifm;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.PowerManager;
        import android.widget.Toast;

public class MyAlarm extends BroadcastReceiver
{
    static MyAlarm sMyAlarm =  new MyAlarm();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // Put here YOUR code.
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        Player.stop();

        //context.stopService(new Intent(new Intent(MusicAndroidActivity.Get(), BackgroundSoundService.class)));
        wl.release();
        MusicAndroidActivity.Get().finish();
    }

    public void setAlarm(Context context, int min)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MyAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * min, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, MyAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static void setCloseAppAfter(int i) {
        sMyAlarm.setAlarm(MusicAndroidActivity.Get(), 2);
    }

    public static void cancelCloseAppTimer() {
        sMyAlarm.cancelAlarm(MusicAndroidActivity.Get());
    }
}