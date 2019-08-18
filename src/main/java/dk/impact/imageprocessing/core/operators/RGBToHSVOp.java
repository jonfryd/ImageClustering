package dk.impact.imageprocessing.core.operators;

import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.IHSColorSpace;
import javax.media.jai.JAI;
import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class RGBToHSVOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
        IHSColorSpace hsvSpace = IHSColorSpace.getInstance();
	    ColorModel hsvColorModel = new ComponentColorModel(hsvSpace,
							       new int []{8,8,8},
							       false,false,
							       Transparency.OPAQUE,
							       DataBuffer.TYPE_BYTE) ;

	    ParameterBlock colorPb = new ParameterBlock();
	    colorPb.addSource(input);
	    colorPb.add(hsvColorModel);

	    return JAI.create("ColorConvert", colorPb);
	}

	public String getName() {
		return "RGBToHSV";
	}

}
