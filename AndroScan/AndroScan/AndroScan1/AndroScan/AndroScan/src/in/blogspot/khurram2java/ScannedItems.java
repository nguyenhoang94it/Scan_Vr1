package in.blogspot.khurram2java;

import android.R.string;

public class ScannedItems {
	public int id;
	public String code, time, note;
	public ScannedItems(int id, String code, String time,
			String note) {
	this.id = id;
	this.code = code;
	this.note = note;
	this.time = time;
	
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}	

}
