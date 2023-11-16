package com.csetutorials.services;

import com.csetutorials.beans.Arguments;

public class ArgumentsParser {

	public static Arguments parseArguments(String[] args) {
		Arguments arguments = new Arguments();
		for (String arg : args) {
			switch (arg) {
				case "--rename-images":
					arguments.renameImages = true;
					break;
				case "--rename-videos":
					arguments.renameVideos = true;
					break;
				case "--move-files":
					arguments.moveFiles = true;
					break;
				case "--update-last-modified":
					arguments.updateLastModified = true;
					break;
				case "--remove-duplicates":
					arguments.removeDuplicates = true;
					break;
				case "--use-meta":
					arguments.useMeta = true;
					break;
			}
		}

		if (!arguments.renameImages && !arguments.renameVideos && !arguments.moveFiles && !arguments.updateLastModified) {
			System.out.println(
					"Kindly use at least one argument from below" +
							"\n\t--rename-videos" +
							"\n\t--rename-images" +
							"\n\t--move-files" +
							"\n\t--update-last-modified" +
							"\n\t --remove-duplicates" +
							"\nUse --help for more options.");
			System.exit(0);
		}

		if (arguments.renameVideos) {
			if (arguments.useLastModified && arguments.useFileName) {
				System.out.println("Kindly use exactly one from --use-last-modified or --use-file-name along with --rename-videos");
				System.exit(0);
			}

			if (!arguments.useLastModified && !arguments.useFileName) {
				System.out.println("Kindly use --use-last-modified or --use-file-name along with --rename-videos");
				System.exit(0);
			}
		}

		if (arguments.renameImages) {
			int count = 0;
			count += arguments.useLastModified ? 1 : 0;
			count += arguments.useMeta ? 1 : 0;
			count += arguments.useFileName ? 1 : 0;
			if (count == 0) {
				System.out.println("Kindly use --use-meta or --use-last-modified or --use-file-name along with --rename-images");
				System.exit(0);
			}
			if (count > 1) {
				System.out.println("Kindly use exactly one from --use-meta or --use-last-modified or --use-file-name along with --rename-images");
				System.exit(0);
			}
		}

		return arguments;
	}

}
