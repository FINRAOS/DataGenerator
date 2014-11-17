package org.hhat.servlet.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

// Simple dao to access a file, change to access a database if there is time
// Currently we'll just use ID as the filename
public class DataGenDao {
	
	public void store(String id) throws IOException
	{
		String path = new File("").getAbsolutePath();
		FileWriter newFile = new FileWriter(path + System.getProperty("file.separator") + id +".txt");
		newFile.write("DataGenStarted with id: " + id + "\n");
		newFile.flush();
		newFile.close();
	}
	
	public String getStatus(String id) throws IOException
	{
		String path = new File("").getAbsolutePath() + System.getProperty("file.separator") + id +".txt";
		RandomAccessFile raf = new RandomAccessFile(path, "r");
		String readLine = raf.readLine();
		while (readLine != null)
		{
			String nextLine = raf.readLine();
			if (nextLine == null)
			{
				raf.close();
				return readLine;
			}
			readLine = raf.readLine();
		}
		raf.close();
		// This will assume that the last line is the status
		return readLine;
	}
}
