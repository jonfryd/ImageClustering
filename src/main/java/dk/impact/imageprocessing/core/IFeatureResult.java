package dk.impact.imageprocessing.core;

import java.util.Vector;

public interface IFeatureResult {
	// Source - textual form - could be class name
	String getSource();
	
	// Get feature vector - can be null
	Vector<Double> getResult();
	
	// Get preferred feature normalization method
	FeatureNormalization.MethodId getFeatureNormalizationMethodId();
	
	// Get preferred distance measure
	DistanceMeasuring.MethodId getDistanceMeasureId();
}
