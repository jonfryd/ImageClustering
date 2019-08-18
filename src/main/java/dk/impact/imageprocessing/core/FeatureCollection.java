package dk.impact.imageprocessing.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

// Uses:
//
// 1. Set<Vector<FeatureResult>> (feature extraction result)
// 2. Set<Vector<String>> -> FeatureCollection (after transfer from worker to collector)
// 3. FeatureCollection -> 2 files (data + descriptor) (collector result)
// 4. 2 files (data + descriptor) -> FeatureCollection (loading data in CalcDisMatrix)
//
public class FeatureCollection {
	public static final		String	DESCRIPTOR_SUFFIX = ".desc";
	
	private Vector<Vector<IFeatureResult>>	featureResults;
	private Vector<Vector<double[]>>		rawData;
		
	public FeatureCollection(Vector<Vector<IFeatureResult>> featureResults) {
		this.featureResults = featureResults;
		rawData = null;
	}
	
	public int getNoSamples() {
		return featureResults.size();
	}

	public int getNoFeatureGroups() {
		return featureResults.get(0).size();
	}

	public int getNoFeatureElements(int featureGroupIndex) {
		return getFeatureGroup(0, featureGroupIndex).getResult().size();
	}
	
	public int getNoTotalFeatureElements() {
		int result = 0;
		
		for(int i = 0; i < getNoFeatureGroups(); i++) {
			result += getNoFeatureElements(i);
		}
		
		return result;
	}
	
	public Vector<IFeatureResult> getSample(int sampleIndex) {
		return featureResults.get(sampleIndex);
	}

	public IFeatureResult getFeatureGroup(int sampleIndex, int featureGroupIndex) {
		return getSample(sampleIndex).get(featureGroupIndex);
	}

	public double getFeatureElement(int sampleIndex, int featureGroupIndex, int elementIndex) {
		return getFeatureGroup(sampleIndex, featureGroupIndex).getResult().get(elementIndex);
	}

	public double setFeatureElement(int sampleIndex, int featureGroupIndex, int elementIndex, double value) {
		return getFeatureGroup(sampleIndex, featureGroupIndex).getResult().set(elementIndex, value);
	}

	public double[] getRawFeatureGroup(int sampleIndex, int featureGroupIndex) {
		return rawData.get(sampleIndex).get(featureGroupIndex);
	}
	
	public void makeRawData() {
		int		noSamples = getNoSamples();
		int		noFeatureGroups = getNoFeatureGroups();		
			
		rawData = new Vector<Vector<double[]>>();
		
		for(int i = 0; i < noSamples; i++) {
			Vector<double[]> group = new Vector<double[]>();
			
			for(int j = 0; j < noFeatureGroups; j++) {
				int			noElements = getNoFeatureElements(j);
				double[]	data = new double[noElements];
				
				for(int k = 0; k < noElements; k++) {
					data[k] = getFeatureElement(i, j, k);
				}	
				
				group.add(data);
			}			
			
			rawData.add(group);
		}
	}
	
	public static FeatureCollection createFeatureCollection(Vector<Vector<String>> featureVectorSet) {
		Vector<Vector<IFeatureResult>>	result = new Vector<Vector<IFeatureResult>>();
		
		for (Vector<String> vec : featureVectorSet) {
			int i = 0;			
			
			Vector<IFeatureResult>	frVector = new Vector<IFeatureResult>();
			
			while (i < vec.size()) {
				String							source = vec.get(i++);
				int								size = Integer.parseInt(vec.get(i++));
				FeatureNormalization.MethodId	nmi = Enum.valueOf(FeatureNormalization.MethodId.class, vec.get(i++));
				DistanceMeasuring.MethodId		dmi = Enum.valueOf(DistanceMeasuring.MethodId.class, vec.get(i++));
				
				Vector<Double>	data = new Vector<Double>(size);
				
				for (int j = 0; j < size; j++) {
					data.add(Double.parseDouble(vec.get(i++)));
				}
				
				frVector.add(new BasicFeatureResult(source, dmi, nmi, data));				
			}
			
			result.add(frVector);
		}
		
		return new FeatureCollection(result);
	}
	
