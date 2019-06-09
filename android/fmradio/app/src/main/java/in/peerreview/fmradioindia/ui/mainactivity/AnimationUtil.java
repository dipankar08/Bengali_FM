package in.peerreview.fmradioindia.ui.mainactivity;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class AnimationUtil {
  enum AnimationType {
    OPEN_CLOSE_FROM_BUTTON,
    OPEN_CLOSE_FROM_RIGHT,
  }

  public static void switchFromSpashToHomeScreen(Context context, ViewGroup prev, ViewGroup cur) {
    prev.setVisibility(View.GONE);
    cur.setVisibility(View.VISIBLE);
  }

  public static void open(AnimationType type, ViewGroup screen) {
    switch (type) {
      case OPEN_CLOSE_FROM_BUTTON:
        screen
            .animate()
            .translationY(0f)
            .alpha(1.0f)
            .setListener(
                new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {
                    screen.setTranslationY(screen.getHeight());
                    screen.setAlpha(0f);
                    screen.setVisibility(View.VISIBLE);
                  }

                  @Override
                  public void onAnimationEnd(Animator animator) {}

                  @Override
                  public void onAnimationCancel(Animator animator) {}

                  @Override
                  public void onAnimationRepeat(Animator animator) {}
                });
        break;
    }
  }

  public static void close(AnimationType type, ViewGroup screen) {
    switch (type) {
      case OPEN_CLOSE_FROM_BUTTON:
        screen
            .animate()
            .translationY(screen.getHeight())
            .alpha(0f)
            .setListener(
                new Animator.AnimatorListener() {
                  @Override
                  public void onAnimationStart(Animator animator) {}

                  @Override
                  public void onAnimationEnd(Animator animator) {
                    screen.setVisibility(View.GONE);
                  }

                  @Override
                  public void onAnimationCancel(Animator animator) {}

                  @Override
                  public void onAnimationRepeat(Animator animator) {}
                });
        break;
    }
  }
}
