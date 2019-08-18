package dk.impact.imageprocessing.core.featureextractors;

import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractDCTSubpixelsOp extends ExtractSubpixelsOp {
	public ExtractDCTSubpixelsOp(int width, int height, int layer) {
		super(width, height, layer);
	}

	public String getName() {
		return "ExtractDCTSubpixelsOp";
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		// rank normalization is probably best suited for DCT coefficients
		return FeatureNormalization.MethodId.RANK_NORMALIZATION;
	}	
}
