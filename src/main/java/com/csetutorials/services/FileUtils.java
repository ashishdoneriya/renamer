package com.csetutorials.services;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

	public static void rename(File file, String newName) {
		if (file.getName().equals(newName)) {
			return;
		}
		String newPath = file.getParentFile().getAbsolutePath() + File.separator + newName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			System.out.println("Skipped " + file.getName() + ", Already exists : " + newName);
		} else {
			file.renameTo(newFile);
		}
	}

	public static String getExtension(String fileName) {
		String[] arr = fileName.split("\\.");
		if (arr.length < 2) {
			return "";
		}
		return arr[arr.length - 1].toLowerCase();
	}

	public static void moveFiles(String sourceDir) {
		File dir = new File(sourceDir);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-\\d{2}\\s\\d{2}\\.\\d{2}\\.\\d{2}\\.(png|jpg|jpeg|mp4|webp|gif|mkv)");
		for (File file : files) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				File newFile = new File(file.getParentFile().getAbsolutePath() + File.separator
						+ matcher.group(1) + File.separator + matcher.group(2) + File.separator + file.getName());
				if (newFile.exists()) {
					System.out.println("Already Exists : " + newFile.getAbsolutePath().replace(sourceDir, ""));
					continue;
				}
				newFile.getParentFile().mkdirs();
				file.renameTo(newFile);
			}
		}
	}

}
