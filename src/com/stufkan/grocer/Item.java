package com.stufkan.grocer;

import android.util.Log;

import java.util.Comparator;

public class Item implements Comparable<Item>{
	int _id;
	String Name;
	private boolean selected;
	int timesBought;
	
	public Item()
	{}
	
	public Item(int id, String Name, int timesBought)
	{
		this._id = id;
		this.Name =Name; 
		this.selected = false;
		this.timesBought = timesBought;
	}
	
	public Item(String Name)
	{
		this.Name = Name;
		this.timesBought = 0;
	}
	
	public int getId()
	{
		return this._id;
	}
	
	public void setId(int id)
	{
		this._id = id;
	}

	public String getName()
	{
		return Name;
	}
	
	public void setName(String Name)
	{
		this.Name = Name;
	}
	
	public int getTimesBought()
	{
		return this.timesBought;
	}
	
	public void incrementTimesBought()
	{
		this.timesBought++;
	}
	
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
        Log.w("grocer",Name + " was set to " + (selected ? "selected" : "unselected"));
    }

    public  boolean getSelected()
    {
        return selected;
    }

	@Override
	public String toString() {
		return Name;
	}

	public int compareTo(Item another) {
		return this.Name.compareTo(another.Name);
	}
	
	public static Comparator<Item> ItemTimesBoughtComparator = new Comparator<Item>() {
		
	public int compare(Item item1, Item item2)
	{
		int diff = item2.getTimesBought() - item1.getTimesBought();
		if(diff!=0)
			return diff;
		else return item1.Name.compareTo(item2.Name);			
	}
	
	};
	
	public static Comparator<Item> NameComparator = new Comparator<Item>() {
		public int compare(Item item1, Item item2){
			return item1.getName().compareTo(item2.getName());
		}
	};

	
}