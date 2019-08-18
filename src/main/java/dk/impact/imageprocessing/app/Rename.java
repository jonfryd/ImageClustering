package dk.impact.imageprocessing.app;

import java.io.*;
import java.text.DecimalFormat;

public class Rename {
    public static void main(String args[]) {
    	try {
			String baseDir = null;
			if (args != null && args.length > 0) {
				baseDir = args[0];
			}    		
    		
    		File rename_log = new File(baseDir, "rename_log.txt");

    		if (rename_log.exists()) {
    			System.out.println("Rename log exists. Rename back (or delete rename_log.txt - be careful).");
    			return;
    		}

    		DecimalFormat df = new DecimalFormat("000");
    		BufferedReader in_out = new BufferedReader(new FileReader(new File(baseDir, "output.txt")));	    
    		BufferedReader in_files = new BufferedReader(new FileReader(new File(baseDir, "img_names.txt")));
    		PrintWriter log = new PrintWriter (new BufferedWriter (new FileWriter (rename_log, true)));
    		while(true) {
    			String out = in_out.readLine();
    			String in_file = in_files.readLine();

    			if (out == null || in_file == null) {
    				break;
    			}

    			String new_filename = "C" + df.format(Integer.parseInt(out)) + "_" + in_file;

    			File old = new File(baseDir, in_file);
    			boolean result = old.renameTo(new File(baseDir, new_filename));

    			System.out.println(result + " " + out + " " + in_file + " -> " + new_filename);

    			log.println(in_file);
    			log.println(new_filename);
    		}
    		log.flush();
    		log.close();
    		in_out.close ();
    		in_files.close();
    	}
    	catch (IOException e) {
    	}
    }
}
