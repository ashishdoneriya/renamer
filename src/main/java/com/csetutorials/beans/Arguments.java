package com.csetutorials.beans;


import java.io.File;

public class Arguments {

	public String sourceDir = (new File("")).getAbsolutePath();

	public boolean renameImages;
	public boolean renameVideos;
	public boolean useFileName;
	public boolean useLastModified;
	public boolean useImageProperties;
	public boolean moveFiles;
	public boolean updateLastModified;
	public boolean removeDuplicates;

}
