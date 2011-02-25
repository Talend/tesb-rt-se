package org.sopera.monitoring.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {
	private static final MessageFormat MESSAGE_FORMAT = new MessageFormat(
			"[{1}|{2}|{3,date,hh:mm:ss}] {0}: {4} \n");

	public CustomLogFormatter() {
		super();
	}

	@Override
	public String format(final LogRecord record) {
		Object[] arguments = new Object[6];
		arguments[0] = record.getLoggerName();
		arguments[1] = record.getLevel();
		arguments[2] = Thread.currentThread().getName();
		arguments[3] = new Date(record.getMillis());
		arguments[4] = record.getMessage();
		return MESSAGE_FORMAT.format(arguments);
	}

}
