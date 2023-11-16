package com.csetutorials.services;

import com.csetutorials.beans.Arguments;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaRenamer {


	private static final SimpleDateFormat finalSdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

	public static void renameUsingLastModified(Arguments arguments) {
		File dir = new File(arguments.sourceDir);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}

		for (File file : files) {
			String extension = FileUtils.getExtension(file.getName());
			if (isNotMedia(extension)) {
				continue;
			}
			String newName = finalSdf.format(new Date(file.lastModified())) + "." + extension;
			FileUtils.rename(file, newName);
		}
	}

	public static void renameUsingFileName(Arguments arguments) {
		File dir = new File(arguments.sourceDir);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		Pattern[] mediaPatterns = {
				Pattern.compile("Screenshot_(\\d{4})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2})-(\\d{2}).+\\.(jpg|jpeg|webp|png|avif|gif)" ),
				Pattern.compile("IMG_(\\d{4})(\\d{2})(\\d{2})_(\\d{2})(\\d{2})(\\d{2})\\.(jpg|jpeg|webp|png|avif|gif)",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("IMG_(\\d{4})(\\d{2})(\\d{2})_(\\d{2})(\\d{2})(\\d{2})~\\d\\.(jpg|jpeg|webp|png|avif|gif)",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("VID_(\\d{4})(\\d{2})(\\d{2})_(\\d{2})(\\d{2})(\\d{2})\\.(mp4|MP4)"),
				Pattern.compile("VID_(\\d{4})(\\d{2})(\\d{2})_(\\d{2})(\\d{2})(\\d{2})_HSR_\\d{3}\\.(mp4|MP4)")
		};
		for (File file : files) {
			String extension = FileUtils.getExtension(file.getName());
			if (isNotMedia(extension)) {
				continue;
			}
			for (Pattern pattern : mediaPatterns) {
				Matcher matcher = pattern.matcher(file.getName());
				if (matcher.find()) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
					calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)));
					calendar.set(Calendar.DATE, Integer.parseInt(matcher.group(3)));
					calendar.set(Calendar.HOUR, Integer.parseInt(matcher.group(4)));
					calendar.set(Calendar.MINUTE, Integer.parseInt(matcher.group(5)));
					calendar.set(Calendar.SECOND, Integer.parseInt(matcher.group(6)));
					calendar.set(Calendar.MILLISECOND, 0);
					String newName = finalSdf.format(new Date(calendar.getTimeInMillis())) + "." + extension;
					FileUtils.rename(file, newName);
					break;
				}
			}
		}
	}

	public static void renameUsingImageProperties(Arguments arguments) {
		File dir = new File(arguments.sourceDir);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			String extension = FileUtils.getExtension(file.getName());
			if (isNotMedia(extension)) {
				continue;
			}
			Date metadataDate = extractMetadata(file);
			if (metadataDate != null) {
				String newName = finalSdf.format(metadataDate) + "." + extension;
				FileUtils.rename(file, newName);
			}
		}
	}

	private static Date extractMetadata(File file) {
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(file);
		} catch (Exception e) {
			System.out.println("Problem while reading metadata : " + file.getName());
			return null;
		}
		SimpleDateFormat imageMetaDataSdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		for (Directory directory: metadata.getDirectories()) {
			for (Tag tag: directory.getTags()) {
				if ("Date/Time".equals(tag.getTagName())) {
					try {
						return imageMetaDataSdf.parse(tag.getDescription());
					} catch (ParseException e) {
						System.out.println("Paring Error : " + file.getName());
						return null;
					}
				}
			}
		}

		return null;
	}


	public static boolean isNotMedia(String extension) {
		if (extension == null) {
			return true;
		}
		switch (extension) {
			case "jpeg":
			case "jpg":
			case "webp":
			case "png":
			case "gif":
			case "avif":
			case "mp4":
			case "mkv":
				return false;
			default:
				return true;
		}
	}

}
