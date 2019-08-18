package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.JAI;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class ImageLoadOp extends ImageOperatorAbstract {
	private	String			fileName;
	private SeekableStream 	stream;
	
	public ImageLoadOp(String fileName) {
		setFileName(fileName);
	}
	 
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		try {
			stream = new FileSeekableStream(getFileName());
			ParameterBlock param = new ParameterBlock(); 
			param.add(stream); 

			return JAI.create("stream", param);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public void closeStream() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 			
	}
	
	public String getName() {
		return "ImageLoad";
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setParameter("File name", fileName);
	}
}
