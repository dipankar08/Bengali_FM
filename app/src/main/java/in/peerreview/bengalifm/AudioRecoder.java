package in.peerreview.bengalifm;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;

/**
 * Created by ddutta on 5/3/2017.
 */
interface IAudioRecoderCallback {
    public void onStarted();
    public void onStoped();
}

public class AudioRecoder {

   public static void register(IAudioRecoderCallback callback){
        s_callback = callback;
   }
    public static void start(){
        if(s_isStarted) return;
        //cheks
        if(!hasMicrophone()){
            MiniUI.alert("You dont have microphone!");
            return;
        }
        String audioFilePath = getPath();
        if(audioFilePath == null){
            MiniUI.alert("You must give the external storage permission.!");
            return;
        }
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mediaRecorder.start();
        s_isStarted = true;
        s_callback.onStarted();
    }
    public static void stop(){
        if(!s_isStarted) return;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            s_isStarted = false;
        s_callback.onStoped();
    }

    public static boolean isStarted() {
        return s_isStarted;
    }
    //privates
    private static boolean hasMicrophone() {
        PackageManager pmanager = MusicAndroidActivity.Get().getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }
    private static String getPath(){
        try {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/FM_Recorded.3gp";
        }
        catch (Exception e){
            return null;
        }
    }
    private static IAudioRecoderCallback s_callback;
    private static boolean s_isStarted = false;
    private static MediaRecorder mediaRecorder = null;
}
