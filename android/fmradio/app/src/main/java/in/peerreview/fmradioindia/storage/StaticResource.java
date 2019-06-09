package in.peerreview.fmradioindia.storage;

import android.content.Context;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.splash.FixedPageAdapter;
import java.util.ArrayList;
import java.util.List;

public class StaticResource {
  public static List<FixedPageAdapter.Item> getSplashData(Context context) {
    List<FixedPageAdapter.Item> res = new ArrayList<>();
    res.add(
        new FixedPageAdapter.Item(
            R.drawable.ic_ftux1,
            context.getResources().getString(R.string.ftux_title1),
            context.getResources().getString(R.string.ftux_subtitle1)));
    res.add(
        new FixedPageAdapter.Item(
            R.drawable.ic_ftux2,
            context.getResources().getString(R.string.ftux_title2),
            context.getResources().getString(R.string.ftux_subtitle2)));
    res.add(
        new FixedPageAdapter.Item(
            R.drawable.ic_ftux3,
            context.getResources().getString(R.string.ftux_title3),
            context.getResources().getString(R.string.ftux_subtitle3)));
    return res;
  }
}
