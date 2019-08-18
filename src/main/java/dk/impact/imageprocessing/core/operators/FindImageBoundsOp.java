package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class FindImageBoundsOp extends ImageOperatorAbstract {
	private int 		minX;
	private int 		minY;
	private int 		maxX;
	private int 		maxY;
	private boolean 	crop;

	public FindImageBoundsOp() {
		setMinX(0);
		setMinY(0);
		setMaxX(0);
		setMaxY(0);
		setCrop(false);		
	}
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		int 	minX = input.getWidth();
		int 	minY = input.getHeight();
		int 	maxX = 0;
		int 	maxY = 0;
		boolean	crop = false;
		
		for(int y = 0; y < input.getHeight(); y++) {
			int pixels[] = input.getData().getPixels(0, y, input.getWidth(), 1, (int[]) null);
			
			for(int i = 0, x = 0; i < pixels.length; i += 3, x++) {
				if (pixels[i] != 0 && pixels[i+1] != 0 && pixels[i+2] != 0) {
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
					crop = true;
				}
			}
		}
		
		setMinX(minX);
		setMinY(minX);
		setMaxX(minX);
		setMaxY(maxY);
		setCrop(crop);
		
		return null;
	}

	public String getName() {
		return "FindImageBounds";
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
		setParameter("Min. X", Integer.toString(minX));
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
		setParameter("Min. Y", Integer.toString(minY));
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
		setParameter("Max. X", Integer.toString(maxX));
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
		setParameter("Max. Y", Integer.toString(maxY));
	}

	public boolean isCrop() {
		return crop;
	}

	public void setCrop(boolean crop) {
		this.crop = crop;
		setParameter("Crop", Boolean.toString(crop));
	}
}
