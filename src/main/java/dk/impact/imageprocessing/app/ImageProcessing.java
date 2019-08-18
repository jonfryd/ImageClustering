package dk.impact.imageprocessing.app;

import dk.impact.imageprocessing.core.FeatureCollection;
import dk.impact.imageprocessing.core.IFeatureResult;
import dk.impact.imageprocessing.core.ImageProcessNode;
import dk.impact.imageprocessing.core.ImageProcessingParms;
import dk.impact.imageprocessing.core.featureloaders.FIREHistogramLoader;
import dk.impact.imageprocessing.core.operators.ImageLoadOp;
import dk.impact.imageprocessing.core.procedures.MultiFeatureProcedure1;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * 
 */

public class ImageProcessing {
	/**
	 * 
	 */
	ImageProcessingParms	imParms;
	
	public ImageProcessing (ImageProcessingParms imParms) {
		this.imParms = imParms;
	}
	
	public Vector<String> processImage(String fileName) throws IOException {
		Vector<String>			results = new Vector<String>();		
		ImageLoadOp				imageLoadOp = new ImageLoadOp(fileName);
		ImageProcessNode		root = new ImageProcessNode(imageLoadOp);
		MultiFeatureProcedure1	mfp1 = new MultiFeatureProcedure1(imParms);
		FIREHistogramLoader		fireHistLoader = new FIREHistogramLoader();
		
		// perform processing
		mfp1.process(root);
		
		// close file 
		imageLoadOp.closeStream();
		
		// retrieve result
    	Vector<IFeatureResult> 	featureResults = mfp1.getFeatureResults();

    	// load LF histogram data (if possible)
		String fireHistFilename = fileName + ".pca.lf.gz.histo.gz";
		File histoData = new File(fireHistFilename);

//		if (histoData.exists()) {
//			if (!fireHistLoader.load(fireHistFilename)) {
//				System.out.println("Failed to load LF histogram data");
//				System.exit(-1);
//			}
//			else {
//				// merge loaded features in result set
//				featureResults.add(fireHistLoader);				
//			}
//		}

		// create feature collection
		for (int i = 0; i < featureResults.size(); i++) {
			IFeatureResult	fr = featureResults.get(i);
			
			FeatureCollection.addResults(fr, results);				
		}
		
		return results;
	}
}