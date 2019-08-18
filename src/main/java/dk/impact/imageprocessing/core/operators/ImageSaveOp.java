package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;

import javax.media.jai.JAI;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class ImageSaveOp extends ImageOperatorAbstract {
	private	String	fileName;
	
	public ImageSaveOp(String fileName) {
		setFileName(fileName);
	}
	 
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		JAI.create("filestore", input, getFileName(), "JPEG"); 
		return null;
	}

	public String getName() {
		return "ImageSave";
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setParameter("File name", fileName);
	}
}
