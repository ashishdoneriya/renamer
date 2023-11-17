package com.csetutorials;

import com.csetutorials.beans.Arguments;
import com.csetutorials.services.*;

public class Main {

	public static void main(String[] args) {
		Arguments arguments = ArgumentsParser.parseArguments(args);
		if (arguments.renameImages) {
			if (arguments.useLastModified) {
				MediaRenamer.renameUsingLastModified(arguments);
			} else if (arguments.useFileName) {
				MediaRenamer.renameUsingFileName(arguments);
			} else if (arguments.useMeta) {
				MediaRenamer.renameUsingImageProperties(arguments);
			}
		}
		if (arguments.renameVideos) {
			if (arguments.useLastModified) {
				MediaRenamer.renameUsingLastModified(arguments);
			} else if (arguments.useFileName) {
				MediaRenamer.renameUsingFileName(arguments);
			}
		}
		if (arguments.updateLastModified) {
			LastModifiedUpdater.updateLastModified(arguments.sourceDir);
		}
		if (arguments.moveFiles) {
			FileUtils.moveFiles(arguments.sourceDir);
			FileUtils.moveWhatsappFiles(arguments.sourceDir);
		}
		if (arguments.removeDuplicates) {
			DuplicateFilesRemover.removeDuplicates(arguments.sourceDir);
		}
		if (arguments.merge) {
			if (arguments.removeDuplicates) {
				DuplicateFilesRemover.removeDuplicates(arguments.sourceDir, arguments.mergeDir);
			}
			FileUtils.mergeDirectories(arguments.sourceDir, arguments.mergeDir);
		}
	}
}