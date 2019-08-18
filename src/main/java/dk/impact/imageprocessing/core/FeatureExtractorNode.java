package dk.impact.imageprocessing.core;

import java.awt.image.RenderedImage;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Vector;

public class FeatureExtractorNode extends ProcessNodeAbstract {
//	private static final 				boolean printDebug  = false;
//	private static final 				boolean printTiming = false;

	public FeatureExtractorNode(IFeatureExtractor fe, int nodeId) {
		super(fe, nodeId);
	}

	public FeatureExtractorNode(IFeatureExtractor fe) {
		this(fe, -1);
	}
	
	protected void postInvoke(RenderedImage im, Map<String,Double> featureMap) {		
		// collect features if available
		Vector<Double> tmpFeatures = ((IFeatureExtractor) getOperator()).getResult();
		
		if (tmpFeatures != null) {
			DecimalFormat	df = new DecimalFormat("000");
			
			int i = 1;
			
			for (Double d : tmpFeatures) {
				featureMap.put(df.format(getNodeId()) + "_" + getOperator().getName() + "_" + df.format(i++), d);							
			}
		}
	}	
}
