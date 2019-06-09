package in.peerreview.fmradioindia.ui.splash;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.storage.StaticResource;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import javax.annotation.Nullable;

public class SplashScreen extends ConstraintLayout implements BaseView<SplashState> {
  private ViewPager mViewPager;
  private int index = 0;
  private Button mButton;
  private VideoView mVideoView;
  private TextView mVersion;
  private ViewGroup mFTUX, mBoot;
  private TabLayout mTabLayout;
  private SplashPresenter mSplashPresenter;
  private FixedPageAdapter mFixedPageAdapter;
  @Nullable private Callback mCallback;

  public interface Callback {
    void onLoadSuccess();
  }

  public SplashScreen(Context context) {
    super(context);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SplashScreen(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  protected void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.activity_splash, this, true);
    mFTUX = findViewById(R.id.ftux);
    mBoot = findViewById(R.id.boot);
    mViewPager = findViewById(R.id.pager);
    mVideoView = findViewById(R.id.videoView);
    mButton = findViewById(R.id.next);
    mTabLayout = findViewById(R.id.tabDots);
    mFixedPageAdapter =
        new FixedPageAdapter(getContext(), StaticResource.getSplashData(getContext()));

    // define viewpager
    mViewPager.setAdapter(mFixedPageAdapter);
    mTabLayout.setupWithViewPager(mViewPager, true);
    mViewPager.setOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          @Override
          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          @Override
          public void onPageSelected(int position) {
            index = position;
            if (position < StaticResource.getSplashData(getContext()).size() - 1) {
              mButton.setBackgroundResource(R.drawable.rounded_white_empty);
              mButton.setTextColor(Color.WHITE);
              mButton.setText("Next");
            } else {
              mButton.setBackgroundResource(R.drawable.rounded_white_full);
              mButton.setTextColor(Color.BLACK);
              mButton.setText("Getting Stated");
            }
          }

          @Override
          public void onPageScrollStateChanged(int state) {}
        });
    // setup Button.
    mButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            if (index == 3) {
              mSplashPresenter.onCompelteFTUX();
            } else {
              mViewPager.setCurrentItem(++index);
            }
          }
        });
    // Setup Video View
    Uri uri =
        Uri.parse(
            "android.resource://" + getContext().getPackageName() + "/" + R.raw.backgroud_preview);
    mVideoView.setVideoURI(uri);
    mVideoView.start();
    mVideoView.setOnPreparedListener(
        new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mp) {
            mp.setLooping(true);
          }
        });
    // Setup presenter.
    mSplashPresenter = new SplashPresenter("SplashPresenter");
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mSplashPresenter.attachView(this);
    mVideoView.start();
  }

  @Override
  protected void onDetachedFromWindow() {
    mSplashPresenter.detachView();
    super.onDetachedFromWindow();
  }

  public void addCallback(Callback callback) {
    mCallback = callback;
  }

  @Override
  public void render(final SplashState splashState) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                switch (splashState.getType()) {
                  case Boot:
                    mBoot.setVisibility(VISIBLE);
                    mFTUX.setVisibility(GONE);
                    break;
                  case Ftux:
                    mBoot.setVisibility(GONE);
                    mFTUX.setVisibility(VISIBLE);
                    break;
                  case Done:
                    if (mCallback != null) {
                      mCallback.onLoadSuccess();
                    }
                }

                if (splashState.getError() != null) {
                  AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                  alertDialog.setTitle("Not able to Load Channel");
                  alertDialog.setMessage("Please make sure you have internet connection");
                  alertDialog.setButton(
                      AlertDialog.BUTTON_NEUTRAL,
                      "Quit",
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                      });
                  alertDialog.setButton(
                      AlertDialog.BUTTON_POSITIVE,
                      "Try agin",
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                          mSplashPresenter.startFetch();
                        }
                      });
                  alertDialog.show();
                }
              }
            });
  }
}
