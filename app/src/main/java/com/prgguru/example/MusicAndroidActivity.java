package com.prgguru.example;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.Toast;


public class MusicAndroidActivity extends AppCompatActivity {
	private BroadcastReceiver receiver;


	void SendMsg(String msg){
		log.append(msg+"\n");
	}


	static Activity sActivity = null;
	Button buttonCat,buttonCha,buttonStop,buttonRef;
	EditText log;

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
		log = (EditText)findViewById(R.id.logs);
		log.setVisibility(View.INVISIBLE);
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
		buttonRef.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new ServiceProxy().execute();
			}
		});
	}

	private void PermUIActionOnServiceNotification(String s) {
		if(s.equals("PLAY_START")){
			Loading.showPlayProgressDialog();
		} else{
			Loading.hide();
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
		List<String> list = ChannelList.getAllChannelForCategories(m_current_categories);
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
						m_current_channel_name = checkedItem.toString();
						Intent i=new Intent(MusicAndroidActivity.Get(), BackgroundSoundService.class);
						i.setAction(BackgroundSoundService.ACTION_PLAY);
						i.putExtra("Name", m_current_channel_name);
						startService(i);
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
