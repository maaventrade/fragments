package com.mochalov.readrec;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import com.mochalov.readrec.ActivityMain.states.State;

public class Media {
	static MediaRecorder mediaRecorder = null;          
	static MediaPlayer mediaPlayer = null;
	
	static long timeOfRecordStarting = 0;
	static long durationOfRecord = 0;
	static Handler handler = new Handler();
	
	static State mState;
	
	static String tempMediaFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/readrectmp.3gp";
	static String playingMediaFileName = tempMediaFileName;
	
	public static OnEventListener listener;

	public static void open(String fileName)
	{
		playingMediaFileName = Vars.APP_FOLDER+"/"+fileName;
	}

	public static CharSequence msdToString(long l)
	{
		//Calendar c = Calendar.getInstance();
		//c.
		
		long second = (l / 1000) % 60;
        long minute = (l / (1000 * 60)) % 60;
        long hour = (l / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, l-
							 second*1000-
							 minute*1000*60-
							 hour*1000 * 60 * 60);
	
	}

	public static long getDuration()
	{
		return durationOfRecord;
	}
	

	public static long getTimeStarting()
	{
		return timeOfRecordStarting;
	}
	

	public static void onPause()
	{
		if (mediaRecorder != null) {            
			mediaRecorder.release();            
			mediaRecorder = null;        
		}        
		if (mediaPlayer != null) {            
			mediaPlayer.release();            
			mediaPlayer = null;        
		}
		
		// TODO: Implement this method
	}
	
	public static String getTempMediaFileName(){
		return tempMediaFileName;
	}
	
	public static void playPause()
	{
		if (mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			//itemPause.setIcon(R.drawable.playc);
		} else {
			//itemPause.setIcon(R.drawable.pausec);
			mediaPlayer.start();
		}
		
		// TODO: Implement this method
	}
	public interface OnEventListener{
		void onTimeTick(String time);
		void onMessage(String text);
		void onSetState(State state);
		void onPlayCompletion();
	}

	
    private static Runnable updateTimeTask = new Runnable() { 
		   public void run() {
			   if (mState == State.record){
				   durationOfRecord = System.currentTimeMillis() - timeOfRecordStarting;
				   if (listener != null)
					   listener.onTimeTick(""+msToString(durationOfRecord));				   
	
				   handler.postDelayed(this, 100);
			   } else if (mState == State.play){
				  // seekBarBarPlay.setProgress(mediaPlayer.getCurrentPosition());
				   if (listener != null)
					   listener.onTimeTick(""+msToString(mediaPlayer.getCurrentPosition()));				   
				   handler.postDelayed(this, 100);
			   } else
			   	   handler.removeCallbacks(updateTimeTask);
		   } 
	};        
	
	
	public static void dropTimer() {
		timeOfRecordStarting = System.currentTimeMillis();
		durationOfRecord = 0;
	}


	public static void startShowTime(State state) {
		mState = state;
    	handler.postDelayed(updateTimeTask, 1000);
	}

	public static void startRecording() {        
		mediaRecorder = new MediaRecorder();        
		try {            
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        
		} 
		catch (IllegalStateException  e) {  
			if (listener != null)
				listener.onMessage("Error on phone initialisation: " + e.toString());
			
			return;
		}        
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		
		if (listener != null)
			listener.onMessage(tempMediaFileName);
		mediaRecorder.setOutputFile(tempMediaFileName);        
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);        
		try {            
			mediaRecorder.prepare();        
			} 
		catch (IOException e) {      
			if (listener != null)
				listener.onMessage("Error on mediacoder initialisation: " + e.toString());
			return;
		}        
		mediaRecorder.start();
		if (listener != null)
			listener.onSetState(State.record);
		mState = State.record;
		playingMediaFileName = tempMediaFileName;
		RecsArray.setCurent(-1);
	}
	
	public static void stopRecording() {
		if (mediaRecorder != null){
			mediaRecorder.stop();        
			mediaRecorder.release();        
			mediaRecorder = null;
			mState = State.read;
		}
	}
	
	public static void startPlaying() {        
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mState = State.read;
				if (listener != null)
				   listener.onPlayCompletion();				   
			}
		});
		
		try {
			if (listener != null)
				listener.onMessage(playingMediaFileName);
			
			mediaPlayer.setDataSource(playingMediaFileName);            
			mediaPlayer.prepare();            
			mediaPlayer.start();
			
	//		seekBarBarPlay.setMax(mediaPlayer.getDuration());
			if (listener != null)
				listener.onSetState(State.play);
			
			mState = State.play;
	    	
	    	
			} catch (IOException e) {     
				if (listener != null)
					listener.onMessage("" + e.toString());
					return;
			}    
	}	
	
	public static void stopPlaying() {
		if (mediaPlayer != null){
			mediaPlayer.release();        
			mediaPlayer = null;    
			mState = State.read;
		}
	}

	public static String msToString(long l){
        long second = (l / 1000) % 60;
        long minute = (l / (1000 * 60)) % 60;
        long hour = (l / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d,%03d", hour, minute, second, l-
      		  second*1000-
      		  minute*1000*60-
      		  hour*1000 * 60 * 60);
	}
	
}
