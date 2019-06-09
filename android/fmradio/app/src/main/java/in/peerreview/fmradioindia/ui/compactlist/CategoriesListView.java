package in.peerreview.fmradioindia.ui.compactlist;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import in.peerreview.fmradioindia.model.Category;
import java.util.List;
import javax.annotation.Nullable;

public class CategoriesListView extends RecyclerView {
  private RecyclerView mCategoriesRV;
  private CategoriesAdapter mCategoriesAdapter;
  @Nullable private Callback mCallback;

  public interface Callback {
    void onItemClick(String id);
  }

  public CategoriesListView(Context context) {
    super(context);
    init();
  }

  public CategoriesListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CategoriesListView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mCategoriesAdapter =
        new CategoriesAdapter(
            getContext(),
            null,
            new CategoriesAdapter.Callback() {
              @Override
              public void onClickItem(String id) {
                if (mCallback != null) {
                  mCallback.onItemClick(id);
                }
              }
            });
    setLayoutManager(new LinearLayoutManager(getContext()));
    setItemAnimator(new DefaultItemAnimator());
    setAdapter(mCategoriesAdapter);
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  public void setup(List<Category> list) {
    mCategoriesAdapter.setItems(list);
  }
}
