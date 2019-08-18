package dk.impact.imageprocessing.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import dk.impact.imageprocessing.core.ImageProcessingParms;

import fr.inria.axis.clustering.dsom.RunDSOM;


public class Optimizer {
	static double cfgTable[][] = {
		{39, 3, 86, 168, 184.69152698001713, 0.09993279153485864, 3, 4, 111, 31},
		{40, 0, 86, 168, 158.1631389250021, 0.24876798852320148, 1, 5, 108, 27},
		{38, 0, 86, 204, 193.85124193970364, 0.07153534967463634, 3, 5, 97, 33},
		{34, 1, 86, 228, 102.62181295800008, 0.15567963509863308, 3, 4, 92, 39},
		{36, 0, 86, 244, 229.84579522929755, 0.24367116777283793, 3, 6, 111, 32},
		{38, 0, 86, 208, 226.6420352439218, 0.2768397559872692, 2, 5, 116, 31},
		{44, 1, 86, 164, 101.62813598653636, 0.05742739814926599, 2, 4, 96, 34},
		{41, 2, 86, 220, 83.9368849420381, 0.027786073021778587, 1, 5, 92, 34},
		{37, 0, 86, 192, 204.93681814202787, 0.06520096255497569, 2, 5, 116, 25},
		{49, 1, 86, 164, 200.6014538247854, 0.07631290770478583, 1, 5, 94, 31},
		{40, 0, 86, 180, 138.71229205118414, 0.2406670437807482, 3, 6, 116, 31},
		{46, 6, 86, 208, 56.46743046078513, 0.05329272884399568, 2, 4, 91, 34},
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting optimizer.");

		//Random rnd = new Random();

		PrintWriter log = null;
	
		try {
			log = new PrintWriter (new BufferedWriter (new FileWriter (new File("optimizer_log.txt"), true)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int iter = 0;
		
		//while(true) {
		{
			ImageProcessingParms imParms = new ImageProcessingParms();

			int 	clusters; // cfg idx 8
			int 	samp; // cfg idx 9
			double	relativeRadius;
			
			//int 	cfgIdx = iter % 10;
			//int		phase  = iter / 10;
	
			//imParms.imSize 		 	= 250;// + cfgIdx * 20; //(int) cfgTable[cfgIdx][3]; 
			//imParms.edgeThres 	 	= 100.0; //cfgTable[cfgIdx][4];
			//imParms.dctLumSize   	= 2; //(int) cfgTable[cfgIdx][6];
			//imParms.dctChromSize	= 3; //(int) cfgTable[cfgIdx][7];
			//imParms.imTinySize		= 6;
			//imParms.histogramBins	= 10;
			//imParms.textureMaxDelta	= 2;
			clusters       = 100; //(int) cfgTable[cfgIdx][8];
			samp           = 35; //(int) cfgTable[cfgIdx][9];
			//relativeRadius = 0.08;
/*
imParms.imSize 		 = 250;// + cfgIdx * 20; //(int) cfgTable[cfgIdx][3]; 
imParms.edgeThres 	 = 100.0; //cfgTable[cfgIdx][4];
//imParms.dctScale 	 = cfgTable[cfgIdx][5];
imParms.dctLumSize   = 2; //(int) cfgTable[cfgIdx][6];
imParms.dctChromSize = 3; //(int) cfgTable[cfgIdx][7];
clusters       = 100; //(int) cfgTable[cfgIdx][8];
samp           = 35; //(int) cfgTable[cfgIdx][9];
relativeRadius = 0.08; 
			
best: 0 66860.72618854046
Total in test: 85
Total overlapping: 1
Total in same classes: 62 (72.94118%)
Selected test classes: 11
Unique classes in test sets: 23

ExtractDCTSubpixelsOp 4 RANK_NORMALIZATION EUCLIDEAN
ExtractDCTSubpixelsOp 9 RANK_NORMALIZATION EUCLIDEAN
ExtractDCTSubpixelsOp 9 RANK_NORMALIZATION EUCLIDEAN
ExtractEdgeHistogramOp 8 NONE EUCLIDEAN
ExtractTextureFeaturesOp 45 RANK_NORMALIZATION EUCLIDEAN
ExtractPixelsOp 108 LINEAR EUCLIDEAN
ExtractHistogramOp 30 NONE JSD
FIREHistogramLoader 1886 RANK_NORMALIZATION EUCLIDEAN
*/
	
			/*
			switch(phase) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					System.exit(0);
			}
			*/
			
			imParms.updateParmList();
			
			String baseDir = ".";
			
			CalcImagesFeaturesLocal.main(new String[] {baseDir});
			//CalcImageFeatures.main(imParms.toStringArray());
			CalcDisMatrix.main(new String[] {baseDir});
	
			int bestHits = 0;
			int bestOverlapping = 0;
			double bestRelativeRadius = 0.0;

			int totalHits = 0;
			int totalOverlapping = 0;
			
			final int noRuns = 40;
			
			for(int i = 0; i < noRuns; i++) {
				relativeRadius = 0.01 + (i * 0.005);
				
				try {
					//String kmeansArgs[] = new String[]{"-clust", String.valueOf(clusters), "-samp", String.valueOf(samp), "-data", "dismat.txt", "-part", "-outpart", "output.txt"};
					//fr.inria.axis.clustering.kmeans.relational.RunSampledRelationalKMeans.main(kmeansArgs);
					
					//String kmeansArgs[] = new String[]{"-clust", String.valueOf((int) Math.sqrt(clusters)), "-data", "dismat.txt", "-part", "-outpart", "output.txt"};
					//fr.inria.axis.clustering.dsom.relational.RunRelationalDSOM.main(kmeansArgs);
					
					//String dsomArgs[] = new String[]{"-clust", String.valueOf((int) Math.sqrt(clusters)), "-radius", String.valueOf(relativeRadius), "-data", "dismat.txt", "-outpart", "output.txt"};
					String dsomArgs[] = new String[]{"-clust", String.valueOf((int) Math.sqrt(clusters)), "-relativeradius", String.valueOf(relativeRadius), "-data", "dismat.txt", "-outpart", "output.txt"};
					RunDSOM.main(dsomArgs);
					//String somArgs[] = new String[]{"-clust", String.valueOf((int) Math.sqrt(clusters)), "-data", "img_features.txt", "-part", "-outpart", "output.txt", "-step", "100", "-verbose"};
					
					//RunSOM.main(somArgs);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Test.main(null);
				
			    try {
					BufferedReader result = new BufferedReader(new FileReader("test_result.txt"));
					
					String total = result.readLine();
					String overlapping = result.readLine();
					String hits = result.readLine();
					
					if (Integer.parseInt(hits) > bestHits) {
						bestHits =Integer.parseInt(hits);
						bestOverlapping = Integer.parseInt(overlapping);
						bestRelativeRadius = relativeRadius;
					}
					
					totalHits += Integer.parseInt(hits);
					totalOverlapping += Integer.parseInt(overlapping);
					
					result.close();
					
					log.println(hits + " " + 
							overlapping + " " + 
							total + " " + 
							imParms + " " + 
							clusters + " " + 
							samp);
					log.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			System.out.println("All done.");
			
			System.out.println("\nAvg. in same classes: " + (float) totalHits / (float) noRuns); 
			System.out.println("Ang. overlapping, in same classes: " + (float) totalOverlapping / (float) noRuns); 

			System.out.println("\nBest relative radius: " + bestRelativeRadius); 
			System.out.println("Best in same classes: " + bestHits); 
			System.out.println("Best overlapping, in same classes: " + bestOverlapping); 
			
			//
			//
			try {
				String dsomArgs[] = new String[]{"-clust", String.valueOf((int) Math.sqrt(clusters)), "-relativeradius", String.valueOf(bestRelativeRadius), "-data", "dismat.txt", "-outpart", "output.txt"};
				RunDSOM.main(dsomArgs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//
			//
			
			System.exit(0);
			
			iter++;
		}
	}

}
