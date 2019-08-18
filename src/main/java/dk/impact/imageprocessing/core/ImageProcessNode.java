package dk.impact.imageprocessing.core;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Map;

public class ImageProcessNode extends ProcessNodeAbstract {
	private	ArrayList<ProcessNodeAbstract>		leafs;	

	public ImageProcessNode(IImageOperator op, int nodeId) {
		super(op, nodeId);
		leafs = new ArrayList<ProcessNodeAbstract>();
	}

	public ImageProcessNode(IImageOperator op) {
		this(op, -1);
	}

	public void addNode(ProcessNodeAbstract node) {
		leafs.add(node);
	}
		
	@Override
	protected void postInvoke(RenderedImage im, Map<String, Double> featureMap) {
		// traverse tree
		for (ProcessNodeAbstract leaf : leafs) {
			leaf.traverse(im, featureMap);
		}
	}
}
