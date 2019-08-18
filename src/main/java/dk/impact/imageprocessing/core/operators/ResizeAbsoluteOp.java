package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class ResizeAbsoluteOp extends ImageOperatorAbstract {
	private	float			fixedWidth;
	private	float			fixedHeight;
	private float			offsetX;
	private float			offsetY;
	private	Interpolation	interpolationType;
	
	public ResizeAbsoluteOp(int fixedWidth, int fixedHeight) {
		setFixedWidth(fixedWidth);
		setFixedHeight(fixedHeight);
		setOffsetX(0.0f);
		setOffsetY(0.0f);
		setInterpolationType(Interpolation.INTERP_BICUBIC);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		float	width = getFixedWidth() / (float) input.getWidth();
		float	height = getFixedHeight() / (float) input.getHeight();
				
		ParameterBlock pbResize = new ParameterBlock();        
		pbResize.addSource(input);        
		
		pbResize.add(width); // float, width        
		pbResize.add(height); // float, height
		pbResize.add(getOffsetX()); // float, x offset (usually 0.0)
		pbResize.add(getOffsetY()); // float, y offset (usually 0.0)
		pbResize.add(getInterpolationType()); // interpolation type
		
		return JAI.create("scale", pbResize, null);      
	}

	public String getName() {
		return "ResizeAbsolute";
	}

	public float getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(int fixedX) {
		this.fixedWidth = fixedX;		
		setParameter("Fixed width", Integer.toString(fixedX));
	}

	public float getFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(int fixedY) {
		this.fixedHeight = fixedY;
		setParameter("Fixed height", Integer.toString(fixedY));
	}
	
	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		setParameter("Offset X", Float.toString(offsetX));
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		setParameter("Offset Y", Float.toString(offsetY));
	}

	public Interpolation getInterpolationType() {
		return interpolationType;
	}

	public void setInterpolationType(int type) {
		this.interpolationType = Interpolation.getInstance(type);
		setParameter("Interpolation Type", interpolationType.getClass().getSimpleName());
	}	
}
