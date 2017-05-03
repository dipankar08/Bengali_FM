package in.peerreview.bengalifm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MusicAndroidActivity extends AppCompatActivity {
	private BroadcastReceiver receiver;


	void SendMsg(String msg){
		//log.append(msg+"\n");
	}


	static Activity sActivity = null;
	Button buttonCat,buttonCha,buttonStop,buttonRef,buttonRate;
	TextView status;

	String m_current_categories = "Internet";
	String m_current_channel_name = "FNF FM";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		buttonCat = (Button) findViewById(R.id.select_categories);
		buttonCha = (Button) findViewById(R.id.select_channel);
		buttonStop = (Button) findViewById(R.id.stop);
		buttonRef = (Button) findViewById(R.id.refresh);
		buttonRate = (Button) findViewById(R.id.rate);
		status = (TextView)findViewById(R.id.status);
		//log = (EditText)findViewById(R.id.logs);
		//log.setVisibility(View.INVISIBLE);
		LinearLayout layout =(LinearLayout)findViewById(R.id.back);
		final int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.back) );
		} else {
			layout.setBackground( getResources().getDrawable(R.drawable.back));
		}
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String s = intent.getStringExtra(BackgroundSoundService.COPA_MESSAGE);
				PermUIActionOnServiceNotification(s);
			}
		};


		buttonCat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				chooseCategoriesDialog();
			}
		});
		buttonCha.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				chooseChannelDialog();
			}
		});
		buttonStop = (Button) findViewById(R.id.stop);
		buttonStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Player.stop();
			}
		});


		buttonRate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});
	}

	private void PermUIActionOnServiceNotification(String s) {
		//Loading.hide();
		if(s.equals("PLAY_STARTED")){
			//Loading.hide();
		} else{ //PLAY_Error
			//Loading.hide();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		// we allow playing in backgroud...
		//Player.Destroy();

	}
	public static Activity Get(){
		return sActivity;
	}

	public void chooseCategoriesDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(MusicAndroidActivity.this);
		builder.setTitle("Select FM categorie");

		//list of items
		List<String> list = ChannelList.getAllCategories();
		String[] items = list.toArray(new String[list.size()]);
		boolean[] selectedItemsArray = new boolean[items.length];
		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// item selected logic
					}
				});

		String positiveText = getString(android.R.string.ok);
		builder.setPositiveButton(positiveText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView lw = ((AlertDialog)dialog).getListView();
						Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
						m_current_categories = checkedItem.toString();
						chooseChannelDialog();
					}
				});

		String negativeText = getString(android.R.string.cancel);
		builder.setNegativeButton(negativeText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// negative button logic
					}
				});
		builder.setMultiChoiceItems(items, selectedItemsArray,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						//item checked logic
					}
				});
		AlertDialog dialog = builder.create();
		// display dialog
		dialog.show();
	}

	public void chooseChannelDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MusicAndroidActivity.this);
		builder.setTitle("Select FM Channel");

		//list of items
		List<Channel> list = ChannelList.getAllChannelForCategories(m_current_categories);

		String[] items = new String[list.size()];
		for(int i =0;i<list.size();i++){
			items[i] = list.get(i).getName();
		}
		boolean[] selectedItemsArray = new boolean[items.length];
		builder.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// item selected logic
					}
				});

		String positiveText = getString(android.R.string.ok);
		builder.setPositiveButton(positiveText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView lw = ((AlertDialog)dialog).getListView();
						Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
						m_current_channel_name = checkedItem.toString();
						new InvokeService(MusicAndroidActivity.this).execute(m_current_channel_name);

					}
				});

		String negativeText = getString(android.R.string.cancel);
		builder.setNegativeButton(negativeText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// negative button logic
					}
				});
		builder.setMultiChoiceItems(items, selectedItemsArray,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						//item checked logic
					}
				});
		AlertDialog dialog = builder.create();
		// display dialog
		dialog.show();
	}

	public class InvokeService extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog;
		Context appContext;
		public InvokeService(Context ctx) {
			appContext = ctx;
		}
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			pDialog = new ProgressDialog(appContext);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.setCancelable(false);
			pDialog.setMessage("Please Wait! We are trying to play the radio.");
			pDialog.show();
			status.setVisibility(View.INVISIBLE);
		}
		@Override
		protected Void doInBackground(String... params) {
			try {
				Thread.sleep(500);
				Intent i=new Intent(MusicAndroidActivity.Get(), BackgroundSoundService.class);
				i.setAction(BackgroundSoundService.ACTION_PLAY);
				i.putExtra("Name", m_current_channel_name);
//				Loading.showPlayProgressDialog();
				startService(i);

			} catch (Exception e) {
				e.printStackTrace();
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}
			int state = Player.getState();
			if(state == Player.STATE_PLAYING_SUCCESS){
				Toast.makeText(MusicAndroidActivity.this,"State:"+Player.getState(),	Toast.LENGTH_SHORT).show();
				status.setText("Now Playing "+ChannelList.getChannelDetails(m_current_channel_name).getName()+" ...");
				status.setVisibility(View.VISIBLE);
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MusicAndroidActivity.this);
				alertDialogBuilder.setMessage("Not able to play music, Do you want to close the app?");
				alertDialogBuilder.setPositiveButton("yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(MusicAndroidActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
				new IntentFilter(BackgroundSoundService.COPA_RESULT)
		);
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}


}
