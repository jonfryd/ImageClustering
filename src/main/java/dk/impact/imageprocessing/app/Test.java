package dk.impact.imageprocessing.app;

import fr.inria.axis.similarity.DissimilarityMatrix;
import fr.inria.axis.similarity.DissimilarityMatrixIO;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		calcInTestRatio(args); // ignore result
	}
	
	public static float calcInTestRatio(String[] args) {
		try {
		    File result_log = new File("test_result.txt");
		    File result_dismat_ana = new File("test_dismat_analysis.txt");
		    File dismat_ana = new File("dismat_analysis.txt");

		    DecimalFormat df = new DecimalFormat("0.000");
		    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);
		    df.setDecimalFormatSymbols(dfs);
		    
		    BufferedReader test = new BufferedReader(new FileReader("test.txt"));	    
		    Vector<Vector<String>>	testLists = new Vector<Vector<String>>();
		    
		    while (true) {
				String files = test.readLine();
				
				if (files == null) {
					break;
				}
				
				StringTokenizer st = new StringTokenizer(files, ",");
				
				Vector<String>	testList = new Vector<String>();
				
				while(st.hasMoreTokens()) {
					String 	file = st.nextToken();
					
					testList.add(file);
				}
				
				testLists.add(testList);
		    }
		    
		    test.close ();
		    
		    BufferedReader in_out = new BufferedReader(new FileReader("output.txt"));	    
		    BufferedReader in_files = new BufferedReader(new FileReader("img_names.txt"));

		    BufferedReader disMatBr = new BufferedReader(new FileReader("dismat.txt"));	    
		    DissimilarityMatrix dm = DissimilarityMatrixIO.textLoad(disMatBr);
		    
		    PrintWriter log = new PrintWriter (new BufferedWriter (new FileWriter (result_log, false)));
		    PrintWriter testDismatPw = new PrintWriter (new BufferedWriter (new FileWriter (result_dismat_ana, false)));
		    PrintWriter dismatPw = new PrintWriter (new BufferedWriter (new FileWriter (dismat_ana, false)));

		    HashMap<String, Integer> fileToClass  = new HashMap<String, Integer>();
		    HashMap<String, Integer> fileToMatIdx = new HashMap<String, Integer>();
		    HashMap<Integer, Vector<String>> classToFiles = new HashMap<Integer, Vector<String>>();
		    
		    // establish file to class mapping
		    int matIdx = 0;
		    
			while(true) {
				String out = in_out.readLine();
				String in_file = in_files.readLine();
		
				if (out == null || in_file == null) {
					break;
				}
		
				int classIdx = Integer.parseInt(out);
				
				fileToClass.put(in_file, classIdx);
				fileToMatIdx.put(in_file, matIdx);
				
				//
				Vector<String> filesVec = classToFiles.get(classIdx);
				
				if (filesVec == null) {
					filesVec = new Vector<String>();
					classToFiles.put(classIdx, filesVec);
				}
				
				filesVec.add(in_file);
				
				matIdx++;
		    }
			
			double dmGlobalClassMin = Double.MAX_VALUE;
			double dmGlobalClassMax = Double.MIN_VALUE;			
			boolean globalDefined = false;
			
			// dissimilarity matrix analysis			
			for(Integer i : classToFiles.keySet()) {
				Vector<String> v = classToFiles.get(i);
				
				dismatPw.print("Class: " + i + " - ");
				
				for (int j = 0; j < v.size(); j++) {
					dismatPw.print(v.get(j) + " ");
				}	
				
				dismatPw.println();
				
				double dmTestClassMin = Double.MAX_VALUE;
				double dmTestClassMax = Double.MIN_VALUE;
				
				for (int j = 1; j < v.size(); j++) {
					int	idx1 = fileToMatIdx.get(v.get(j));

					dismatPw.print(v.get(j) + " ");
					
					for (int k = 0; k < j; k++) {
						int	idx2 = fileToMatIdx.get(v.get(k));
						
						double value = dm.get(idx1, idx2);
						
						dmTestClassMin = Math.min(dmTestClassMin, value);
						dmTestClassMax = Math.max(dmTestClassMax, value);
						
						dismatPw.print(df.format(value) + " ");
					}			
					dismatPw.println();
				}

				if (v.size() > 1) {
					dmGlobalClassMin = Math.min(dmGlobalClassMin, dmTestClassMin);
					dmGlobalClassMax = Math.max(dmGlobalClassMax, dmTestClassMax);

					globalDefined = true;
					
					dismatPw.println("Min/max: " + df.format(dmTestClassMin) + "/" + df.format(dmTestClassMax));
				}
				dismatPw.println();
			}

			if (globalDefined) {
				dismatPw.println("Global Min/max: " + df.format(dmGlobalClassMin) + "/" + df.format(dmGlobalClassMax));
			}
			
			int totalInClass = 0;
			int totalInTest = 0;
			int totalOverlap = 0;
			
			dmGlobalClassMin = Double.MAX_VALUE;
			dmGlobalClassMax = Double.MIN_VALUE;					
			globalDefined = false;
			
			boolean usedClassCount[] = new boolean[300];
			boolean uniqueClassCount[] = new boolean[300];

			int selectedClasses = 0;

			for(int i = 0; i < testLists.size(); i++) {
				Vector<String>	testList = testLists.get(i);

				int classCount[] = new int[300];

				int	highCount      = 0;
				int	highCountClass = -1;

				for (int j = 0; j < testList.size(); j++) {
					String	testFile = (String) testList.get(j);
					
					Integer val = fileToClass.get(testFile);
					
					if (val != null) {
						int classIdx = val.intValue();
						int newCount = ++classCount[classIdx];
						uniqueClassCount[classIdx] = true;
						
						if (highCount < newCount) {
							highCount = newCount;
							highCountClass = classIdx;
						}
						
						if (usedClassCount[classIdx]) {
							totalOverlap++;
						}
					}
					
					
					//System.out.print(fileToClass.get(testFile) + " ");
				}

				//System.out.println();
				//System.out.println(highCount + " " + highCountClass);
				
				// test dissimilarity analysis
				testDismatPw.print("Test class: " + i + " - ");
				
				for (int j = 0; j < testList.size(); j++) {
					testDismatPw.print(testList.get(j) + " ");
					
					// code dupe
					Integer val = fileToClass.get(testList.get(j));
					
					if (val != null) {
						int classIdx = val.intValue();
						
						testDismatPw.print("(" + classIdx);
						
						if (usedClassCount[classIdx]) {
							testDismatPw.print("!");
						}
						
						testDismatPw.print(") ");
					}					
				}	
				
				testDismatPw.println();
				
				double dmTestClassMin = Double.MAX_VALUE;
				double dmTestClassMax = Double.MIN_VALUE;
				
				for (int j = 1; j < testList.size(); j++) {
					int	idx1 = fileToMatIdx.get(testList.get(j));

					testDismatPw.print(testList.get(j) + " ");

					// code dupe
					Integer val = fileToClass.get(testList.get(j));
					
					if (val != null) {
						int classIdx = val.intValue();
						
						testDismatPw.print("(");
						testDismatPw.format("%3d", classIdx);
						
						if (usedClassCount[classIdx]) {
							testDismatPw.print("!");
						} else {
							testDismatPw.print(" ");
						}
						
						testDismatPw.print(") ");
					}					
					
					for (int k = 0; k < j; k++) {
						int	idx2 = fileToMatIdx.get(testList.get(k));
						
						double value = dm.get(idx1, idx2);
						
						dmTestClassMin = Math.min(dmTestClassMin, value);
						dmTestClassMax = Math.max(dmTestClassMax, value);
						
						testDismatPw.print(df.format(value) + " ");
					}			
					testDismatPw.println();
				}
				
				//
				if (highCount > 1) {
					totalInClass += highCount;
					usedClassCount[highCountClass] = true;
					selectedClasses++;
					
					//
					testDismatPw.println("Class " + highCountClass + " selected (" + highCount + "/" + testList.size() + ").");

					//
					dmGlobalClassMin = Math.min(dmGlobalClassMin, dmTestClassMin);
					dmGlobalClassMax = Math.max(dmGlobalClassMax, dmTestClassMax);
					
					globalDefined = true;

					testDismatPw.println("Min/max: " + df.format(dmTestClassMin) + "/" + df.format(dmTestClassMax));
				}

				totalInTest += testList.size();		

				testDismatPw.println();
			}

			int uniqueClasses = 0;
			for (int i = 0; i < uniqueClassCount.length; i++) {
				uniqueClasses = uniqueClasses + (uniqueClassCount[i] ? 1 : 0);
			}

			if (globalDefined) {
				testDismatPw.println("Global Min/max: " + df.format(dmGlobalClassMin) + "/" + df.format(dmGlobalClassMax));
			}
			
			testDismatPw.println("\nTotal in test: " + totalInTest);
			testDismatPw.println("Total overlapping: " + totalOverlap);
			testDismatPw.println("Total in same classes: " + totalInClass + " (" + 100.0f * (float) totalInClass / (float) totalInTest + "%)");
			testDismatPw.println("Selected test classes: " + selectedClasses);
			testDismatPw.println("Unique classes in test sets: " + uniqueClasses);

			System.out.println("Total in test: " + totalInTest);
			System.out.println("Total overlapping: " + totalOverlap);
			System.out.println("Total in same classes: " + totalInClass + " (" + 100.0f * (float) totalInClass / (float) totalInTest + "%)");
			System.out.println("Selected test classes: " + selectedClasses);
			System.out.println("Unique classes in test sets: " + uniqueClasses);
			
			log.println(totalInTest);
			log.println(totalOverlap);
			log.println(totalInClass);
			log.println(selectedClasses);
			log.println(uniqueClasses);
			
		    log.flush();
		    log.close();
		    in_out.close ();
		    in_files.close();
		    testDismatPw.close();
		    dismatPw.close();
		    
		    return (float) totalInTest / (float) totalOverlap;
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return 0.0f;
	}
}
