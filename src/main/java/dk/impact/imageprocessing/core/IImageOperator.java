package dk.impact.imageprocessing.core;
import java.awt.image.RenderedImage;
import java.util.Map;

public interface IImageOperator {
	// Process input image and (maybe) return a new image
	// null is acceptable for input and return value
	RenderedImage invoke(RenderedImage input);

	// Return computation time
	long getDeltaMillis();
	
	// Return textual description
	String getName();
	
	// Return textual representation for debugging/logging purposes	
	String getInfo();
	
	// Return parameter string map (key,value), cannot return null
	Map<String,String> getParameters();
}
