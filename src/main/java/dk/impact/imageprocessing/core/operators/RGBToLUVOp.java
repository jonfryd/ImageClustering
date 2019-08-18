package dk.impact.imageprocessing.core.operators;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class RGBToLUVOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		ColorSpace yccColorSpace = ColorSpace.getInstance(ColorSpace.CS_PYCC);

		ColorModel luvColorModel = new ComponentColorModel(yccColorSpace,
				null,
				false,false,
				Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);

		ParameterBlock luvColorPb = new ParameterBlock();
		luvColorPb.addSource(input);
		luvColorPb.add(luvColorModel);

		// Perform the band combine operation.
		return JAI.create("ColorConvert", luvColorPb, null);		
	}

	public String getName() {
		return "RGBToLUV";
	}

}
