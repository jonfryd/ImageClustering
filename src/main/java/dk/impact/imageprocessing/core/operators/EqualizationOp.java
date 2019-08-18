package dk.impact.imageprocessing.core.operators;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.MatchCDFDescriptor;

import dk.impact.imageprocessing.core.ImageOperatorAbstract;

public class EqualizationOp extends ImageOperatorAbstract {
	public enum MatchingType {
		Equalize,
		Normalize
	}
	
	private MatchingType	matchingType;

	public EqualizationOp() {
		setMatchingType(MatchingType.Equalize);
	}
			
	public EqualizationOp(MatchingType matchingType) {
		setMatchingType(matchingType);
	}
	
    private static RenderedImage createMatchCdfEqualizeImage(RenderedImage sourceImage) {
        int numBands = sourceImage.getSampleModel().getNumBands();
        final Histogram histogram = createHistogram(sourceImage);

        // Create an equalization CDF.
        float[][] eqCDF = new float[numBands][];
        for (int b = 0; b < numBands; b++) {
            int binCount = histogram.getNumBins(b);
            eqCDF[b] = new float[binCount];
            for (int i = 0; i < binCount; i++) {
                eqCDF[b][i] = (float) (i + 1) / (float) binCount;
            }
        }
        return MatchCDFDescriptor.create(sourceImage, eqCDF, null);
    }

    private static Histogram createHistogram(RenderedImage sourceImage) {
        int binCount = 256;

        // Get the band count.
        int numBands = sourceImage.getSampleModel().getNumBands();

        // Allocate histogram memory.
        int[] numBins = new int[numBands];
        double[] lowValue = new double[numBands];
        double[] highValue = new double[numBands];
        for(int i = 0; i < numBands; i++) {
            numBins[i] = binCount;
            lowValue[i] = 0.0;
            highValue[i] = 255.0;
        }

        // Create the histogram op.
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(sourceImage);
	    pb.add(null); // Specify ROI No ROI
	    pb.add(1); // Sampling
	    pb.add(1); // periods
	    pb.add(numBins); // Specify the histogram	
	    pb.add(lowValue); // Specify the histogram
	    pb.add(highValue); // Specify the histogram
		
	    PlanarImage histImage = (PlanarImage) JAI.create("histogram", pb, null);

        // Retrieve the histogram.
        Histogram histogram = (Histogram)histImage.getProperty("histogram");

        ((PlanarImage) sourceImage).setProperty("histogram", histogram);
        if (sourceImage instanceof RenderedOp) {
            RenderedOp renderedOp = (RenderedOp) sourceImage;
            renderedOp.getRendering().setProperty("histogram", histogram);
        }	        
        
        return histogram;
    }

    private static RenderedImage createMatchCdfNormalizeImage(RenderedImage sourceImage) {
        final double dev = 256.0;
        int numBands = sourceImage.getSampleModel().getNumBands();
        final double[] means = new double[numBands];
        Arrays.fill(means, 0.5 * dev);
        final double[] stdDevs = new double[numBands];
        Arrays.fill(stdDevs, 0.25 * dev);
        return createHistogramNormalizedImage(sourceImage, means, stdDevs);
    }

    private static RenderedImage createHistogramNormalizedImage(RenderedImage sourceImage, double[] mean, double[] stdDev) {
        int numBands = sourceImage.getSampleModel().getNumBands();

        final Histogram histogram = createHistogram(sourceImage);

        float[][] normCDF = new float[numBands][];
        for (int b = 0; b < numBands; b++) {
            int binCount = histogram.getNumBins(b);
            normCDF[b] = new float[binCount];
            double mu = mean[b];
            double twoSigmaSquared = 2.0 * stdDev[b] * stdDev[b];
            normCDF[b][0] = (float) Math.exp(-mu * mu / twoSigmaSquared);
            for (int i = 1; i < binCount; i++) {
                double deviation = i - mu;
                normCDF[b][i] = normCDF[b][i - 1] +
                        (float) Math.exp(-deviation * deviation / twoSigmaSquared);
            }
        }

        for (int b = 0; b < numBands; b++) {
            int binCount = histogram.getNumBins(b);
            double CDFnormLast = normCDF[b][binCount - 1];
            for (int i = 0; i < binCount; i++) {
                normCDF[b][i] /= CDFnormLast;
            }
        }

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(sourceImage);
		pb.add(normCDF);
        
        return JAI.create("matchcdf", pb, null);
    }

	
	@Override
	protected RenderedImage doProcessing(RenderedImage input) {
		if (matchingType == MatchingType.Equalize) {
			return createMatchCdfEqualizeImage(input);
		}
		
		return createMatchCdfNormalizeImage(input);
	}

	public String getName() {
		return "Equalization";
	}
	
	public MatchingType getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(MatchingType matchingType) {
		this.matchingType = matchingType;
		setParameter("MatchingType", matchingType.toString());
	}
}
