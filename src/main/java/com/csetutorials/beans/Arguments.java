package com.csetutorials.beans;


import java.io.File;
import java.util.List;

public class Arguments {

	public String sourceDir = (new File("")).getAbsolutePath();

	public boolean renameImages;
	public boolean renameVideos;
	public boolean useFileName;
	public boolean useLastModified;
	public boolean useMeta;
	public boolean moveFiles;
	public boolean updateLastModified;
	public boolean removeDuplicates;
	public boolean merge;
	public String mergeDir;
	public boolean renameAndMove;
	public boolean renameAndMoveWhatsApp;
	public boolean cleanEmptyDirs;

}
