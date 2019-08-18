package dk.impact.imageprocessing.core.featureextractors;

import java.awt.image.RenderedImage;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractEdgeHistogramOp extends FeatureExtractorAbstract {
	private	double	edgeThreshold;

	public ExtractEdgeHistogramOp(double edgeThreshold) {
		setEdgeThreshold(edgeThreshold);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		// input is assumed to be a grayscale image
		KernelJAI kern_h = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
		KernelJAI kern_v = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;

		// Create the Gradient operation.
		PlanarImage imSobelH = (PlanarImage) JAI.create("convolve", input, kern_h);		    
		PlanarImage imSobelV = (PlanarImage) JAI.create("convolve", input, kern_v);		    

		int edgeHistogram[] = new int[8];
		int totalEdges = 0;
		
		for (int i = 0; i < input.getHeight(); i++) {
			float sobelLineH[] = imSobelH.getData().getPixels(0, i, imSobelH.getWidth(), 1, (float[]) null);
			float sobelLineV[] = imSobelV.getData().getPixels(0, i, imSobelV.getWidth(), 1, (float[]) null);

			for (int j = 0; j < input.getWidth(); j++) {
				double amp = Math.hypot(sobelLineV[j], sobelLineH[j]);

				if (amp > getEdgeThreshold()) {
					double angle = Math.atan2(sobelLineV[j], sobelLineH[j]) * 180.0 / Math.PI;
					double dblQuad = (angle + 180) / 45.0;
					int quad = (int) (dblQuad % 8);

					edgeHistogram[quad]++;
					totalEdges++;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			addExtractedFeature((double) edgeHistogram[i] / (double) totalEdges);
		}
		
		return null;
	}

	public String getName() {
		return "ExtractEdgeHistogram";
	}

	public double getEdgeThreshold() {
		return edgeThreshold;
	}

	public void setEdgeThreshold(double edgeThreshold) {
		this.edgeThreshold = edgeThreshold;
		setParameter("Edge threshold", Double.toString(edgeThreshold));
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.JSD;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.NONE;
	}
}
