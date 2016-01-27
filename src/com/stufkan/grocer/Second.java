package com.stufkan.grocer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class Second extends Activity implements OnClickListener {

    public enum sortMode {Alphabetically, Frequency}

    public static final String PREFS_NAME = "GrocerPrefsFile";

    MySQLiteHelper db;
    TextView tv;
    ListView lv;
    ItemAdapter adapter;
    sortMode sMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new MySQLiteHelper(this);
        adapter = new ItemAdapter(this, R.layout.grocerlistviewitem, db.getAllItems(sMode));

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        sMode = sortMode.valueOf(settings.getString("sortMode", "Alphabetically"));

        setContentView(R.layout.second);


        Button add = (Button) findViewById(R.id.AddNewItem);
        add.setOnClickListener(this);

        Button deleteItems = (Button) findViewById(R.id.DeleteItems);
        deleteItems.setOnClickListener(this);

        Button addItems = (Button) findViewById(R.id.AddItems);
        addItems.setOnClickListener(this);

        tv = (TextView) findViewById(R.id.editText1);

        lv = (ListView) findViewById(R.id.listView1);

        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);

        tv.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("textwatcher", "ontextchanged " + s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                Log.d("textwatcher", "beforetextchanged " + s.toString());
            }

            public void afterTextChanged(Editable s) {

                Log.d("textwatcher", "after " + s.toString());

                adapter.getFilter().filter(s);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        switch (item.getItemId()) {
            case R.id.menu_SortFrequency:
                sMode = sortMode.Frequency;
                resetList();
                break;
            case R.id.menu_sortAlpha:
                sMode = sortMode.Alphabetically;
                resetList();
                break;
        }
        editor.putString("sortMode", sMode.toString());
        editor.commit();

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.AddNewItem) {
            int tvText = tv.getText().length();
            if (tvText != 0)
                db.addItemCurrent(new Item(tv.getText().toString()));
            resetList();
            //adapter.data.get(Collections.binarySearch(items, new Item(tv.getText().toString()), Item.NameComparator)).
            //forsøg på at få tilføjede elementer til at være "selected" fra starten

            tv.setText("");
        } else if (v.getId() == R.id.AddItems) {
            for (Item i : adapter.data) {
                if (i.getSelected() == true) {
                    i.incrementTimesBought();
                    db.AvailableToCurrent(i);
                }
            }
            resetList();
            tv.setText("");
        } else if (v.getId() == R.id.DeleteItems) {
            for (Item i : adapter.data) {
                if (i.getSelected() == true)
                    db.deleteItemAvailable(i);
            }
            resetList();
            tv.setText("");
        }
    }

    private void resetList() {
        adapter.data = db.getAllItems(sMode);
        adapter = new ItemAdapter(this, R.layout.grocerlistviewitem, adapter.data);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
