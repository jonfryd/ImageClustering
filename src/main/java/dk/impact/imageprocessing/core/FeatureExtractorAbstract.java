package dk.impact.imageprocessing.core;
import java.awt.image.RenderedImage;
import java.util.Vector;


public abstract class FeatureExtractorAbstract extends ImageOperatorAbstract implements IFeatureExtractor {
	private		Vector<Double>		extractedFeatures;
	
	protected FeatureExtractorAbstract() {
		super();
		extractedFeatures = new Vector<Double>();
	}	

	abstract protected RenderedImage doProcessing(RenderedImage input);
	
	public Vector<Double> getResult() {
		return extractedFeatures;
	}
	
	protected void addExtractedFeature(double value) {
		extractedFeatures.add(value);
	}
	
	public String getSource() {
		return getClass().getSimpleName();
	}
}
