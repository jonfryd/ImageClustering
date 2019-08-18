package dk.impact.imageprocessing.app;

import java.io.*;

public class RenameBack {
    public static void main(String args[]) {
    	try {
			String baseDir = null;
			if (args != null && args.length > 0) {
				baseDir = args[0];
			}  
			
			File rename_log = new File(baseDir, "rename_log.txt");

    		if (!rename_log.exists()) {
    			System.out.println("Rename log doesn't exists. Can't rename back.");
    			return;
    		}

    		BufferedReader log = new BufferedReader(new FileReader(rename_log));

    		while(true) {
    			String in_file = log.readLine();
    			String new_filename = log.readLine();

    			if (new_filename == null || in_file == null) {
    				break;
    			}

    			System.out.println(new_filename + " -> " + in_file);

    			File old = new File(baseDir, new_filename);
    			old.renameTo(new File(baseDir, in_file));
    		}

    		log.close();
    		rename_log.delete();
    	}
    	catch (IOException e) {
    	}
    }
}
