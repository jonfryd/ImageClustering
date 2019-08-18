package dk.impact.imageprocessing.core;

import java.awt.image.RenderedImage;
import java.util.Map;
import java.util.TreeMap;

public abstract class ProcessNodeAbstract {
	private static final 	boolean printDebug  = false;
	private static final 	boolean printTiming = false;
	
	private	IImageOperator	operator;
	private	int				nodeId;

	public ProcessNodeAbstract(IImageOperator op, int nodeId) {
		setNodeId(nodeId);		
		setOperator(op);
	}

	public ProcessNodeAbstract(IImageOperator op) {
		this(op, -1);
	}
	
	public IImageOperator getOperator() {
		return operator;
	}

	public void setOperator(IImageOperator operator) {
		this.operator = operator;
	}
	
	public final void traverse(RenderedImage im, Map<String,Double> featureMap) {
		// print debug
		if (printDebug) {
			System.out.println(getOperator());
		}

		// process image
		im = getOperator().invoke(im);
		
		// print timing
		if (printTiming) {
			System.out.println(getOperator().getName() + " took " + getOperator().getDeltaMillis() + " ms.");
		}
		
		postInvoke(im, featureMap);
	}

	protected abstract void postInvoke(RenderedImage im, Map<String,Double> featureMap);
	
	final public Map<String,Double> process() {
		TreeMap<String,Double>	featureMap = new TreeMap<String,Double>();
		
		// print debug
		if (printDebug) {
			System.out.println("Starting image operator traversal.");
		}

		// process images and collect extracted features
		traverse(null, featureMap);

		// print debug
		if (printDebug) {
			System.out.println("Processing completed.");
		}
		
		return featureMap;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}
}
