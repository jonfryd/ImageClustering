package dk.impact.imageprocessing.core.featureextractors;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import dk.impact.imageprocessing.core.DistanceMeasuring;
import dk.impact.imageprocessing.core.FeatureExtractorAbstract;
import dk.impact.imageprocessing.core.FeatureNormalization;

public class ExtractHuMomentsOp extends FeatureExtractorAbstract {
	static final double dCutoff = 0.0; // default cutoff (minimum) value for calcs
	//  (only values >= dCutoff are used)
	//  (use "0" to include all positive pixel values)
	static final double dFactor = 1.0; // default factor                              
	//  (multiplies pixel values prior to calculations)			
    static final double zero = 0.0;

    private double[] getImageMoments(Raster ip, int band) {
    	double m00 = zero;
    	double m10 = zero, m01 = zero;
    	double m20 = zero, m02 = zero, m11 = zero;
    	double m30 = zero, m03 = zero, m21 = zero, m12 = zero;
    	double m40 = zero, m04 = zero, m31 = zero, m13 = zero;
    	double xC=zero, yC=zero;
    	//double xxVar = zero, yyVar = zero, xyVar = zero;
    	//double xSkew = zero, ySkew = zero;
    	//double xKurt = zero, yKurt = zero;
    	//double orientation = zero, eccentricity = zero;
    	double currentPixel, xCoord, yCoord;
    	double hu_i1, hu_i2, hu_i3, hu_i4, hu_i5, hu_i6, hu_i7;
    	double pixel[] = {0.0, 0.0, 0.0};
    	double result[] = new double[7];

    	double pw = 1.0; 
    	double ph = 1.0; 
    	Rectangle r = ip.getBounds();

    	// Compute moments of order 0 & 1

    	for (int y=r.y; y<(r.y+r.height); y++) {
    		for (int x=r.x; x<(r.x+r.width); x++) {
    			xCoord = (x+0.5)*pw; //this pixel's X calibrated coord. (e.g. cm)
    			yCoord = (y+0.5)*ph; //this pixel's Y calibrated coord. (e.g. cm)
    			ip.getPixel(x, y, pixel);
    			currentPixel=pixel[band];//ip.getPixel(x, y, pixel);
    			currentPixel=currentPixel-dCutoff;
    			if (currentPixel < 0) currentPixel = zero; //gets rid of negative pixel values
    			currentPixel = dFactor*currentPixel;
    			/*0*/       m00+=currentPixel;
    			/*1*/       m10+=currentPixel*xCoord;
    			m01+=currentPixel*yCoord;
    		}
    	}

    	// Compute coordinates of centre of mass

    	xC = m10/m00;
    	yC = m01/m00;

    	// Compute moments of orders 2, 3, 4

    	// Reset index on "mask"
    	for (int y=r.y; y<(r.y+r.height); y++) {
    		for (int x=r.x; x<(r.x+r.width); x++) {
    			xCoord = (x+0.5)*pw; //this pixel's X calibrated coord. (e.g. cm)
    			yCoord = (y+0.5)*ph; //this pixel's Y calibrated coord. (e.g. cm)
    			//currentPixel=ip.getPixelValue(x,y);
    			ip.getPixel(x, y, pixel);
    			currentPixel=pixel[band];//ip.getPixel(x, y, pixel);
    			currentPixel=currentPixel-dCutoff;
    			if (currentPixel < 0) currentPixel = zero; //gets rid of negative pixel values
    			currentPixel = dFactor*currentPixel;
    			/*2*/       m20+=currentPixel*(xCoord-xC)*(xCoord-xC);
    			m02+=currentPixel*(yCoord-yC)*(yCoord-yC);
    			m11+=currentPixel*(xCoord-xC)*(yCoord-yC);

    			/*3*/       m30+=currentPixel*(xCoord-xC)*(xCoord-xC)*(xCoord-xC);
    			m03+=currentPixel*(yCoord-yC)*(yCoord-yC)*(yCoord-yC);
    			m21+=currentPixel*(xCoord-xC)*(xCoord-xC)*(yCoord-yC);
    			m12+=currentPixel*(xCoord-xC)*(yCoord-yC)*(yCoord-yC);

    			/*4*/       m40+=currentPixel*(xCoord-xC)*(xCoord-xC)*(xCoord-xC)*(xCoord-xC);
    			m04+=currentPixel*(yCoord-yC)*(yCoord-yC)*(yCoord-yC)*(yCoord-yC);
    			m31+=currentPixel*(xCoord-xC)*(xCoord-xC)*(xCoord-xC)*(yCoord-yC);
    			m13+=currentPixel*(xCoord-xC)*(yCoord-yC)*(yCoord-yC)*(yCoord-yC);
    		}
    	}

    	// Normalize 2nd moments & compute VARIANCE around centre of mass
    	//xxVar = m20/m00;
    	//yyVar = m02/m00;
    	//xyVar = m11/m00;

    	// Normalize 3rd moments & compute SKEWNESS (symmetry) around centre of mass
    	// source: Farrell et al, 1994, Water Resources Research, 30(11):3213-3223
    	//xSkew = m30 / (m00 * Math.pow(xxVar,(3.0/2.0)));
    	//ySkew = m03 / (m00 * Math.pow(yyVar,(3.0/2.0)));

    	// Normalize 4th moments & compute KURTOSIS (peakedness) around centre of mass
    	// source: Farrell et al, 1994, Water Resources Research, 30(11):3213-3223
    	//xKurt = m40 / (m00 * Math.pow(xxVar,2.0)) - 3.0;
    	//yKurt = m04 / (m00 * Math.pow(yyVar,2.0)) - 3.0;

    	// Compute Orientation and Eccentricity
    	// source: Awcock, G.J., 1995, "Applied Image Processing", pp. 162-165
    	//orientation = 0.5*Math.atan2((2.0*m11),(m20-m02));
    	//orientation = orientation*180./Math.PI; //convert from radians to degrees
    	//eccentricity = (Math.pow((m20-m02),2.0)+(4.0*m11*m11))/m00;


    	double s11, s02, s20, s21, s12, s30, s03;

    	s11 = m11 / (Math.pow (m00, (1.0 + (1.0 + 1.0) / 2.0)));
    	s02 = m02 / (Math.pow (m00, (1.0 + (0.0 + 2.0) / 2.0)));
    	s20 = m20 / (Math.pow (m00, (1.0 + (2.0 + 0.0) / 2.0)));
    	s21 = m21 / (Math.pow (m00, (1.0 + (2.0 + 1.0) / 2.0)));
    	s12 = m12 / (Math.pow (m00, (1.0 + (1.0 + 2.0) / 2.0)));
    	s30 = m30 / (Math.pow (m00, (1.0 + (3.0 + 0.0) / 2.0)));
    	s03 = m03 / (Math.pow (m00, (1.0 + (0.0 + 3.0) / 2.0)));

    	hu_i1 = s20 + s02;
    	hu_i2 = Math.pow(s20 - s02, 2.0) + Math.pow(2 * s11, 2.0);
    	hu_i3 = Math.pow(s30 - 3.0 * s12, 2.0) + Math.pow(2 * s21 - s03, 2.0);
    	hu_i4 = Math.pow(s30 + s12, 2.0) + Math.pow(s21 + s03, 2.0);
    	hu_i5 = (s30 - 3.0 * s12) * (s30 + s12) * (Math.pow(s30 + s12, 2.0) - 3.0 * Math.pow(s21 + s03, 2.0)) + 
    	(3.0 * s21 - s03) * (s21 + s03) * (3.0 * Math.pow(s30 + s12, 2.0) - Math.pow(s21 + s03, 2.0));
    	hu_i6 = (s20 - s02) * (Math.pow(s30 + s12, 2.0) - Math.pow(s21 + s03, 2.0)) + 4.0 * s11 * (s30 + s12) * (s21 + s03);
    	hu_i7 = (3.0 * s21 - s03) * (s30 + s12) * (Math.pow(s30 + s12, 2.0) - 3.0 * Math.pow(s21 + s03, 2.0)) - 
    	(s30 - 3.0 * s12) * (s21 + s03) * (3.0 * Math.pow(s30 + s12, 2.0) - Math.pow(s21 + s03, 2.0));

    	result[0] = hu_i1;		    
    	result[1] = hu_i2;		    
    	result[2] = hu_i3;		    
    	result[3] = hu_i4;		    
    	result[4] = hu_i5;		    
    	result[5] = hu_i6;		    
    	result[6] = hu_i7;		    

    	return result;
    }
	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		for (int b = 0; b < input.getData().getNumBands(); b++) {
			double moments1[] = getImageMoments(input.getData(), b);

			for ( int i = 0; i < moments1.length; i++ ) {
				addExtractedFeature(moments1[i]);
			}				
		}
		
		return null;
	}

	public String getName() {
		return "ExtractHuMoments";
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return DistanceMeasuring.MethodId.EUCLIDEAN;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return FeatureNormalization.MethodId.RANK_NORMALIZATION;
	}
}
