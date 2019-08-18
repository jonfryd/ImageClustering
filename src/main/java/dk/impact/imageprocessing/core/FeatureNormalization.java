package dk.impact.imageprocessing.core;

import java.util.TreeMap;
import java.util.Vector;

public class FeatureNormalization {
	public enum MethodId {
		NONE,
		DIVIDE_BY_255,
		LINEAR,
		RANK_NORMALIZATION
	}

	public static void byMethod(MethodId id, FeatureCollection featureCol, int featureGroup, int element) {
		switch(id) {
			case DIVIDE_BY_255:
				break;

			case LINEAR:
				linearNormalization(featureCol, featureGroup, element);
				break;
	
			case RANK_NORMALIZATION:
				rankNormalization(featureCol, featureGroup, element);
				break;
		}		
	}

	public static void div255Normalization(FeatureCollection featureCol, int featureGroup, int element) {
	    // normalize features (divide by 256)		    
		// scale features
		for (int j = 0; j < featureCol.getNoSamples(); j++) {
			double newValue = featureCol.getFeatureElement(j, featureGroup, element) / 255.0;
			
			featureCol.setFeatureElement(j, featureGroup, element, newValue);
		}		    
	}
	
	
	public static void rankNormalization(FeatureCollection featureCol, int featureGroup, int element) {
	    // normalize features via 'rank normalization'    
		TreeMap<Double,Vector<Integer>>	map = new TreeMap<Double,Vector<Integer>>();

		// order values			
		for (int j = 0; j < featureCol.getNoSamples(); j++) {
			double val = featureCol.getFeatureElement(j, featureGroup, element);
			
			Vector<Integer> list = map.get(val);
			
			if (list == null) {
				list = new Vector<Integer>();						
				map.put(val, list);
			}
			
			list.add(j);
		}		    

		int j = 0;
		
		for (Vector<Integer> list : map.values()) {
			int	lastIdx = j + list.size();
			
			// calc average rank
			double first = (double) j / (double) (featureCol.getNoSamples()  - 1);
			double last = (double) (lastIdx - 1) / (double) (featureCol.getNoSamples()  - 1);
							
			double averageRank = (first + last) * 0.5;
			
			// assign rank						
			for (int k = 0; k < list.size(); k++) {
				int keyIndex = list.get(k);

				featureCol.setFeatureElement(keyIndex, featureGroup, element, averageRank);
			}
				
			j = lastIdx;	
		}
	}

	public static void linearNormalization(FeatureCollection featureCol, int featureGroup, int element) {
	    // normalize features (linear scaling)		    
		double featMin = Double.POSITIVE_INFINITY;
		double featMax = Double.NEGATIVE_INFINITY;
		
		// find min and max				
		for (int j = 0; j < featureCol.getNoSamples(); j++) {
			double val = featureCol.getFeatureElement(j, featureGroup, element);
			
			featMin = Math.min(featMin, val);
			featMax = Math.max(featMax, val);
		}		    

		// scale features
		if (featMax > featMin) {
			for (int j = 0; j < featureCol.getNoSamples(); j++) {
				double newValue = (featureCol.getFeatureElement(j, featureGroup, element) - featMin) / (featMax - featMin);
				
				featureCol.setFeatureElement(j, featureGroup, element, newValue);
			}		    
		} else {
			for (int j = 0; j < featureCol.getNoSamples(); j++) {
				featureCol.setFeatureElement(j, featureGroup, element, 0.0);
			}					
		}
	}
	
	
}
