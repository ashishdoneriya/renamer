package com.csetutorials.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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

	public static void moveWhatsappFiles(String sourceDir) {
		File dir = new File(sourceDir);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		Pattern pattern = Pattern.compile("(?:IMG|VID)-(\\d{4})(\\d{2})\\d{2}-WA\\d{4}\\.(png|jpg|jpeg|mp4|webp|gif|mkv)");
		for (File file : files) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				String parentPath = file.getParentFile().getAbsolutePath() + File.separator
						+ matcher.group(1) + File.separator + matcher.group(2);
				(new File(parentPath)).mkdirs();
				File newFile = new File(parentPath + File.separator + file.getName());
				if (newFile.exists()) {
					String extension = matcher.group(3);
					for (int i = 1; i < 1_00_000; i++) {
						newFile = new File(parentPath + File.separator
								+ file.getName().replace("." + extension, "-" + i + "." + extension));
						if (!newFile.exists()) {
							file.renameTo(newFile);
							break;
						}
					}
				} else {
					file.renameTo(newFile);
				}
			}
		}
	}

	public static void mergeDirectories(String sourceDirPath, String mergeDirPath) {
		File mergeDir = new File(mergeDirPath);
		mergeDirPath = mergeDir.getAbsolutePath();
		File sourceDir = new File(sourceDirPath);
		Stack<File> stack = new Stack<>();
		stack.push(mergeDir);
		while (!stack.isEmpty()) {
			File obj = stack.pop();
			if (obj.isDirectory()) {
				File[] files = obj.listFiles();
				if (files != null) {
					for (File file : files) {
						stack.push(file);
					}
				}
			} else if (obj.isFile()) {
				move(obj, sourceDir, mergeDir);
			}
		}

	}

	private static void move(File file, File sourceDir, File mergeDir) {
		List<Pattern> patternList = new ArrayList<>();
		patternList.add(Pattern.compile("(IMG-\\d{8}-WA\\d{4})(?:-\\d+)?\\.(jpg|jpeg|png|webp|mp4|mkv)"));
		patternList.add(Pattern.compile("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}\\.\\d{2}\\.\\d{2})\\.(jpg|jpeg|png|webp|mp4|mkv)"));
		File newFile = new File(file.getAbsolutePath().replace(mergeDir.getAbsolutePath(), sourceDir.getAbsolutePath()));
		if (!newFile.exists()) {
			try {
				Files.move(file.toPath(), newFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String newParentPath = newFile.getParentFile().getAbsolutePath();
		for (Pattern pattern : patternList) {
			Matcher matcher = pattern.matcher(file.getName());
			if (matcher.find()) {
				String prefix = matcher.group(1);
				String ext = matcher.group(2);
				for (int i = 1; i < 1_00_000; i++) {
					String newFileName = prefix + "-" + i + "." + ext;
					newFile = new File(newParentPath, newFileName);
					if (!newFile.exists()) {
						try {
							Files.move(file.toPath(), newFile.toPath());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						if (file.getParentFile().listFiles() == null || file.getParentFile().listFiles().length == 0) {
							file.getParentFile().delete();
						}
						return;
					}
				}
			}
		}


	}
}
