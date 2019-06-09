package in.peerreview.fmradioindia.ui.rowlist;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.common.RecyclerTouchListener;
import java.util.List;

public class RowListView extends LinearLayout {
  private RecyclerView mRV;
  private TextView mTitle;
  private @Nullable Callback mCallback;
  private RowListAdapter mRowListAdapter;
  private @Nullable List<Channel> mItemList;

  public interface Callback {
    void onClick(String id);
  }

  public RowListView(Context context) {
    super(context);
    init(null, -1);
  }

  public RowListView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public RowListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  public RowListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs, defStyleAttr);
  }

  private void init(AttributeSet attrs, int defStyle) {
    int headerLayout = -1;
    String headerText = "Header";
    boolean isHorizantal = false;
    TypedArray a =
        getContext()
            .getTheme()
            .obtainStyledAttributes(attrs, R.styleable.MyCustomView, defStyle, 0);

    try {
      headerLayout = a.getResourceId(R.styleable.MyCustomView_headerLayout, -1);
      headerText = a.getString(R.styleable.MyCustomView_headerText);
      isHorizantal = a.getBoolean(R.styleable.MyCustomView_isHorizantal, false);
    } finally {

      a.recycle();
    }
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.view_row_list, this, true);
    mRV = findViewById(R.id.rv);
    if (isHorizantal) {
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
      mRV.setLayoutManager(layoutManager);
    } else {
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
      mRV.setLayoutManager(layoutManager);
    }

    mRV.setItemAnimator(new DefaultItemAnimator());
    mRowListAdapter = new RowListAdapter(getContext(), headerLayout);
    mRV.setAdapter(mRowListAdapter);
    mRV.addOnItemTouchListener(
        new RecyclerTouchListener(
            getContext(),
            mRV,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int position) {
                if (mCallback != null && mItemList != null && position < mItemList.size()) {
                  mCallback.onClick(mItemList.get(position).getId());
                }
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));

    mTitle = findViewById(R.id.row_list_text);
    mTitle.setText(headerText);
  }

  public void setTitle(String title) {
    mTitle.setText(title);
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  public void setData(List<Channel> list) {
    mItemList = list;
    mRowListAdapter.setItem(list);
  }
}
