package dk.impact.imageprocessing.core.featureextractors;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractTextureFeaturesOp extends FeatureExtractorAbstract {
	private	int		offsetX;
	private int		offsetY;
	
	public ExtractTextureFeaturesOp(int offsetX, int offsetY) {
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}
	
	private double[] getTextureFeatures(Raster ip, int band, int dx, int dy) {
	    int pixel1[] = {0, 0, 0};
	    int pixel2[] = {0, 0, 0};
	    int sumHist[] = new int[511];
	    int diffHist[] = new int[511];
	    double sumHistNorm[] = new double[511];
	    double diffHistNorm[] = new double[511];
	    double result[] = new double[5];
	    int val1, val2;
	    
	    Rectangle r = ip.getBounds();
	    
	    r.width  -= dx;
	    r.height -= dy;
	    
	    for (int y=r.y; y<(r.y+r.height); y++) {
	    	for (int x=r.x; x<(r.x+r.width); x++) {
		        ip.getPixel(x, y, pixel1);
		        ip.getPixel(x + dx, y + dy, pixel2);
		        
		        val1 = pixel1[band];
		        val2 = pixel2[band];
	           
		        sumHist[val1 + val2]++;
		        diffHist[val1 - val2 + 255]++;
	    	}
	    }
	    
	    // no. pixels
	    double pixels = r.width * r.height;
	    
	    for (int i = 0; i < 511; i++) {
	    	sumHistNorm[i] = (double) sumHist[i] / pixels;
	    	diffHistNorm[i] = (double) diffHist[i] / pixels;
	    }
	    
	    //mean
	    for (int i = 0; i < 511; i++) {
	    	result[0] += (double) i * sumHistNorm[i]; 
	    }
	    result[0] *= 0.5;
	    //result[0] = 0;
	    
	    // TODO: fix me?
	    //contrast
	    for (int j = -255; j < 255; j++) {
	    	result[1] += (double) (j * j) * diffHistNorm[j + 255]; 
	    }
	    //result[1] = 0;
	    
	    //homogenity
	    for (int j = -255; j < 255; j++) {
	    	result[2] += 1.0 / (1.0 + (double) (j * j)) * diffHistNorm[j + 255]; 
	    }
	    
	    //entropy
	    for (int i = 0; i < 511; i++) {
	    	if (sumHist[i] > 0) {
		    	result[3] -= sumHistNorm[i] * Math.log(sumHistNorm[i]); 
	    	}
	    }		    
	    for (int j = -255; j < 255; j++) {
	    	if (diffHist[j + 255] > 0) {
		    	result[3] -= diffHistNorm[j + 255] * Math.log(diffHistNorm[j + 255]); 
	    	}
	    }
	    
	    //energy
	    for (int i = 0; i < 511; i++) {
	    	result[4] += sumHistNorm[i] * sumHistNorm[i]; 
	    }
	    for (int j = -255; j < 255; j++) {
	    	result[4] += diffHistNorm[j + 255] * diffHistNorm[j + 255]; 
	    }
	    
	    
	    return result;
	}	
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		for (int dy = 0; dy < getOffsetY(); dy++) {
			for (int dx = 0; dx < getOffsetX(); dx++) {
				if ((dx > 0) || (dy > 0)) {
					for (int band = 0; band < input.getData().getNumBands(); band++) {
						double ft[] = getTextureFeatures(input.getData(), band, dx, dy);
						
						for ( int i = 0; i < ft.length; i++ ) {
							addExtractedFeature(ft[i]);
						}				
					}
				}
			}
		}
		
		return null;
	}

	public String getName() {
		return "ExtractTextureFeatures";
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
		setParameter("Offset X", Float.toString(offsetX));
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		setParameter("Offset Y", Float.toString(offsetY));
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.EUCLIDEAN;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.RANK_NORMALIZATION;
	}
}