	public static FeatureCollection read(String baseDir, String baseFilename) throws IOException {		
		Vector<String>							sources = new Vector<String>();
		Vector<Integer>							sizes = new Vector<Integer>();
		Vector<FeatureNormalization.MethodId>	nmis = new Vector<FeatureNormalization.MethodId>();
		Vector<DistanceMeasuring.MethodId>		dmis = new Vector<DistanceMeasuring.MethodId>();
		int										featureGroups = 0;
		
		BufferedReader	descriptors = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir, baseFilename + DESCRIPTOR_SUFFIX))));				

		while(true) {
			String	line = descriptors.readLine();
			
			if (line == null) {
				break;
			}
			
			StringTokenizer st = new StringTokenizer(line, " ");
			
			sources.add(st.nextToken());
			sizes.add(Integer.parseInt(st.nextToken()));
			nmis.add(Enum.valueOf(FeatureNormalization.MethodId.class, st.nextToken()));
			dmis.add(Enum.valueOf(DistanceMeasuring.MethodId.class, st.nextToken()));
			
			featureGroups++;
		}

		descriptors.close();			
	
		BufferedReader	feats = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir, baseFilename))));

		Vector<Vector<IFeatureResult>>	result = new Vector<Vector<IFeatureResult>>();			
		
		while(true) {
			String	line = feats.readLine();
			
			if (line == null) {
				break;
			}

			StringTokenizer st = new StringTokenizer(line, " ");
			
			Vector<IFeatureResult>	frVector = new Vector<IFeatureResult>();

			for (int i = 0; i < featureGroups; i++) {
				Vector<Double>	data = new Vector<Double>(sizes.get(i));
									
				for (int j = 0; j < sizes.get(i); j++) {
					data.add(Double.parseDouble(st.nextToken()));
				}	
				
				frVector.add(new BasicFeatureResult(sources.get(i), dmis.get(i), nmis.get(i), data));				
			}

			result.add(frVector);
		}
		
		feats.close();			
		
		return new FeatureCollection(result);
	}
	
	public void write(String baseDir, String baseFilename) throws IOException {
		boolean	first = true;
		
	    PrintWriter featuresFile = new PrintWriter (new BufferedWriter (new FileWriter (new File(baseDir, baseFilename))));
		PrintWriter descriptorsFile  = new PrintWriter (new BufferedWriter (new FileWriter (new File(baseDir, baseFilename + DESCRIPTOR_SUFFIX))));
		
		for (Vector<IFeatureResult> results : featureResults) {
			for (IFeatureResult fr : results) {
				if (first) {
					descriptorsFile.println(
							fr.getSource() + " " + 
							fr.getResult().size() + " " + 
							fr.getFeatureNormalizationMethodId().toString() + " " + 
							fr.getDistanceMeasureId().toString());					
				}
				
				for (Double sample : fr.getResult()) {
					featuresFile.print(sample + " ");					
				}
			}

			featuresFile.println();
			
			first = false;			
		}		

		featuresFile.close ();
		descriptorsFile.close ();
	}
	
	public static void addResults(IFeatureResult fr, Vector<String> results) {
		//System.out.println(fr.getClass().getSimpleName());
		//System.out.println("Size: " + (fr.getResult().size()));
		//System.out.println("Norm: " + fr.getFeatureNormalizationMethodId());
		//System.out.println("Dist: " + fr.getDistanceMeasureId());
		
		results.add(fr.getSource());
		results.add(Integer.toString(fr.getResult().size()));
		results.add(fr.getFeatureNormalizationMethodId().toString());
		results.add(fr.getDistanceMeasureId().toString());
		
		for(int j = 0; j < fr.getResult().size(); j++) {
			results.add(Double.toString(fr.getResult().get(j)));
		}		
	}
	
}
