package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class RGBToGrayOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		double[][] grayMatrix = {
				{ 0.299, 0.587, 0.114, 0.0 }
		};

		ParameterBlock grayPb = new ParameterBlock();
		grayPb.addSource(input);
		grayPb.add(grayMatrix);

		// Perform the band combine operation.
		return JAI.create("bandcombine", grayPb, null);
	}

	public String getName() {
		return "RGBToGray";
	}

}
