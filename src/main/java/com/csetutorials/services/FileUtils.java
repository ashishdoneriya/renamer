package com.csetutorials.services;

import java.io.File;

public class FileUtils {

	public static void rename(File file, String newName) {
		String newPath = file.getParentFile().getAbsolutePath() +File.separator + newName;
		File newFile = new File(newPath);
		if (newFile.exists()) {
			System.out.println("Skipped " + file.getName());
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

}