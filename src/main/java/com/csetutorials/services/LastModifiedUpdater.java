package com.csetutorials.services;

import java.io.File;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastModifiedUpdater {

	public static void updateLastModified(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})\\s(\\d{2})\\.(\\d{2}).(\\d{2})\\.[A-Za-z]{2,4}\\d?");
		for (File file : files) {
			String name = file.getName();
			Matcher matcher = pattern.matcher(name);
			if (matcher.find()) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
				calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)));
				calendar.set(Calendar.DATE, Integer.parseInt(matcher.group(3)));
				calendar.set(Calendar.HOUR, Integer.parseInt(matcher.group(4)));
				calendar.set(Calendar.MINUTE, Integer.parseInt(matcher.group(5)));
				calendar.set(Calendar.SECOND, Integer.parseInt(matcher.group(6)));
				calendar.set(Calendar.MILLISECOND, 0);
				file.setLastModified(calendar.getTimeInMillis());
			}
		}
	}

}
