package com.mochalov.readrec;

import java.io.File;
import java.util.ArrayList;

import alex.xolo.readrec.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mochalov.data.Data;
import com.mochalov.files.FileLoader;
import com.mochalov.files.FileSaver;
import com.mochalov.files.SelectFileDialog;
import com.mochalov.readrec.ActivityMain.states.State;
import com.mochalov.readrec.ImgView.OnEventListener;
import com.mochalov.readrec.SeekBarV.OnCustomEventListener;

/**
 * @author Alexey Mochalov
 * 
 *         The application is designed to train the reading of the text with
 *         intonation. It works with texts in formats txt, html, fb2. The user
 *         marks in the text the risings and lowerings of tone. The user can
 *         record readed text and listen and save it with the notes.
 * 
 */
public class ActivityMain extends Activity {
	Context mContext;

	// States of the application. They are displayed in toolbar menu
	static class states {
		enum State {
			read, record, play, edit
		}
	}

	public State mState = State.read;

	// Visual elements
	ImgView imgView;
	SeekBarV seekBar;
	ProgressBar progressBar;

	// Strings of the read text. They are not formatted for displaying.
	ArrayList<String> strings = new ArrayList<String>();

	String mFileName;

	private String initPath = "/sdcard"; // initial directory for the text files
											// // storage
	private static final String FILE_EXT[] = { ".txt", ".fb2", ".zip", ".xml" };

	private static final String PREFS_FILE_NAME = "PREFS_FILE_NAME";
	private static final String PREFS_TEXT_SIZE = "PREFS_TEXT_SIZE";
	private static final String PREFS_SHOW_LINES = "PREFS_SHOW_LINES";
	private static final String PREFS_SHOW_BG_COLORS = "PREFS_SHOW_BG_COLORS";

	private static final int SELECT_FILE = 1;
	private static final int SETUP = 2;

	ProgressDialog progressDialog;

	private boolean showNums = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setMainLayout();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Vars.textSize = prefs.getInt(PREFS_TEXT_SIZE, 24);
		Vars.showLines = prefs.getBoolean(PREFS_SHOW_LINES, true);
		Vars.showBGColors = prefs.getBoolean(PREFS_SHOW_BG_COLORS, true);

		imgView.setParams(this);

		MenuControl.setActionBar(this);

		mContext = this;

