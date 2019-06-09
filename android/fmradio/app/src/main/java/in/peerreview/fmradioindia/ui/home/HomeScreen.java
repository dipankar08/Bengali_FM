package in.peerreview.fmradioindia.ui.home;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.compactlist.CategoriesListView;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import in.peerreview.fmradioindia.ui.rowlist.RowListView;
import javax.annotation.Nullable;

public class HomeScreen extends ConstraintLayout implements BaseView<HomeState> {
  private CategoriesListView mCategoriesList;
  private HomePresenter mPresenter;
  @Nullable private Callback mCallback;
  private TextView mSerachBox;
  private ImageView mSettings;
  private RowListView mSuggested;
  private RowListView mRecent;
  private ImageView mMenu;

  public interface Callback {
    void onSearchClick();

    void onSettingClick();

    void onMenuClick();
  }

  public HomeScreen(Context context) {
    super(context);
    init();
  }

  public HomeScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HomeScreen(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.screen_home, this, true);
    mCategoriesList = findViewById(R.id.categories_list);
    mCategoriesList.setNestedScrollingEnabled(false);
    mSerachBox = findViewById(R.id.search_box);
    mSettings = findViewById(R.id.settings);
    mSuggested = findViewById(R.id.list_suggested);
    mRecent = findViewById(R.id.list_recent);
    mMenu = findViewById(R.id.menu);
    mSuggested.setNestedScrollingEnabled(false);
    mSuggested.addCallback(
        new RowListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.onItemClick(id);
          }
        });
    mCategoriesList.addCallback(
        new CategoriesListView.Callback() {
          @Override
          public void onItemClick(String id) {
            mPresenter.onItemClick(id);
          }
        });
    mSerachBox.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mCallback != null) {
              mCallback.onSearchClick();
            }
          }
        });
    mSettings.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mCallback != null) {
              mCallback.onSettingClick();
            }
          }
        });
    mRecent.addCallback(
        new RowListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.onItemClick(id);
          }
        });
    mMenu.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mCallback != null) {
              mCallback.onMenuClick();
            }
          }
        });
    mPresenter = new HomePresenter();
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  @Override
  public void render(final HomeState state) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (state.getRecentList() != null) {
                  if (state.getRecentList().size() > 0) {
                    mRecent.setData(state.getRecentList());
                    mRecent.setVisibility(VISIBLE);
                  } else {
                    mRecent.setVisibility(GONE);
                  }
                }
                if (state.getSuggestionList() != null) {
                  if (state.getSuggestionList().size() > 0) {
                    mSuggested.setData(state.getSuggestionList());
                    mSuggested.setVisibility(VISIBLE);
                  } else {
                    mSuggested.setVisibility(GONE);
                  }
                }
                if (state.getCategoriesMap() != null) {
                  if (state.getCategoriesMap().size() > 0) {
                    mCategoriesList.setup(state.getCategoriesMap());
                    mCategoriesList.setVisibility(VISIBLE);
                  } else {
                    mCategoriesList.setVisibility(GONE);
                  }
                }
              }
            });
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mPresenter.attachView(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    mPresenter.detachView();
    super.onDetachedFromWindow();
  }
}
