package in.peerreview.bengalifm;

/**
 * Created by ddutta on 4/25/2017.
 */
public interface ICallback {
    public void beforePlay();
    public void onPlayError();
    public void onPlay();
    public void onPause();
    public void onStop();
}

