package dk.impact.imageprocessing.core.operators;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class ConvToIntFormatOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		ParameterBlock pb = new ParameterBlock();    
		pb.addSource(input);    
		pb.add(DataBuffer.TYPE_INT);    
		return JAI.create("format", pb);    
	}

	public String getName() {
		return "ConvToIntFormat";
	}

}
