package dk.impact.imageprocessing.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import java.io.File;

public abstract class FeatureLoaderAbstract implements IFeatureLoader {
	Vector<Double>	results;
	boolean			fileLoaded;
	
	protected FeatureLoaderAbstract() {
		results = new Vector<Double>();
	}
	
	public final boolean load(String filename) {
		FileInputStream 	fis;		
		File				file = new File(filename);
		
		results.clear();		
		fileLoaded = false;
	
		try {
			fis = new FileInputStream(file);
			
			fileLoaded = doLoading(fis);

			fis.close();
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
		
		return fileLoaded;
	}
	
	abstract protected boolean doLoading(FileInputStream fis) throws IOException;	
	
	public Vector<Double> getResult() {
		return results;
	}	

	public String getSource() {
		return getClass().getSimpleName();
	}
}
