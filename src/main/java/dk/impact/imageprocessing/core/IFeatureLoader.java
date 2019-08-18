package dk.impact.imageprocessing.core;

public interface IFeatureLoader extends IFeatureResult {
	// Load feature file, returns true if file was loaded successfully
	boolean load(String filename);
	
	// Return textual description
	String getName();
}
