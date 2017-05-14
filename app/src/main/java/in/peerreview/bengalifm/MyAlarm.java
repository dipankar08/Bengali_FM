package in.peerreview.bengalifm;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.PowerManager;
        import android.os.SystemClock;
        import android.widget.Toast;

        import java.util.Calendar;

public class MyAlarm extends BroadcastReceiver
{
    static MyAlarm sMyAlarm =  new MyAlarm();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("Time is 12 Am");
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        Intent eventService = new Intent(context, BackgroundSoundService.class);
        context.startService(eventService);
    }

    public static void setAlarm(Context context)
    {
        cancelAlarm(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 33);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(MusicAndroidActivity.Get() , BackgroundSoundService.class);
        PendingIntent pendingIntent =PendingIntent.getBroadcast(MusicAndroidActivity.Get().getApplicationContext(), 0, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
    }

    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, MyAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_LONG).show();
    }

}