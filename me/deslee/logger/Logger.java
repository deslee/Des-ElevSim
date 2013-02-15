package me.deslee.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.deslee.logger.impl.ConsoleLogger;
import me.deslee.logger.impl.LoggerImpl;

public class Logger {
	private int num = 0;
	private final SimpleDateFormat fmt = new SimpleDateFormat("[k:m:s.S]");
	
	private LoggerImpl implementation;
	
	public Logger(int type) {
		if (type == 0) {
			implementation = new ConsoleLogger();
		}
	}
	
	public static final int CONSOLE = 0;
	
	private String parseString(String s) {
		return ++num + fmt.format(new Date()) + " " + s;
	}
	
	public void log(Object obj, String s, int priority) {
		implementation.log(parseString("<"+obj.toString()+">" + ": " + s));
	}
}
