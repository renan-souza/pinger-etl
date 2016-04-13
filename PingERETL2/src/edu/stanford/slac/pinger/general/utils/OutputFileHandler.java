package edu.stanford.slac.pinger.general.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.Logger;

public class OutputFileHandler  {

	private StringBuffer fileContent; //StringBuffer is synchronized
	
	private String outputDir;
	private String filePath;
	private static int CUT_HEAP_MEMORY = (int) (Runtime.getRuntime().freeMemory()/C.CUT_HEAP_COEF); //divided by 2 is the standard.. divided by 10 generates lots of files of ~ 13 MB;
	
	public OutputFileHandler(String outputDir, String fileName) {
		this.outputDir = outputDir;
		this.filePath = outputDir+fileName;
		start();
	}
	
	public void addRow(String row) {
		checkHeap();
		fileContent.append(row);
	}

	private synchronized void checkHeap() {
		if (fileContent.length() >= CUT_HEAP_MEMORY) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
				out.write(fileContent.toString());
				out.close();
				fileContent = new StringBuffer();
			} catch (Exception e) {
				Logger.error(Utils.getCurrentMethodName(), e);
			}
		}
	}
	
	public void writeContentAndClean() {
		try {
			if (fileContent==null || fileContent.toString().equals(""))
				return;
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
			out.write(fileContent.toString());
			out.close();
			fileContent = new StringBuffer();
		} catch (Exception e) {
			Logger.error(Utils.getCurrentMethodName(), e);
		}
	}
	

	public void start() {
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(filePath);
		if (f.exists()) f.delete();
		fileContent = new StringBuffer();
	}
	
	public StringBuffer getFileContet() {
		return this.fileContent;
	}
	
	public String getAbsoluteFilePath() {
		try {
			File f = new File(filePath);
			return f.getAbsolutePath();
 		} catch (Exception e) {
 			Logger.error(e);
 			return filePath;
 		}
	}
}
