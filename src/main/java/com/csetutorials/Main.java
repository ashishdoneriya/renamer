package com.csetutorials;

import com.csetutorials.beans.Arguments;
import com.csetutorials.services.ArgumentsParser;
import com.csetutorials.services.DuplicateFilesRemover;
import com.csetutorials.services.LastModifiedUpdater;
import com.csetutorials.services.MediaRenamer;

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
			DuplicateFilesRemover.removeDuplicates(arguments.sourceDir);
		}
	}
}