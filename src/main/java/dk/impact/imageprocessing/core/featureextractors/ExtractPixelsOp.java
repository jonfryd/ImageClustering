package dk.impact.imageprocessing.core.featureextractors;

import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractPixelsOp extends FeatureExtractorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		for (int i = 0; i < input.getHeight(); i++) {
			for (int j = 0; j < input.getWidth(); j++) {
				double pixel[] = input.getData().getPixel(j, i, (double[]) null);
				
				addExtractedFeature(pixel[0]);
				addExtractedFeature(pixel[1]);
				addExtractedFeature(pixel[2]);
			}
		}

		return null;
	}

	public String getName() {
		return "ExtractPixels";
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.EUCLIDEAN;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.DIVIDE_BY_255;
	}
}
