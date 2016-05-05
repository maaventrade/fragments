package com.mochalov.readrec;
import com.mochalov.readrec.ActivityMain.states.State;

import alex.xolo.readrec.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.*;

public final class MenuControl
{
	static ActionBar actionBar;
	static private Menu mMenu;

	public static void setItenAddIcon(int nextState)
	{
		mMenu.findItem(R.id.action_add).setIcon(nextState);
		// TODO: Implement this method
	}
	
	public static void setMenuVisability(State state) {
		if (state == State.record) {
			mMenu.setGroupVisible(R.id.group_edit, false);
			mMenu.setGroupVisible(R.id.group_record, true);

			mMenu.findItem(R.id.action_rec).setIcon(R.drawable.recordu);
			mMenu.findItem(R.id.action_rec).setEnabled(false);
			
			mMenu.findItem(R.id.action_play).setIcon(R.drawable.playu);
			mMenu.findItem(R.id.action_play).setEnabled(false);

			mMenu.findItem(R.id.action_stop).setIcon(R.drawable.stopc);
			mMenu.findItem(R.id.action_stop).setEnabled(true);
			
			actionBar.setIcon(R.drawable.pencil);
			actionBar.setTitle("");
		} else if (state == State.play) {
			mMenu.setGroupVisible(R.id.group_edit, false);
			mMenu.setGroupVisible(R.id.group_record, true);

			mMenu.findItem(R.id.action_rec).setIcon(R.drawable.recordu);
			mMenu.findItem(R.id.action_rec).setEnabled(false);
			
			mMenu.findItem(R.id.action_play).setIcon(R.drawable.playu);
			mMenu.findItem(R.id.action_play).setEnabled(false);

			mMenu.findItem(R.id.action_stop).setIcon(R.drawable.stopc);
			mMenu.findItem(R.id.action_stop).setEnabled(true);

			mMenu.findItem(R.id.action_pause).setIcon(R.drawable.pausec);
			mMenu.findItem(R.id.action_pause).setEnabled(true);
			
			actionBar.setIcon(R.drawable.pencil);
		    actionBar.setTitle("");
		} else if (state == State.read) { 
			mMenu.setGroupVisible(R.id.group_edit, false);
			mMenu.setGroupVisible(R.id.group_record, true);

			mMenu.findItem(R.id.action_rec).setIcon(R.drawable.recordc);
			mMenu.findItem(R.id.action_rec).setEnabled(true);

			mMenu.findItem(R.id.action_play).setIcon(R.drawable.playc);
			mMenu.findItem(R.id.action_play).setEnabled(true);

			mMenu.findItem(R.id.action_stop).setIcon(R.drawable.stopu);
			mMenu.findItem(R.id.action_stop).setEnabled(false);

			mMenu.findItem(R.id.action_pause).setIcon(R.drawable.pauseu);
			mMenu.findItem(R.id.action_pause).setEnabled(false);
			
			actionBar.setIcon(R.drawable.pencil);
			
		    actionBar.setTitle("");
		    actionBar.setSubtitle("");
		} else {
			mMenu.setGroupVisible(R.id.group_edit, true);
			mMenu.setGroupVisible(R.id.group_record, false);
			//mMenu.findItem(R.id.submenu1).setVisible(true);

	        actionBar.setIcon(R.drawable.mic);
		}
		
		/*
		String name = fileName;
	   	 if (name.lastIndexOf("/") >= 0)
	   		 name = name.substring(name.lastIndexOf("/")+1);
		actionBar.setSubtitle(name);
		*/

	}


	@SuppressLint("NewApi")
	public static void setActionBar(Context context) {
        actionBar = ((Activity)context).getActionBar();
        actionBar.setHomeButtonEnabled(true);

        // In the "limited edition" we hide Home button
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.edit);
	}


	public static void setActionBarSubtitle(String time) {
		actionBar.setSubtitle(time);	
	}

	public static void storeMenu(Menu menu) {
		mMenu = menu;
	}
	
}
