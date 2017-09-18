package in.peerreview.fmradioindia.External;

import in.peerreview.fmradioindia.BuildConfig;

/**
 * Created by ddutta on 9/18/2017.
 */
public class AndroidUtils {
    public static boolean isDebug(){
        if (BuildConfig.DEBUG) {
            return true;
        } else{
            return false;
        }
    }
}
