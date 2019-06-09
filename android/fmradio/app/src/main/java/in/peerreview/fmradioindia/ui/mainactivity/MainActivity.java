package in.peerreview.fmradioindia.ui.mainactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.home.HomeScreen;
import in.peerreview.fmradioindia.ui.pref.UserPrefView;
import in.peerreview.fmradioindia.ui.search.SearchView;
import in.peerreview.fmradioindia.ui.splash.SplashScreen;

public class MainActivity extends AppCompatActivity implements BaseView<MainState> {
  private SplashScreen mSplashView;
  private SearchView mSearchView;
  private HomeScreen mHomeView;
  private UserPrefView mUserPrefView;
  private MainPresenter mPresenter;
  private DrawerLayout mDrawerLayout;

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent != null && intent.getExtras() != null) {
      // DLog.d("onNewIntent " + intent.getExtras().toString());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); // will hide the title
    getSupportActionBar().hide(); // hide the title bar
    this.getWindow()
        .setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); // enable full screen
    setContentView(R.layout.activity_main1);

    mSplashView = findViewById(R.id.splash);
    mSearchView = findViewById(R.id.search);
    mHomeView = findViewById(R.id.home);
    mUserPrefView = findViewById(R.id.user_pref);
    mDrawerLayout = findViewById(R.id.drawer_layout);

    mSearchView.addCallback(
        new SearchView.Callback() {
          @Override
          public void onClose() {
            AnimationUtil.close(AnimationUtil.AnimationType.OPEN_CLOSE_FROM_BUTTON, mSearchView);
          }

          @Override
          public void onOpen() {}
        });
    mSplashView.addCallback(
        new SplashScreen.Callback() {
          @Override
          public void onLoadSuccess() {
            AnimationUtil.switchFromSpashToHomeScreen(
                getApplicationContext(), mSplashView, mHomeView);
            mPresenter.onLoadSuccess();
          }
        });
    mHomeView.addCallback(
        new HomeScreen.Callback() {
          @Override
          public void onSearchClick() {
            AnimationUtil.open(AnimationUtil.AnimationType.OPEN_CLOSE_FROM_BUTTON, mSearchView);
          }

          @Override
          public void onSettingClick() {
            AnimationUtil.open(AnimationUtil.AnimationType.OPEN_CLOSE_FROM_BUTTON, mUserPrefView);
          }

          @Override
          public void onMenuClick() {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
              mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
              mDrawerLayout.openDrawer(GravityCompat.START);
            }
          }
        });
    mUserPrefView.addCallback(
        new UserPrefView.Callback() {
          @Override
          public void onClose() {
            AnimationUtil.close(AnimationUtil.AnimationType.OPEN_CLOSE_FROM_BUTTON, mUserPrefView);
          }
        });
    setupDrawer();
    mPresenter = new MainPresenter();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mPresenter.attachView(this);
  }

  @Override
  protected void onPause() {
    mPresenter.detachView();
    super.onPause();
  }

  @Override
  public void render(final MainState state) {
    runOnUiThread(
        new Runnable() {
          @Override
          public void run() {}
        });
  }

  @Override
  public void onBackPressed() {
    new AlertDialog.Builder(this)
        .setMessage("You are about to exit the app. Do you want to keep playing after exit?")
        .setCancelable(false)
        .setPositiveButton(
            "Exit but keep playing",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                MainActivity.super.onBackPressed();
              }
            })
        .setNeutralButton(
            "Cancel dialog",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {}
            })
        .setNegativeButton(
            "Stop Music and Exit",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                mPresenter.onStopPlay();
                MainActivity.super.onBackPressed();
              }
            })
        .show();
  }

  private void setupDrawer() {
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(MenuItem menuItem) {
            // set item as selected to persist highlight
            menuItem.setChecked(true);
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers();

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            return true;
          }
        });
  }
}
