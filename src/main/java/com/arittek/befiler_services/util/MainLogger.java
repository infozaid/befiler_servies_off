package com.arittek.befiler_services.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

public class MainLogger {
	private static Logger log = null;
	private static MainLogger instance;
	public static void main(String args[]){
		MainLogger.getInstance().getLogger().info("");
	}
	private MainLogger() {

	}

	synchronized public static MainLogger getInstance() {
		if (instance == null) {
			instance = new MainLogger();
		}
		return instance;
	}

	public void configureLogger(String fileName) {

		PatternLayout pocLayout = new PatternLayout();

		try {
			// creating error file appender
			RollingFileAppender pocAppender = new RollingFileAppender(pocLayout, fileName);
			pocAppender.setMaxFileSize("200MB");
			pocAppender.setMaxBackupIndex(50000);
			pocAppender.activateOptions();
			
			Logger rootLogger = Logger.getLogger("HTTPLogger");

			rootLogger.setLevel(Level.INFO);
			rootLogger.addAppender(pocAppender);
			
			// remove console layout not using anymore
			// rootLogger.addAppender(new ConsoleAppender(pocLayout));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Logger getLogger() {
		if (log == null) {
			getInstance();
			log = Logger.getLogger("HTTPLogger");
			MainLogger.getInstance().configureLogger("C:\\Befiler HttpRequests Logs\\HttpRequests.txt");
		}
		return log;
	}
}