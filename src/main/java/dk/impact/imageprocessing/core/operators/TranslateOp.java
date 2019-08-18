package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class TranslateOp extends ImageOperatorAbstract {
	private float 	offsetX;
	private float 	offsetY;

	public TranslateOp(int offsetX, int offsetY) {
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		ParameterBlock transPb = new ParameterBlock();  
		transPb.addSource(input);  
		transPb.add(getOffsetX());  
		transPb.add(getOffsetY());  
		// Create the output image by translating itself.
		return JAI.create("translate",transPb,null);  					
	}

	public String getName() {
		return "Translate";
	}
	
	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
		setParameter("Offset X", Float.toString(offsetX));
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		setParameter("Offset Y", Float.toString(offsetY));
	}
}
