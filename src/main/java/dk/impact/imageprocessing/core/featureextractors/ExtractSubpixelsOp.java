package dk.impact.imageprocessing.core.featureextractors;

import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractSubpixelsOp extends FeatureExtractorAbstract {
	private	int		width;
	private	int		height;
	private int		layer;
	
	public ExtractSubpixelsOp(int width, int height, int layer) {
		setWidth(width);
		setHeight(height);
		setLayer(layer);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				double pixel[] = input.getData().getPixel(j, i, (double[]) null);
				
				addExtractedFeature(pixel[getLayer()]);
			}
		}

		return null;
	}

	public String getName() {
		return "ExtractSubpixels";
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		setParameter("Width", Integer.toString(width));
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		setParameter("Height", Integer.toString(height));
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
		setParameter("Layer", Integer.toString(layer));
	}	

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.EUCLIDEAN;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.DIVIDE_BY_255;
	}
}
