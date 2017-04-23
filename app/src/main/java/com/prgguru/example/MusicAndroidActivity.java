package com.prgguru.example;

import java.io.IOException;
import java.util.List;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
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
	void SendMsg(String msg){
		log.append(msg+"\n");
	}

	static MediaPlayer mPlayer;
	Button buttonCat,buttonCha,buttonStop,buttonRef;
	EditText log;

	String m_current_categories = "Internet";
	String m_current_channel_name = "FNF FM";

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

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
				stop();
			}
		});
		buttonRef.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new ServiceProxy().execute();
			}
		});
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
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
						//new PlayOperation().execute(); //It causing carsh,,,
						play();
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

	String play(){
		stop();
		String msg="";
		String url = ChannelList.getChannelDetails(m_current_channel_name);
		msg+=("buttonPlay.setOnClickListener");
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}
		try {
			msg+=("mPlayer.setDataSource");
			mPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {
			//Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			msg+=("IllegalArgumentException");
		} catch (SecurityException e) {
			//Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			msg+=("SecurityException");
		} catch (IllegalStateException e) {
			//Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			msg+=("IOException");
		} catch (IOException e) {
			e.printStackTrace();
			msg+=("IOException");
		}
		try {
			msg+=("mPlayer.prepare");
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			msg+=("IllegalStateException");
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			msg+=("IOException");
		}
		SendMsg("mPlayer.start");
		mPlayer.start();
		buttonCha.setText(m_current_channel_name);
		return msg;
	}
	boolean stop(){
		// TODO Auto-generated method stub
		if(mPlayer!=null && mPlayer.isPlaying()){
			SendMsg("mPlayer.stop");
			mPlayer.stop();
			if (mPlayer != null) {
				mPlayer.release();
				mPlayer = null;
			}
			return true;
		}
		return false;
	}

	private class PlayOperation extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			SendMsg(play());
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			buttonCha.setText(m_current_channel_name);
		}

		@Override
		protected void onPreExecute() {
			buttonCha.setText("Try playing .."+m_current_channel_name);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}
}
