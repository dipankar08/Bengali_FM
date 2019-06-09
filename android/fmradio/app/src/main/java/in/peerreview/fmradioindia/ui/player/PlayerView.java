package in.peerreview.fmradioindia.ui.player;

import static in.peerreview.fmradioindia.ui.player.PlayerState.VisibilityType.HIDE_ALL;
import static in.peerreview.fmradioindia.ui.player.PlayerState.VisibilityType.NONE;
import static in.peerreview.fmradioindia.ui.player.PlayerState.VisibilityType.SHOW_ALL;
import static in.peerreview.fmradioindia.ui.player.PlayerState.VisibilityType.SHOW_FULL;

import android.animation.Animator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.co.dipankar.quickandorid.utils.DLog;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.applogic.Utils;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;

public class PlayerView extends ConstraintLayout implements BaseView<PlayerState> {

  ViewGroup mMiniView, mRootView;
  TextView mPlayTitle1, mPlayTitle2;
  ImageView mPlayPause1, mPlayPause2, mNext, mPrev, mFev;
  View mBack;
  PlayerPresenter mPresenter;
  ProgressBar mProgressBar;
  TextView mLive;
  ImageView mLogo;
  ImageView mLike, mUnlike;

  public PlayerView(Context context) {
    super(context);
    init();
  }

  public PlayerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.screen_player, this, true);
    mMiniView = findViewById(R.id.mini_view);
    mRootView = findViewById(R.id.player_screen);
    mProgressBar = findViewById(R.id.progress_bar);
    mPlayTitle1 = findViewById(R.id.play_text1);
    mPlayTitle2 = findViewById(R.id.title);
    mPlayPause1 = findViewById(R.id.play_pause1);
    mPlayPause2 = findViewById(R.id.play_pause2);
    mNext = findViewById(R.id.full_next);
    mPrev = findViewById(R.id.full_prev);
    mBack = findViewById(R.id.back);
    mFev = findViewById(R.id.fev);
    mLive = findViewById(R.id.player_live);
    mLogo = findViewById(R.id.logo);
    mLike = findViewById(R.id.like);
    mUnlike = findViewById(R.id.unlike);
    mBack.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            setVisibility(PlayerState.VisibilityType.SHOW_MINI);
          }
        });
    mMiniView.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            setVisibility(PlayerState.VisibilityType.SHOW_FULL);
          }
        });
    mPlayPause1.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPlayPause();
          }
        });
    mPlayPause2.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPlayPause();
          }
        });
    mNext.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickNext();
          }
        });
    mPrev.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onClickPrevious();
          }
        });
    mFev.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.toggleFev();
          }
        });
    mLike.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
            mPresenter.onClickLike();
          }
        });
    mUnlike.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            Toast.makeText(getContext(), "Un-linked", Toast.LENGTH_SHORT).show();
            mPresenter.onClickUnlike();
          }
        });

    mRootView.setOnTouchListener(
        new OnTouchListener() {
          @Override
          public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
              case MotionEvent.ACTION_UP:
                break;
              case MotionEvent.ACTION_DOWN:
                break;
              case MotionEvent.ACTION_MOVE:
                break;
              default:
                break;
            }
            DLog.d("Touch:" + event.getRawX() + event.getRawY());
            mRootView.setY(event.getY());
            return true;
          }
        });
    mPresenter = new PlayerPresenter();
  }

  private void setVisibility(PlayerState.VisibilityType type) {
    switch (type) {
      case HIDE_ALL:
        mRootView.setVisibility(GONE);
        mMiniView.setVisibility(GONE);
        break;
      case SHOW_ALL:
        mRootView.setVisibility(VISIBLE);
        break;
      case SHOW_FULL:
        mRootView
            .animate()
            .translationY(0)
            .alpha(1.0f)
            .setListener(
                new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {
                    mRootView.setVisibility(VISIBLE);
                    mMiniView.setVisibility(GONE);
                    mBack.setVisibility(VISIBLE);
                    mFev.setVisibility(VISIBLE);
                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {}

                  @Override
                  public void onAnimationCancel(Animator animator) {}

                  @Override
                  public void onAnimationRepeat(Animator animator) {}
                });
        break;
      case SHOW_MINI:
        mRootView
            .animate()
            .translationY(mRootView.getHeight() - Utils.convertDpToPixel(50, getContext()))
            .alpha(1.0f)
            .setListener(
                new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {
                    mRootView.setVisibility(VISIBLE);
                    mMiniView.setVisibility(GONE);
                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    mMiniView.setVisibility(VISIBLE);
                    mBack.setVisibility(GONE);
                    mFev.setVisibility(GONE);
                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {}

                  @Override
                  public void onAnimationRepeat(Animator animator) {}
                });
        break;
    }
  }

  @Override
  public void render(PlayerState state) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (state.getChannel() != null) {
                  mPlayTitle1.setText(state.getChannel().getName());
                  mPlayTitle2.setText(state.getChannel().getName());
                  if (state.getChannel().getImg() != null
                      && state.getChannel().getImg().length() != 0) {
                    Glide.with(getContext()).load(state.getChannel().getImg()).into(mLogo);
                  } else {
                    mLogo.setImageResource(R.drawable.ic_music);
                  }
                  if (state.getChannel().isOnline()) {
                    mLive.setText("Online");
                    mLive.setBackgroundResource(R.drawable.rouned_button_red_full);
                  } else {
                    mLive.setText("Offline");
                    mLive.setBackgroundResource(R.drawable.rounded_black_full);
                  }
                }
                if (state.getState() != null) {
                  switch (state.getState()) {
                    case STOP:
                    case ERROR:
                    case PAUSE:
                      mPlayPause1.setImageResource(R.drawable.ic_play_red);
                      mPlayPause2.setImageResource(R.drawable.ic_play_white);
                      break;
                    case PLAYING:
                    case RESUME:
                      mPlayPause1.setImageResource(R.drawable.ic_pause_red);
                      mPlayPause2.setImageResource(R.drawable.ic_pause_white);
                      break;
                  }
                  if (state.getState() == PlayerState.State.TRY_PLAYING) {
                    mProgressBar.setVisibility(VISIBLE);
                  } else {
                    mProgressBar.setVisibility(GONE);
                  }

                  if (state.getState() == PlayerState.State.ERROR) {
                    Toast.makeText(
                            getContext(),
                            "This channel is offline - Please again later",
                            Toast.LENGTH_SHORT)
                        .show();
                  }
                }
                if (state.isFev()) {
                  mFev.setImageResource(R.drawable.ic_heart_white_empty_24);
                } else {
                  mFev.setImageResource(R.drawable.ic_heart_white_full_24);
                }

                if (state.getVisibilityType() != NONE) {
                  if (state.getVisibilityType() == HIDE_ALL) {
                    mRootView.setVisibility(GONE);
                  }
                  if (state.getVisibilityType() == SHOW_FULL) {
                    mRootView.setVisibility(VISIBLE);
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
    super.onDetachedFromWindow();
    mPresenter.detachView();
  }
}
