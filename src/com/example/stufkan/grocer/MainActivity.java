package com.example.stufkan.grocer;

/*
 * TODO
 * New -> tilf�j til indk�bsliste
 * search -> select -> ny search giver forkert index
 * liste markering + scroll giver weird index 
 * 
 */

import java.util.List;

import com.example.stufkan.grocer.Second.sortMode;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener{

	MySQLiteHelper db;
	ItemAdapter adapter;
	List<Item> currentItems;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new MySQLiteHelper(this);

		Button b = (Button) findViewById(R.id.btnAdd);
		b.setOnClickListener(this);

		Button checkout = (Button) findViewById(R.id.checkout);
		checkout.setOnClickListener(this);

		lv = (ListView) findViewById(R.id.listView1);

	}

	@Override
	protected void onResume() {
		currentItems = db.getAllItemsCurrent(sortMode.Alphabetically);
		adapter = new ItemAdapter(this,R.layout.grocerlistviewitem,currentItems);
		lv.setAdapter(adapter);

		super.onResume();
	}

	

	public void onClick(View v) {
		if(v.getId() == R.id.btnAdd)
		{
			startActivity(new Intent(MainActivity.this, Second.class));
		}
		else if(v.getId() == R.id.checkout)
		{
			for (Item i : currentItems) {
				if(i.getSelected() == true)
					db.CurrentToAvailable(i);
			}
			currentItems = db.getAllItemsCurrent(sortMode.Alphabetically);
			adapter = new ItemAdapter(this,R.layout.grocerlistviewitem,currentItems);
			lv.setAdapter(adapter);
		
		}
		
	}
}
