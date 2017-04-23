package com.prgguru.example;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MusicAndroidActivity extends Activity {
	void SendMsg(String msg){
		log.append(msg+"\n");
	}

	static MediaPlayer mPlayer;
	Button buttonPlay;
	Button buttonStop;
	EditText log;
	//String url = "http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3";
	//String url = "";
	String[][] FM = {
			{"FNF FM", "http://94.23.36.117/proxy/arrahm00?mp=/stream"},
			{"AIR BENGALI", "http://airlive.nic.in/hls-live/livepkgr/_definst_/bengali/bengali.m3u8"},
			{"Mirchi top 20", "http://mt20live-lh.akamaihd.net/i/mt20live_1@346531/master.m3u8"},
			{"Mithe Mirchi", "http://meethimirchihdl-lh.akamaihd.net/i/MeethiMirchiHDLive_1_1@320572/master.m3u8"},
			{"Puraje Jeans", "http://puranijeanshdliv-lh.akamaihd.net/i/PuraniJeansHDLive_1_1@334555/master.m3u8"},
			{"Filmy Mirchi", "http://filmymirchihdliv-lh.akamaihd.net/i/FilmyMirchiHDLive_1_1@336266/master.m3u8"},
			{"Mirchi PhelaNeha", "http://pehlanashahdlive-lh.akamaihd.net/i/PehlaNashaHDLive_1_1@335229/master.m3u8"},
			{"Club Mirchi", "http://clubmirchihdlive-lh.akamaihd.net/i/ClubMirchiHDLive_1_1@336269/master.m3u8"},
			{"Mirchi Mehefil", "http://mirchimahfil-lh.akamaihd.net/i/MirchiMehfl_1@120798/master.m3u8"},
			{"Wakaao Mirchi", "http://wmirchi-lh.akamaihd.net/i/WMIRCHI_1@75780/master.m3u8"},
			{"Radio City", "http://prclive1.listenon.in:9960/"},
			{"BongNet","https://radio.bongonet.net:9000/;stream.mpeg"},
			{"Vividh Bhrati","http://airlive.nic.in/hls-live/livepkgr/_definst_/vividhbharti.m3u8"},
			{"Telegu","http://android.programmerguru.com/wp-content/uploads/2013/04/hosannatelugu.mp3"}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		buttonPlay = (Button) findViewById(R.id.play);
		log = (EditText)findViewById(R.id.logs);
		//log.setEnabled(false);
		final int[] idx = {0};

		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		buttonPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(idx[0] == FM.length){
					idx[0] =0;
				}
				String[]  now = FM[idx[0]];
				SendMsg("Stoping player....");
				stop();
				SendMsg("Changing channel...");
				if( play(now[1]) == true){
					SendMsg("Started....");
					buttonPlay.setText(now[0]);
				}
				idx[0]++;
			}
		});
		
		buttonStop = (Button) findViewById(R.id.stop);
		buttonStop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stop();
			}
		});
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	boolean play(String url){
		SendMsg("buttonPlay.setOnClickListener");
		if(mPlayer == null) {
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}
		try {
			SendMsg("mPlayer.setDataSource");
			mPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			SendMsg("IllegalArgumentException");
		} catch (SecurityException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			SendMsg("SecurityException");
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			SendMsg("IOException");
		} catch (IOException e) {
			e.printStackTrace();
			SendMsg("IOException");
		}
		try {
			SendMsg("mPlayer.prepare");
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			SendMsg("IllegalStateException");
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
			SendMsg("IOException");
		}
		SendMsg("mPlayer.start");
		mPlayer.start();
		return true;
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

}
