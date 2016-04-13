package edu.stanford.slac.pinger.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import edu.stanford.slac.pinger.general.C;
import edu.stanford.slac.pinger.general.Logger;
import edu.stanford.slac.pinger.general.utils.OutputFileHandler;

public class AllMetricsPerDayReducer {

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"1",
					"C:/Users/Renan/Documents/transformed/", //output dir
					"2010", //year					
			};
		}		
		try {
			int debugLevel = Integer.parseInt(args[0]);
			Logger.setDebugLevel(debugLevel);
			long t1 = System.currentTimeMillis();
			start(args);
			long t2 = System.currentTimeMillis();
			Logger.log("Finally done! It took " + ((t2-t1)/1000.0) + " seconds.");
		} catch (Exception e) {
			Logger.log("start", e, "errors");
		}
	}

	public synchronized static void start(String[] args) {
		String transformedFilesDirectory = args[1];
		String year = args[2];

		boolean saveFileSize = true;

		if (saveFileSize) {
			C.filesSizeLog = transformedFilesDirectory + "file_sizes.csv";
		}

		String outputFileName = year+".csv";
		String namePattern = year+"-[0-9]{2}-[0-9]{2}.csv";
		
		File outputFile = new File(transformedFilesDirectory+outputFileName);
		try {
			outputFile.createNewFile();
			File dir = new File(transformedFilesDirectory);
			File files[] = dir.listFiles();
			Arrays.sort(files);
			boolean started = false;
			for (File f : files) {
				String fName = f.getName();
				if (fName.matches(namePattern)) {
					started = true;
					System.out.println(f.getName());

					FileInputStream fis = new FileInputStream(f);
					FileOutputStream fos = new FileOutputStream(outputFile, true);

					fos.flush();
					fos.getFD().sync();
					IOUtils.copy(fis, fos);
					fos.flush();
					fos.getFD().sync();
					
					IOUtils.closeQuietly(fis);
					IOUtils.closeQuietly(fos);
				} else if (started) {
					break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}


		OutputFileHandler erelation = new OutputFileHandler("./", "ERelation.txt");
		erelation.addRow("YEAR;OUTPUT_FILE\n"+year+";"+outputFile.getAbsolutePath()+"\n");
		erelation.writeContentAndClean();
		
	}



}
