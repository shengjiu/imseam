package com.imseam.common.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextFileReaderUtil {
	
	static public String readTextFile(String path) throws IOException{
	    //...checks on aFile are elided
	    StringBuffer contents = new StringBuffer();

	    //declared here only to make visible to finally clause
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(path) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      while (( line = input.readLine()) != null){
	        contents.append(line);
	        contents.append(System.getProperty("line.separator"));
	      }
	    }
	    finally {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	    }
	    return contents.toString();
	  }

}
