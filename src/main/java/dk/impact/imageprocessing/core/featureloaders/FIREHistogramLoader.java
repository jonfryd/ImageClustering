package dk.impact.imageprocessing.core.featureloaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureLoaderAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class FIREHistogramLoader extends FeatureLoaderAbstract {
	private static final String 	FIREHistogramHeader    = "FIRE_histogram"; 
	private static final String 	FIREHistogramDataToken = "data"; 

	@Override
	protected boolean doLoading(FileInputStream fis) throws IOException  {
		GZIPInputStream		gzipStream = new GZIPInputStream(fis);
		BufferedReader		br = new BufferedReader(new InputStreamReader(gzipStream));
		
		/* Example:
		 * 
		 * FIRE_histogram
		 * # Histogram file saved for Fire V2
		 * dim 1
		 * counter 1300
		 * steps 1886
		 * min 0
		 * max 1886
		 * data 0 0 0 0 2 2 1 0 0 0 ..... 
		 */
		String marker = br.readLine();
		
		if (!marker.equals(FIREHistogramHeader)) {
			System.out.println("Incorrect FIRE histogram file header.");
			return false;
		} 
		
//		String descriptionLine = br.readLine();
//		String dimLine = br.readLine();
//		String counterLine = br.readLine();
//		String stepsLine = br.readLine();
//		String minLine = br.readLine();
//		String maxLine = br.readLine();
		for (int i = 0; i < 6; i++) {
			br.readLine();
		}
		
		String dataLine = br.readLine();

		StringTokenizer st = new StringTokenizer(dataLine, " ");
		String dataToken = st.nextToken();
		
		if (!dataToken.equals(FIREHistogramDataToken)) {
			System.out.println("Data marker expected but not found.");
			return false;			
		}
		
		while(st.hasMoreTokens()) {
			String	value = st.nextToken();
			
			getResult().add(Double.parseDouble(value));
		}
		
		return true;
	}

	public String getName() {
		return "FIREHistogramLoader";
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.JSD;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.NONE;
	}
}
