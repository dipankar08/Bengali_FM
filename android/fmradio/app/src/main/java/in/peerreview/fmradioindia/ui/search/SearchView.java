package in.peerreview.fmradioindia.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import in.peerreview.fmradioindia.ui.rowlist.RowListView;

public class SearchView extends ConstraintLayout implements BaseView<SearchState> {
  ImageView mBack;
  ImageView mClose;
  EditText mSearchEditText;
  RowListView mRecentList;
  RowListView mColListView;
  SearchPresenter mPresenter;
  private Callback mCallback;

  public interface Callback {
    void onClose();

    void onOpen();
  }

  public SearchView(Context context) {
    super(context);
    init();
  }

  public SearchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.activity_search, this, true);
    mBack = findViewById(R.id.back);
    mSearchEditText = findViewById(R.id.input);
    mClose = findViewById(R.id.close);
    mRecentList = findViewById(R.id.recent_list);
    mColListView = findViewById(R.id.search_result);
    mPresenter = new SearchPresenter("SearchPresenter");
    mBack.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mCallback != null) {
              mCallback.onClose();
            }
          }
        });
    mClose.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mSearchEditText.setText("");
            mPresenter.onSearch("");
          }
        });

    mSearchEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mPresenter.onSearch(charSequence.toString());
          }

          @Override
          public void afterTextChanged(Editable editable) {}
        });
    mRecentList.addCallback(
        new RowListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.onClickItem(id);
          }
        });
    mColListView.addCallback(
        new RowListView.Callback() {
          @Override
          public void onClick(String id) {
            mPresenter.onClickItem(id);
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

  @Override
  protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    mPresenter.onVisibilityChanged(visibility);
    if (visibility == VISIBLE) {
      mSearchEditText.requestFocus();
      InputMethodManager keyboard =
          (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      keyboard.showSoftInput(mSearchEditText, 0);
    } else {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }
  }

  @Override
  public void render(SearchState state) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (state.getShouldShowClose() != null) {
                  mClose.setVisibility(state.getShouldShowClose() ? VISIBLE : INVISIBLE);
                }

                if (state.getPreviousSerachList() != null && state.getPreviousSerachList().size() > 0) {
                    mRecentList.setData(state.getPreviousSerachList());
                    mRecentList.setVisibility(VISIBLE);
                  } else {
                    mRecentList.setVisibility(GONE);
                  }

                if (state.getSearchChannel() != null && state.getSearchChannel().size() > 0) {
                    mColListView.setData(state.getSearchChannel());
                    mColListView.setVisibility(VISIBLE);
                  } else {
                    mColListView.setVisibility(GONE);
                  }
              }
            });
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }
}
