package dk.impact.imageprocessing.core.featureextractors;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractHistogramOp extends FeatureExtractorAbstract {
	private	int		bins;
	
	public ExtractHistogramOp(int bins) {
		setBins(bins);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
	    int[] bins = {getBins(), getBins(), getBins()}; // The number of bins.
	    double[] low = {0.0D, 0.0D, 0.0D}; // The low value.
	    double[] high = {256.0D, 256.0D, 256.0D}; // The high value.

	    ParameterBlock pb2 = new ParameterBlock();
	    pb2.addSource(input); // Specify the source image
	    pb2.add(null); // Specify ROI No ROI
	    pb2.add(1); // Sampling
	    pb2.add(1); // periods
	    pb2.add(bins); // Specify the histogram	
	    pb2.add(low); // Specify the histogram
	    pb2.add(high); // Specify the histogram

	    PlanarImage dst1 = (PlanarImage) JAI.create("histogram", pb2, null);

	    Histogram hist1 = null;

	    try {
	    	hist1 = (Histogram) dst1.getProperty("histogram");		
	    	
			int[] totals2 = hist1.getTotals();

			for (int b = 0; b < totals2.length; b++) {
				for (int i = 0; i < hist1.getNumBins(b); i++) {
					addExtractedFeature((double) hist1.getBinSize(b, i) / (double) totals2[b]);
				}
			}	    				
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }

	    return null;
	}

	public String getName() {
		return "ExtractHistogram";
	}

	public int getBins() {
		return bins;
	}

	public void setBins(int bins) {
		this.bins = bins;
		setParameter("Bins", Integer.toString(bins));
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.JSD;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.NONE;
	}
}
