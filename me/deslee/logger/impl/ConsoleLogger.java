package me.deslee.logger.impl;

public class ConsoleLogger extends LoggerImpl {


	@Override
	public void log(String s) {
		System.out.println(s);
	}

}
