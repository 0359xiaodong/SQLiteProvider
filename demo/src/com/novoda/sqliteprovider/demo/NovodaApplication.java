package com.novoda.sqliteprovider.demo;

import android.app.Application;

import com.novoda.sqliteprovider.demo.persistance.*;

public class NovodaApplication extends Application {

	private DatabaseReader databaseReader;
	private DatabaseWriter databaseWriter;
	private FireworkReader fireworkReader;
	private FireworkWriter fireworkWriter;
	
	public DatabaseReader getDatabaseReader() {
		if(databaseReader == null){
			databaseReader = new DatabaseReader(getContentResolver());
		}
		return databaseReader;
	}
	
	private DatabaseWriter getDatabaseWriter() {
		if(databaseWriter == null){
			databaseWriter = new DatabaseWriter(getContentResolver());
		}
		return databaseWriter;
	}
	
	public FireworkReader getFireworkReader() {
		if(fireworkReader == null){
			fireworkReader = new FireworkReader(getDatabaseReader());
		}
		return fireworkReader;
	}
	
	public FireworkWriter getFireworkWriter() {
		if(fireworkWriter == null){
			fireworkWriter = new FireworkWriter(getDatabaseWriter());
		}
		return fireworkWriter;
	}
}
