package in.peerreview.fmradioindia.ui.splash;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import java.util.List;

public class FixedPageAdapter extends PagerAdapter {
  public static class Item {
    public int mImageRes;
    public String title;
    public String subtitle;

    public Item(int mImageRes, String title, String subtitle) {
      this.mImageRes = mImageRes;
      this.title = title;
      this.subtitle = subtitle;
    }
  }

  private Context mContext;
  private List<Item> mItemList;

  public FixedPageAdapter(Context context, List<Item> itemList) {
    mContext = context;
    mItemList = itemList;
  }

  public Object instantiateItem(ViewGroup collection, int position) {
    LayoutInflater inflater =
        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.pagelet_ftux1_item, null);
    Item item = mItemList.get(position);

    view.findViewById(R.id.image).setBackgroundResource(item.mImageRes);
    ((TextView) view.findViewById(R.id.title)).setText(item.title);
    ((TextView) view.findViewById(R.id.subtitle)).setText(item.subtitle);
    collection.addView(view, 0);
    return view;
  }

  @Override
  public int getCount() {
    return mItemList.size();
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
