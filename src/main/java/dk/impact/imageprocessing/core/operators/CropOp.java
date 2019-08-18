package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class CropOp extends ImageOperatorAbstract {
	private	float	width;
	private	float	height;

	public CropOp(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		ParameterBlock cropPb = new ParameterBlock();
		cropPb.addSource(input);
		cropPb.add(0.0f);
		cropPb.add(0.0f);
		cropPb.add(getWidth());
		cropPb.add(getHeight());
		return JAI.create("crop", cropPb, null);
	}

	public String getName() {
		return "Crop";
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		setParameter("Width", Integer.toString(width));
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		setParameter("Height", Integer.toString(height));
	}
}
