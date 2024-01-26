package com.csetutorials.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
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
		Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-\\d{2}\\s\\d{2}\\.\\d{2}\\.\\d{2}\\.(avi|png|mov|3gp|jpg|jpeg|mp4|webp|gif|mkv)");
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
		Pattern pattern = Pattern.compile("(?:IMG|VID)-(\\d{4})(\\d{2})\\d{2}-WA\\d{4}\\.(3gp|enc|png|jpg|jpeg|mp4|webp|gif|mkv)");
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
		patternList.add(Pattern.compile("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}\\.\\d{2}\\.\\d{2})\\.(jpg|jpeg|png|webp|mp4|mkv|avi|3gp|mov)"));
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

	public static void renameAndMove(String sourceDir, String targetDir) {
		Stack<File> stack = new Stack<>();
		stack.push(new File(sourceDir));
		SimpleDateFormat finalSdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		while (!stack.isEmpty()) {
			File obj = stack.pop();
			if (obj.isDirectory()) {
				File[] files = obj.listFiles();
				if (files == null) {
					continue;
				}
				Arrays.sort(files, Comparator.comparing(File::getName));
				for (File file: files) {
					stack.push(file);
				}
			} else if (obj.isFile()) {
				String extension = FileUtils.getExtension(obj.getName());
				if (MediaRenamer.isNotMedia(extension)) {
					continue;
				}
				Date date = MediaRenamer.extractMetadata(obj);
				if (date == null) {
					date = new Date(obj.lastModified());
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int year = calendar.get(Calendar.YEAR);
				if (year < 2004) {
					date = new Date(obj.lastModified());
					calendar.setTime(date);
				}
				year = calendar.get(Calendar.YEAR);
				String sYear = String.valueOf(year);
				int month = calendar.get(Calendar.MONTH) + 1;
				String sMonth = String.valueOf(month);
				if (month < 10) {
					sMonth = "0" + month;
				}
				String newName = finalSdf.format(date) + "." + extension;
				String newParentDir = targetDir + File.separator + sYear + File.separator
						+ sMonth + File.separator;
				File newParentFile = new File(newParentDir);
				newParentFile.mkdirs();
				String newObjPath = newParentDir + newName;
				File newFile = new File(newObjPath);
				if (!newFile.exists()) {
					move(obj.toPath(), newFile.toPath());
				} else {
					try {
						String hash11 = DuplicateFilesRemover.getHash1(obj);
						String hash12 = DuplicateFilesRemover.getHash2(obj);
						String hash21 = DuplicateFilesRemover.getHash1(newFile);
						String hash22 = DuplicateFilesRemover.getHash2(newFile);
						if (hash11.equals(hash21) && hash12.equals(hash22)) {
							System.out.println("Deleting - " + obj.getName());
							Files.delete(obj.toPath());
						} else {
							for (int i = 1; i < 1_00_000; i++) {
								newName = finalSdf.format(date) + "-" + i + "." + extension;
								newFile = new File(newParentDir + newName);
								if (!newFile.exists()) {
									move(obj.toPath(), newFile.toPath());
									break;
								}
							}
						}
					} catch (NoSuchAlgorithmException | IOException e) {
						System.out.println("Couldn't check hash of file - " + obj.getAbsolutePath());
					}
				}
			}
		}
	}

	public static void renameAndMoveWhatsApp(String sourceDir, String targetDir) {
		Stack<File> stack = new Stack<>();
		stack.push(new File(sourceDir));
		Pattern[] patterns = {
				Pattern.compile("IMG-(\\d{4})(\\d{2})(\\d{2})-WA0?(\\d{4})\\.(jpg|jpeg|mp4)"),
				Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})\\d(\\d{2})\\.\\d{2}.\\d{2}\\.(jpg|jpeg|mp4)"),
				Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})\\sWA0?(\\d{4})\\.(jpg|jpeg|mp4)"),
				Pattern.compile("VID\\s(\\d{4})-(\\d{2})-(\\d{2})\\sWA0?(\\d{4})\\.(jpg|jpeg|mp4)"),
				Pattern.compile("VID-(\\d{4})(\\d{2})(\\d{2})(?:\\s|-)WA0?(\\d{4})\\.(jpg|jpeg|mp4)")
		};
		while (!stack.isEmpty()) {
			File obj = stack.pop();
			if (obj.isDirectory()) {
				File[] files = obj.listFiles();
				if (files == null) {
					continue;
				}
				Arrays.sort(files, Comparator.comparing(File::getName));
				for (File file: files) {
					stack.push(file);
				}
			} else if (obj.isFile()) {
				String extension = FileUtils.getExtension(obj.getName());
				if (MediaRenamer.isNotMedia(extension)) {
					continue;
				}
				for (Pattern pattern: patterns) {
					Matcher matcher = pattern.matcher(obj.getName());
					if (matcher.find()) {
						String year = matcher.group(1);
						String month = matcher.group(2);
						String date = matcher.group(3);
						String sNumber = matcher.group(4);
						String ext = matcher.group(5);
						String newParentDir = targetDir + File.separator + year + File.separator
								+ month + File.separator;
						(new File(newParentDir)).mkdirs();
						String newName = year + "-" + month + "-" + date + " WA" + sNumber + "." + ext;
						String newObjPath = newParentDir + newName;
						File newFile = new File(newObjPath);
						if (!newFile.exists()) {
							move(obj.toPath(), newFile.toPath());
						} else {
							for (int i = 1; i < 1_000; i++) {
								newName = year + "-" + month + "-" + date + " WA" +  String.format("%04d", i) + "." + ext;
								newObjPath = newParentDir + newName;
								newFile = new File(newObjPath);
								if (!newFile.exists()) {
									move(obj.toPath(), newFile.toPath());
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
	}


	private static void move(Path source, Path target) {
		try {
			Files.move(source, target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (source.toFile().getParentFile().listFiles() == null
				|| source.toFile().getParentFile().listFiles().length == 0) {
			try {
				Files.delete(source.toFile().getParentFile().toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void clean(File obj) {
		if (obj.isDirectory()) {
			File[] files = obj.listFiles();
			if (files == null || files.length == 0) {
				obj.delete();
			} else {
				for (File file: files) {
					clean(file);
				}
				if (obj.listFiles() == null || obj.listFiles().length == 0) {
					try {
						Files.delete(obj.toPath());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
