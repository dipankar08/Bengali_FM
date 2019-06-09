package in.peerreview.fmradioindia.ui.common;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionView extends FlexboxLayout {
  Map<TextView, String> mMap;
  Map<String, Boolean> mAllState;
  Map<String, Boolean> nNowSelecetd;
  @Nullable private Callback mCallback;

  public Map<String, Boolean> getSelectedList() {
    return mAllState;
  }

  public interface Callback {
    void onOptionChanged(Map<String, Boolean> opt);
  }

  public OptionView(Context context) {
    super(context);
    init();
  }

  public OptionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mMap = new HashMap<>();
    nNowSelecetd = new HashMap<>();
    setFlexWrap(FlexWrap.WRAP);
    setFlexDirection(FlexDirection.ROW);
  }

  public void setList(LinkedHashMap<String, Boolean> map) {
    removeAllViews();
    nNowSelecetd.clear();
    mMap.clear();
    mAllState = map;
    for (Map.Entry<String, Boolean> entry : map.entrySet()) {
      String key = entry.getKey();

      Boolean value = entry.getValue();
      TextView button = new TextView(getContext());
      LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      params.setMargins(10, 10, 10, 10);
      button.setLayoutParams(params);
      int px = (int) Utils.convertDpToPixel(10, getContext());
      button.setPadding(px, px, px, px);
      button.setText(key);
      button.setTextSize(12);
      button.setBackgroundResource(
          value
              ? R.drawable.rouned_switch_button_red_full
              : R.drawable.rouned_switch_button_red_empty);
      button.setTextColor(value?Color.WHITE:Color.BLACK);
      mMap.put(button, key);
      button.setOnClickListener(
          new OnClickListener() {
            @Override
            public void onClick(View view) {
              String now = mMap.get(view);
              if (now != null) {
                // exist
                if (!mAllState.get(now)) {
                  nNowSelecetd.put(now, true);
                  mAllState.put(now, true);
                  view.setBackgroundResource(R.drawable.rouned_switch_button_red_full);
                  ((TextView) view).setTextColor(Color.WHITE);

                } else {
                  nNowSelecetd.put(now, false);
                  mAllState.put(now, false);
                  view.setBackgroundResource(R.drawable.rouned_switch_button_red_empty);
                  ((TextView) view).setTextColor(Color.BLACK);
                }
              }
              if (mCallback != null) {
                mCallback.onOptionChanged(nNowSelecetd);
              }
            }
          });
      addView(button);
    }
    this.requestLayout();
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }
}