		checkAppDirectory();

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			// If application is called from "share via"
			// Load strings from the parameter
			String sharedText;
			if ("text/html".equals(type))
				sharedText = intent.getStringExtra(Intent.EXTRA_HTML_TEXT);
			else
				sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
			mFileName = Vars.APP_FOLDER + "/new file.txt";
			loadFile(mFileName, sharedText);
		} else {
			// If application is opened as usual
			// Load strings from file with saved name
			mFileName = prefs.getString(PREFS_FILE_NAME, "");
			if (!mFileName.equals("")) {
				loadFile(mFileName, null);
			}

		}
		// Read the list of the records
		RecsArray.load();
	}

	@Override
	protected void onResume() {
		super.onResume();
 
		// Set event listeners for Data class
		Data.listener = new Data.OnEventListener() {
			@Override
			public void callbackCalcPolyLines() {
				Data.calcPolyLines();
			}
		};

		// Set event listeners for FileLoader class
		FileLoader.listener = new FileLoader.OnEventListener() {
			@Override
			public void callbackCalcPolyLines() {
				Data.calcPolyLines();
			}
		};

		// Set event listeners for Media class
		Media.listener = new Media.OnEventListener() {
			@Override
			public void onSetState(ActivityMain.states.State state) {
				mState = state;
				if (mState == State.play)
					;// ///MenuControl.itemPause.setIcon(R.drawable.pausec);
			}

			@Override
			public void onMessage(String text) {
				Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onTimeTick(String time) {
				MenuControl.setActionBarSubtitle(time);
			}

			@Override
			public void onPlayCompletion() {
				mState = State.read;
				MenuControl.setMenuVisability(mState);
			}
		};
	}

	/**
	 * Set main layout  
	 * Find visual elements on the main layout and set event listeners they
	 */
	private void setMainLayout() {
		mState = State.read;

		setContentView(R.layout.activity_main);

		seekBar = (SeekBarV) findViewById(R.id.SeekBarV);
		seekBar.setVisibility(View.INVISIBLE);

		imgView = (ImgView) findViewById(R.id.imgView);
		imgView.setEventListener(new OnEventListener() {
			@Override
			public void onSlide(int firstLine) {
				seekBar.setProgress(firstLine);
			}

			@Override
			public void callbackCalcPolyLines() {
				Data.calcPolyLines();
			}

			@Override
			public void callbackCall(String action) {
				if (action == "ShowSlideBar") {
					if (seekBar.getVisibility() == View.VISIBLE)
						seekBar.setVisibility(View.INVISIBLE);
					else
						seekBar.setVisibility(View.VISIBLE);
				}
			}
		});

		seekBar.setCustomEventListener(new OnCustomEventListener() {
			@Override
			public void onChanged(int progress) {
				imgView.setFirstLine(progress);
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.INVISIBLE);

		imgView.setParams(this);
	}

	/**
	 * Set menu and start or stop showing Media time
	 */
	private void setButtons() {
		MenuControl.setMenuVisability(mState);

		if (mState == State.read) {
		} else if (mState == State.record) {
			Media.dropTimer();
			seekBar.setVisibility(View.INVISIBLE);
			Media.startShowTime(State.record);
		} else if (mState == State.play) {
			Media.startShowTime(State.play);
		}

	}

	/**
	 * Call reading file and parse string to screen width 
	 * 
	 * @param fileName - name of the loaded file
	 * @param text - if not empty, load strings from text, not from file  
	 */
	private void loadFile(String fileName, String text) {
		imgView.reset();
		mFileName = fileName; // **//////
		progressBar.setVisibility(View.VISIBLE);
		FileLoader.load(fileName, strings, text, this);
		progressBar.setVisibility(View.INVISIBLE);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		imgView.setParams(ActivityMain.this);
		imgView.parseFileAsinc(strings, displaymetrics.widthPixels,
				progressBar, seekBar);

	}

	/**
	 * Extract path from the full path of the loaded file 
	 * 
	 * @param srcPath - name of the loaded file
	 */
	private void getInitPath(String srcPath) {
		int dotposition = srcPath.lastIndexOf("/");
		initPath = srcPath.substring(0, dotposition + 1);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Stop Media
		Media.stopRecording();
		Media.stopPlaying();

		MenuControl.storeMenu(menu);

		MenuControl.setMenuVisability(mState);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.homeAsUp:
		// case R.id.home:
		case android.R.id.home:
			// Change stats read/edit
			if (mState == State.read) {
				mState = State.edit;
				seekBar.setVisibility(View.INVISIBLE);
				imgView.invalidate();
			} else {
				mState = State.read;
				imgView.invalidate();
				Data.calcPolyLines();
			}

			MenuControl.setMenuVisability(mState);
			return true;
		case R.id.action_rec:
			// Start voice recording
			Media.startRecording();
			setButtons();
			return true;
		case R.id.action_stop:
			// Stop recording or playing
			if (mState == State.record)
				Media.stopRecording();
			else if (mState == State.play)
				Media.stopPlaying();

			mState = State.read;
			setButtons();
			return true;
		case R.id.action_pause:
			// Pause playing
			Media.playPause();

			return true;
		case R.id.action_play:
			// Start playing of the current record
			Media.startPlaying();
			setButtons();
			return true;
		case R.id.action_add:
			// Set next state of the imgView (add mark/move marks/scroll text)
			MenuControl.setItenAddIcon(imgView.nextState());
			imgView.invalidate();
			return true;
		case R.id.action_delete:
			// Delete selected mark
			imgView.deleteSelectedMark();
			Data.calcPolyLines();
			return true;
		case R.id.action_link:
			// Delete link marks
			imgView.linkMarks();
			Data.calcPolyLines();
			return true;
		case R.id.action_undo:
			// Undo marks changing
			imgView.undo();
			Data.calcPolyLines();
			return true;
		case R.id.action_save_file:
			// Save text file as xml
			String name = mFileName;
			if (name.endsWith(".zip"))
				name = name.substring(0, (name.lastIndexOf(".")));
			name = name.substring(0, (name.lastIndexOf("."))) + ".xml";

			if (name.lastIndexOf("/") >= 0)
				name = name.substring(name.lastIndexOf("/") + 1);
			saveFile(name);
			return true;
		case R.id.action_settings:
			// Call settings dialog
			DialogSettings dialogSettings = new DialogSettings(this, showNums);
			dialogSettings.execute();
			return true;
		case R.id.action_info:
			// Show info dialog
			DialogInfo dialogInfo = new DialogInfo(this, mFileName);
			dialogInfo.execute();
			return true;
		case R.id.action_open_file:
			// Show dialog for the file selection
			SelectFileDialog selectFileDialog = new SelectFileDialog(this, initPath, mFileName,
					FILE_EXT, "", false, false, "");
			selectFileDialog.callback = new SelectFileDialog.MyCallback() {
				@Override
				public void callbackACTION_SELECTED(String fileName) {
					imgView.cancelTask();
					getInitPath(mFileName);
					loadFile(fileName, null);
				}
			};

			selectFileDialog.show();
			return true;
		case R.id.action_cut_fragment:
			// Show dialog for cutting fragment of the text
			DialogCutFragment dialogCutFragment = new DialogCutFragment(this);
			dialogCutFragment.execute(Data.getStringsSize() - 1);
			return true;
		case R.id.action_clear_marks:
			// Clear array of the marks
			Data.clearMarks();
			Data.calcPolyLines();
			imgView.invalidate();
			return true;
		case R.id.action_save_file_as:
			// Show dialog for saving text file
			DialogFileSave dialogFileSave = new DialogFileSave(this);
			dialogFileSave.execute(mFileName);
			return true;
		case R.id.action_save_audio:
			// Show dialog for saving audio record
			String s = mFileName.substring(0, (mFileName.lastIndexOf(".")))
					.substring(mFileName.lastIndexOf("/") + 1);

			DialogSaveRecord dlgSaveRec = new DialogSaveRecord(this);
			dlgSaveRec.execute(s);

			return true;
		case R.id.action_open_audio:
			// Show dialog for open audio record
			DialogSelectRec dialog = new DialogSelectRec(this);
			dialog.callback = new DialogSelectRec.MyCallback() {
				@Override
				public void callbackSelected(int index) {
					Media.open(RecsArray.getFileName(index));
				}
			};
			dialog.execute();
			return true;
		default:

			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		Media.onPause();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Editor editor = prefs.edit();
		editor.putInt(PREFS_TEXT_SIZE, Vars.textSize);
		editor.putBoolean(PREFS_SHOW_LINES, Vars.showLines);
		editor.putBoolean(PREFS_SHOW_BG_COLORS, Vars.showBGColors);
		editor.putString(PREFS_FILE_NAME, mFileName);
		editor.apply();

		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mState == State.edit) {
				mState = State.read;
				imgView.invalidate();

				MenuControl.setMenuVisability(mState);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * This is directory for saving text files (as xml), audio records
	 * and description of the audio files
	 */
	void checkAppDirectory() {
		File file = new File(Vars.APP_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public boolean getShowNums() {
		return showNums;
	}

	public void changeShowNums() {
		showNums = !showNums;
		imgView.invalidate();
	}

	public void setShowNums(boolean showNums) {
		this.showNums = showNums;
		imgView.invalidate();
	}

	public void setShowLines(boolean showLines) {
		Vars.showLines = showLines;
		imgView.invalidate();
	}

	public void setShowBGColors(boolean showBGColors) {
		Vars.showBGColors = showBGColors;
		imgView.invalidate();
	}

	public void setTextSize(int textSize) {
		Vars.textSize = textSize;

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		imgView.setPaints();
		imgView.parseFileAsinc(strings, displaymetrics.widthPixels,
				progressBar, seekBar);
	}

	/**
	 * Save text file as xml
	 * If file extension changed then reload file 
	 * 
	 * @param name - is the name of the text file
	 */
	public void saveFile(String name) {
		FileSaver.save(name, Vars.APP_FOLDER);
		name = Vars.APP_FOLDER + "/" + name;

		if (!name.equals(mFileName)) {
			mFileName = name;
			loadFile(mFileName, null);
		}
	}

	/**
	 * Cut fragment from text file. Strings there are not in the fragment will be removed  
	 * 
	 * @param cutFrom - first line of the fragment
	 * @param cutTo - last line of the fragment
	 */
	public void cutFragment(int cutFrom, int cutTo) {
		int stringsCount = Data.getStringsSize();
		int marksCount = Data.getMarksSize();

		if (cutTo > cutFrom && cutFrom >= 0 && cutTo <= stringsCount - 1) {
			for (int i = stringsCount - 1; i > cutTo; i--)
				Data.removeString(i);
			for (int i = cutFrom - 1; i >= 0; i--)
				Data.removeString(i);

			for (int i = marksCount - 1; i >= 0; i--)
				if (Data.getMarkLine(i) > stringsCount)
					Data.removeMark(i);
		}
		imgView.invalidate();
	}
}
