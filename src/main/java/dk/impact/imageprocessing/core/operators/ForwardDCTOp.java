package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class ForwardDCTOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		ParameterBlock pbDCT = (new ParameterBlock()).addSource(input); 
		return JAI.create("dct", pbDCT, null);
	}

	public String getName() {
		return "ForwardDCT";
	}

}
