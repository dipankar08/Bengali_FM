package in.peerreview.fmradioindia.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

public class CommonUtils {
  public static void openPlayStore(Context context) {

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder
        .setMessage(
            "If you are enjoying the app, please give 5 STAR rating and share some feedback by writing the comments")
        .setTitle("Your rating is important for us!")
        .setCancelable(false)
        .setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                final String appPackageName =
                    context.getPackageName(); // getPackageName() from Context or Activity object
                try {
                  context.startActivity(
                      new Intent(
                          Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                  context.startActivity(
                      new Intent(
                          Intent.ACTION_VIEW,
                          Uri.parse(
                              "https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
              }
            });
    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void sendFeedback(Context context) {
    /* Create the Intent */
    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

    /* Fill it with Data */
    emailIntent.setType("plain/text");
    emailIntent.putExtra(
        android.content.Intent.EXTRA_EMAIL, new String[] {"dutta.dipankar08@gmail.com"});
    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback on FM Radio");
    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Write your feedback here");

    /* Send it off to the Activity-Chooser */
    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
  }

  public static void shareApp(Context context) {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Best Bengali FM Radio");
    sharingIntent.putExtra(
        android.content.Intent.EXTRA_TEXT,
        "Missing Radio mirchi? Download best fm radio india radio app here: https://play.google.com/store/apps/details?id=in.peerreview.fmradioindia");
    context.startActivity(Intent.createChooser(sharingIntent, "Share FM Radio"));
  }

  public static void followUs(Context context) {
    PackageManager pm = context.getPackageManager();
    String url = "https://www.facebook.com/Best-Bengali-FM-Radio-1668958716450651";
    Uri uri = Uri.parse(url);
    try {
      ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
      if (applicationInfo.enabled) {
        uri = Uri.parse("fb://facewebmodal/f?href=" + url);
      }
    } catch (PackageManager.NameNotFoundException ignored) {
    }
    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
  }

  public static void showCredit(Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder
        .setMessage("This application is developed by Dipankar Dutta")
        .setTitle("About this app")
        .setCancelable(false)
        .setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                // do things
              }
            });
    AlertDialog alert = builder.create();
    alert.show();
  }
}
