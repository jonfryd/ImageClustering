package dk.impact.imageprocessing.app;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureCollection;
import dk.impact.imageprocessing.core.FeatureNormalization;
import dk.impact.imageprocessing.core.IFeatureResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalcDisMatrix {	
	public static void main(String args[]) {
		try {
			int		featureElements = 0;

			String baseDir = null;
			if (args != null && args.length > 0) {
				baseDir = args[0];
			}
			
			System.out.println("CalcDisMatrix.");
			
			FeatureCollection featureCol = FeatureCollection.read(baseDir, "img_features.txt");
			
			if (featureCol == null) {
				System.exit(-1);
			}

			int		noSamples = featureCol.getNoSamples();
			int		noFeatureGroups = featureCol.getNoFeatureGroups();
			featureElements = featureCol.getNoTotalFeatureElements();
		
		    System.out.println(noSamples + " samples");
		    System.out.println(noFeatureGroups + " feature groups");
		    System.out.println(featureElements + " elements per sample");
		    
		    System.out.println("Normalizing...");
			// ensure features are in the range 0-1 via normalization
			for (int i = 0; i < noFeatureGroups; i++) {
				int				noElements = featureCol.getNoFeatureElements(i);
				IFeatureResult	fr = featureCol.getFeatureGroup(0, i);
				
				System.out.println(fr.getSource() + " - " + noElements + " elements: " + fr.getFeatureNormalizationMethodId());
				
				for (int j = 0; j < noElements; j++) {
					FeatureNormalization.byMethod(fr.getFeatureNormalizationMethodId(), featureCol, i, j);
				}				
			}
		    
		    System.out.println("Making raw data...");
			featureCol.makeRawData();
			
		    System.out.println("Calculating dissimilarity...");
					    
			// compare all samples pair-wise and calculate dissimilarity matrix
			float[][] featureGroupDistances = new float[noSamples][noSamples];
			float[][] summedDistances = new float[noSamples][noSamples];
			
			for (int k = 0; k < noFeatureGroups; k++) {
				IFeatureResult	fr = featureCol.getFeatureGroup(0, k);
				float			featureGroupSum = 0.0f;

				System.out.print(fr.getSource() + " - " + fr.getDistanceMeasureId() + "...");
				
				for (int i = 1; i < noSamples; i++) {
					for (int j = 0; j < i; j++) {
						featureGroupDistances[i][j] = (float) DistanceMeasuring.byMethod(fr.getDistanceMeasureId(), featureCol, i, j, k);
						
						featureGroupSum += featureGroupDistances[i][j];
					}
				}		
				
				if (featureGroupSum != 0) {
					float invMean = (float) ((noSamples * (noSamples - 1)) / 2) / featureGroupSum;

					float min = Float.MAX_VALUE;
					float max = Float.MIN_VALUE;
					float sum = 0.0f;					
					float sumSquared = 0.0f;					

					for (int i = 1; i < noSamples; i++) {
						for (int j = 0; j < i; j++) {
							float	correctedValue = featureGroupDistances[i][j] * invMean;

							sum += correctedValue;
							sumSquared += correctedValue * correctedValue;
							min = Math.min(correctedValue, min);
							max = Math.max(correctedValue, max);
							
							summedDistances[i][j] += correctedValue;
						}
					}					

					float samples = ((noSamples * (noSamples - 1)) / 2);
					float mean = sum / samples;
					
					System.out.print(": " + max + " " + (sumSquared / (samples - 1) - mean * mean));
				}
				
				System.out.println();
			}

			// write values to dissimilarity matrix file
			File fileDescriptor = new File(baseDir, "dismat.txt");
			PrintWriter file = new PrintWriter(fileDescriptor);
			file.println("size: " + noSamples);

			DecimalFormat	df = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH));
			
			for (int i = 1; i < noSamples; i++) {
				for (int j = 0; j < i; j++) {
					file.print(df.format(Math.exp(summedDistances[i][j]) - 1.0));
					file.print(" ");
				}
				file.println();
			}
			
			file.flush();
			file.close();

			System.out.println("Done.");
		}	
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
