package in.peerreview.fmradioindia.applogic;

import in.co.dipankar.quickandorid.services.MusicForegroundService;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;

public class MusicService extends MusicForegroundService {
  @Override
  protected int getIcon() {
    return R.drawable.ic_radio;
  }

  @Override
  protected Class getActivityClass() {
    return MainActivity.class;
  }
}
