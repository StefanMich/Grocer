package com.stufkan.grocer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.stufkan.grocer.Second.sortMode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_AVAILABLE = "available";
	public static final String TABLE_CURRENT = "current";

	public static final String KEY_ID = "id";
	public static final String KEY_NAME  = "name";
	public static final String KEY_TIMESBOUGHT = "timesBought";

	private static final String DATABASE_NAME = "grocer";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_ITEM_TABLE = "create table "
			+ TABLE_AVAILABLE + "(" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null," + KEY_TIMESBOUGHT + " integer);";

	private static final String CREATE_CURRENT_TABLE = "create table "
			+ TABLE_CURRENT + "(" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null," + KEY_TIMESBOUGHT + " integer);";

	public MySQLiteHelper(Context context)
	{
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_ITEM_TABLE);
		database.execSQL(CREATE_CURRENT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " +TABLE_AVAILABLE);
		db.execSQL("DROP TABLE IF EXISTS " +TABLE_CURRENT);
		onCreate(db);

	}

	//CRUD for the available list
	public void addItemAvailable(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TIMESBOUGHT, item.getTimesBought());
		//todo gem disse value.put-statements i en funktion så de er lette at opdatere

		db.insert(TABLE_AVAILABLE, null, values);
		db.close();
	}

	public Item getItem(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_AVAILABLE, new String[]{KEY_ID, KEY_NAME}, "=?", new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor!= null)
			cursor.moveToFirst();

		Item item = new Item(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
		return item;
	}

	public List<Item> getAllItems(sortMode sortMode){
		List<Item> itemList = new ArrayList<Item>();

		String selectQuery = "SELECT * FROM "+ TABLE_AVAILABLE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst())
		{
			do{
				Item item = new Item(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
				itemList.add(item);
			}
			while(cursor.moveToNext());
		}

		SortList(itemList, sortMode);
		return itemList;		
	}

	public int getItemCount()
	{
		String countQuery = "SELECT * FROM" + TABLE_AVAILABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =  db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	public int updateItem(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values =  new ContentValues();
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TIMESBOUGHT, item.getTimesBought());

		return db.update(TABLE_AVAILABLE, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});

	}

	public void deleteItemAvailable(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_AVAILABLE, KEY_ID + "= ?", new String[]{String.valueOf(item.getId())});
		db.close();
	}

	//
	//CRUD for the current list
	//
	public void addItemCurrent(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TIMESBOUGHT, item.getTimesBought());

		db.insert(TABLE_CURRENT, null, values);
		db.close();
	}

	public Item getItemCurrent(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CURRENT, new String[]{KEY_ID, KEY_NAME}, "=?", new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor!= null)
			cursor.moveToFirst();

		Item item = new Item(Integer.parseInt(cursor.getString(0)), cursor.getString(1),Integer.parseInt(cursor.getString(2)));
		return item;
	}

	public List<Item> getAllItemsCurrent(sortMode sortMode){
		List<Item> itemList = new ArrayList<Item>();

		String selectQuery = "SELECT * FROM "+ TABLE_CURRENT;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst())
		{
			do{
				Item item = new Item(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
				itemList.add(item);
			}
			while(cursor.moveToNext());
		}

		SortList(itemList, sortMode);

		return itemList;		
	}

	public int getItemCountCurrent()
	{
		String countQuery = "SELECT * FROM" + TABLE_CURRENT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =  db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}

	public int updateItemCurrent(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values =  new ContentValues();
		values.put(KEY_NAME, item.getName());
		values.put(KEY_TIMESBOUGHT, item.getTimesBought());

		return db.update(TABLE_CURRENT, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});

	}

	public void deleteItemCurrent(Item item)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CURRENT, KEY_ID + "= ?", new String[]{String.valueOf(item.getId())});
		db.close();
	}

	//Transfer between the two tables
	public void CurrentToAvailable(Item item)
	{
		deleteItemCurrent(item);
		addItemAvailable(item);
	}

	public void AvailableToCurrent(Item item)
	{
		addItemCurrent(item);
		deleteItemAvailable(item);
	}

	private void SortList(List<Item> list, sortMode sMode)
	{
		if(sMode == sortMode.Alphabetically)
			Collections.sort(list);
		else if(sMode == sortMode.Frequency)
			Collections.sort(list, Item.ItemTimesBoughtComparator);
	}


}
