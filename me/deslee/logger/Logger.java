package me.deslee.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

public class Logger {
	private int num = 0;
	private final SimpleDateFormat fmt = new SimpleDateFormat("[k:m:s.S]");
	
	public final JTextArea medium = new JTextArea();
	public final JTextArea low = new JTextArea();
	public final JTextArea high = new JTextArea();
	
	public Logger() {
		low.append("Low priority text:\n");
		medium.append("Default priority text:\n");
		high.append("High priority text:\n");
	}
	
	private String parseString(String s) {
		return num + fmt.format(new Date()) + " " + s;
	}
	
	private void put1(String s) {
		high.append(s+"\n");
		high.setCaretPosition(high.getDocument().getLength());
	}
	
	private void put2(String s) {
		medium.append(s+"\n");
		medium.setCaretPosition(medium.getDocument().getLength());
	}
	

	private void put3(String s) {
		System.out.println(s);
		low.append(s+"\n");
		low.setCaretPosition(low.getDocument().getLength());
	}
	
	public void log(Object obj, String s, int priority) {
		switch(priority) {
		case 1:
			put1(parseString("<"+obj.toString()+">" + ": " + s));
		case 2:
			put2(parseString("<"+obj.toString()+">" + ": " + s));
		case 3:
			put3(parseString("<"+obj.toString()+">" + ": " + s));
		break;
		}
		num++;
	}

	public void log(Object obj, String s) {
		log(obj, s, 2);
	}
	
	public void log(String s, int priority) {
		switch(priority) {
		case 1:
			put1(parseString(s));
		case 2:
			put2(parseString(s));
		case 3:
			put3(parseString(s));
		break;
		}
		num++;
	}
	
	public void log(String s) {
		log(s, 2);
	}
}
