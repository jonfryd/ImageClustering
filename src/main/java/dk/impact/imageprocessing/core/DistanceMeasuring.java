package dk.impact.imageprocessing.core;

public class DistanceMeasuring {
	public enum MethodId {
		ZERO,
		EUCLIDEAN,
		JSD
	}
	public static double byMethod(MethodId id, FeatureCollection featureCol, int sampleIdx1, int sampleIdx2, int featureGroupIdx) {
		double result = 0.0;
		
		switch(id) {
			case EUCLIDEAN:
				result = calcEuclideanDistance(featureCol, sampleIdx1, sampleIdx2, featureGroupIdx);
				break;
	
			case JSD:
				result = calcJSDDistance(featureCol, sampleIdx1, sampleIdx2, featureGroupIdx);
				break;
		}
		
		return result;
	}

	public static double calcJSDDistance(FeatureCollection featureCol, int sampleIdx1, int sampleIdx2, int featureGroupIdx) {
		// JSD distance metric
		double 	tmp1, tmp2, by_n;
		int		elements = featureCol.getNoFeatureElements(featureGroupIdx);
		double	result = 0.0;
		double 	vec1[] = featureCol.getRawFeatureGroup(sampleIdx1, featureGroupIdx);
		double 	vec2[] = featureCol.getRawFeatureGroup(sampleIdx2, featureGroupIdx);
		
		for (int i = 0; i < elements; i++) {
			double	n1 = vec1[i];
			double	n2 = vec2[i];

			if ((n1 == 0) && (n2 == 0)) continue;
			
			by_n = 2.0 / (n1 + n2);
			
	        if (n1 == 0) {
	            tmp2  = by_n * n2;
	            tmp2  = Math.log(tmp2);
	            tmp2 *= n2;
	            result += tmp2;
	        } else if (n2 == 0) {
                tmp1  = by_n * n1;
                tmp1  = Math.log(tmp1);
                tmp1 *= n1;
                result += tmp1;
	        } else {
	        	// interleave independent paths
                tmp2  = by_n * n2;
                tmp1  = by_n * n1;
                tmp2  = Math.log(tmp2);
                tmp1  = Math.log(tmp1);
                tmp2 *= n2;
                tmp1 *= n1;
                result += tmp1;
                result += tmp2;
	        }
		}
		
		return result * 0.5;
	}

	public static double calcEuclideanDistance(FeatureCollection featureCol, int sampleIdx1, int sampleIdx2, int featureGroupIdx) {
		double 	vec1[] = featureCol.getRawFeatureGroup(sampleIdx1, featureGroupIdx);
		double 	vec2[] = featureCol.getRawFeatureGroup(sampleIdx2, featureGroupIdx);
		int		elements = featureCol.getNoFeatureElements(featureGroupIdx);
		double	result = 0.0;
				
		for (int i = 0; i < elements; i++) {
			double	n1 = vec1[i];
			double	n2 = vec2[i];
			double	dn = (n2 - n1);
			
			result += dn * dn;
		}

		return Math.sqrt(result);
	}	
}
