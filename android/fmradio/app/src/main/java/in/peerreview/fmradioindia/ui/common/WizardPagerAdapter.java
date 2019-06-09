package in.peerreview.fmradioindia.ui.common;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class WizardPagerAdapter extends PagerAdapter {

  public Object instantiateItem(ViewGroup collection, int position) {

    int resId = 0;
    switch (position) {
      case 0:
        // resId = R.id.page_one;
        break;
      case 1:
        // resId = R.id.page_two;
        break;
    }
    return collection.findViewById(resId);
  }

  @Override
  public int getCount() {
    return 2;
  }

  @Override
  public boolean isViewFromObject(View arg0, Object arg1) {
    return arg0 == arg1;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    // No super
  }
}
