package dk.impact.imageprocessing.core.procedures;

import java.util.Map;
import java.util.Vector;

import dk.impact.imageprocessing.core.IFeatureExtractor;
import dk.impact.imageprocessing.core.FeatureExtractorNode;
import dk.impact.imageprocessing.core.IFeatureResult;
import dk.impact.imageprocessing.core.IImageOperator;
import dk.impact.imageprocessing.core.ImageProcessNode;

public abstract class ImageProcessingProcedureAbstract {
	private int 					extractNodeId;
	private Vector<IFeatureResult>	featureResults;
	
	public ImageProcessingProcedureAbstract() {
		featureResults = new Vector<IFeatureResult>();
	}

	public final Map<String,Double> process(ImageProcessNode root) {
		setExtractNodeId(0);
		
		// setup network (abstract)
		setupNetwork(root);
		
		return root.process();
	}
	
	protected abstract void setupNetwork(ImageProcessNode root);

	public int incrementAndGetExtractNodeId() {
		return ++extractNodeId;
	}

	public void setExtractNodeId(int extractNodeId) {
		this.extractNodeId = extractNodeId;
	}
	
	public ImageProcessNode createProcessNode(IImageOperator imgOp) {
		return new ImageProcessNode(imgOp);
	}
	
	public FeatureExtractorNode createFeatureExtractorNode(IFeatureExtractor featExt) {
		featureResults.add(featExt);

		return new FeatureExtractorNode(featExt, incrementAndGetExtractNodeId());
	}

	public Vector<IFeatureResult> getFeatureResults() {
		return featureResults;
	}
}
