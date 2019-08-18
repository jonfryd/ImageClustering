package dk.impact.imageprocessing.core.operators;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class SubsampleAverageAbsoluteOp extends ImageOperatorAbstract {
	private	double	fixedWidth;
	private	double	fixedHeight;
	
	public SubsampleAverageAbsoluteOp(int fixedWidth, int fixedHeight) {
		setFixedWidth(fixedWidth);
		setFixedHeight(fixedHeight);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		double	width = getFixedWidth() / (double) input.getWidth();
		double	height = getFixedHeight() / (double) input.getHeight();
			
		boolean	canSubsample = (width <= 1.0) && (height <= 1.0);

		if (canSubsample) {
			// width <= 1.0 and height <= 1.0 - do subsampling
			
			ParameterBlock pbSubsample = new ParameterBlock();        
			pbSubsample.addSource(input);        
			
			pbSubsample.add(width); // double, width        
			pbSubsample.add(height); // double, height
			
			RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				
			return JAI.create("SubsampleAverage", pbSubsample, qualityHints);
		}
		else {
			// fall back on resize
			
			ParameterBlock pbResize = new ParameterBlock();        
			pbResize.addSource(input);        
			
			pbResize.add((float) width); // float, width        
			pbResize.add((float) height); // float, height
			pbResize.add(0.0f); // float, x offset (usually 0.0)
			pbResize.add(0.0f); // float, y offset (usually 0.0)
			pbResize.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC)); // interpolation type
			
			return JAI.create("scale", pbResize, null);    			
		}
	}

	public String getName() {
		return "SubsampleAverageAbsolute";
	}

	public double getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(int fixedX) {
		this.fixedWidth = fixedX;		
		setParameter("Fixed width", Integer.toString(fixedX));
	}

	public double getFixedHeight() {
		return fixedHeight;
	}

	public void setFixedHeight(int fixedY) {
		this.fixedHeight = fixedY;
		setParameter("Fixed height", Integer.toString(fixedY));
	}
}
