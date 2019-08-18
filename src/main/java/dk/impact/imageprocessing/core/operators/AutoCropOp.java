package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class AutoCropOp extends ImageOperatorAbstract {

	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		FindImageBoundsOp	findImageBounds = new FindImageBoundsOp();

		findImageBounds.invoke(input);
		
		if (findImageBounds.isCrop()) {
			TranslateOp		translate = new TranslateOp(-findImageBounds.getMinX(), -findImageBounds.getMinY());
			CropOp			crop = new CropOp(findImageBounds.getMaxX() - findImageBounds.getMinX(),
											  findImageBounds.getMaxY() - findImageBounds.getMinY());
			
			return crop.invoke(translate.invoke(input));
		}
		
		return input;			
	}

	public String getName() {
		return "AutoCrop";
	}

}
