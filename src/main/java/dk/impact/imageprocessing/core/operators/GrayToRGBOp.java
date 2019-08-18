package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class GrayToRGBOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		if (input.getColorModel().getNumColorComponents() == 1) {
			double[][] toRGBMatrix = {
					{ 1.0D, 0.0D },
					{ 1.0D, 0.0D },
					{ 1.0D, 0.0D }
			};

			ParameterBlock pb = new ParameterBlock();
			pb.addSource(input);
			pb.add(toRGBMatrix);

			// Perform the band combine operation.
			input = (PlanarImage)JAI.create("bandcombine", pb, null);
		}
		
		return input;
	}

	public String getName() {
		return "GrayToRGB";
	}
}
