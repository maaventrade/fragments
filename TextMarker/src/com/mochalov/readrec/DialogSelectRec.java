package com.mochalov.readrec;
import android.app.*;
import android.content.*;
import alex.xolo.readrec.*;
import android.widget.*;
import android.view.*;

import java.util.*;

import android.graphics.*;

public class DialogSelectRec extends Dialog
 {
	Context context;
	String filePath = "";
	Dialog dialog;
	
	MyCallback callback = null;
	public interface MyCallback {
		void callbackSelected(int index); 
	} 

	public DialogSelectRec(Context context) {
		super(context);
		this.context = context;
		dialog = this;
	}

	public void execute(){
	
		setTitle(context.getResources().getString(R.string.action_open_audio));

 		setContentView(R.layout.list_recs);

	  	ListView list = (ListView)findViewById(R.id.listRecsListView);
		AdapterCanvas adapter; 
        adapter = new AdapterCanvas(context, RecsArray.getArray());

		list.setAdapter(adapter);
		list.setOnItemClickListener( new ListView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int index, long p4)
				{
					callback.callbackSelected(index);
					RecsArray.setCurent(index);
					dialog.dismiss();
				}}
		);
		
		show();
	   
	}
}


	class AdapterCanvas  extends BaseAdapter {
		Context context;
		LayoutInflater lInflater;
		ArrayList objects;

		AdapterCanvas(Context context, ArrayList obj) {
			this.context = context;
			objects = obj;
			lInflater = (LayoutInflater)context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = lInflater.inflate(R.layout.rec_item, parent, false);
			}

			Rec rec = (Rec)objects.get(position);
			TextView t = (TextView)view.findViewById(R.id.itemRecName);
			t.setText(rec.getName());
			
			t = (TextView)view.findViewById(R.id.itemRecTime);
			t.setText(rec.getTime());

			t = (TextView)view.findViewById(R.id.itemRecDuration);
			t.setText(rec.getDuration());
			
			t = (TextView)view.findViewById(R.id.itemRecNote);
			t.setText(rec.getNote());
			
			t = (TextView)view.findViewById(R.id.itemRecLabel);
			t.setText(rec.getLabel());
			
			return view;	
		}
}

