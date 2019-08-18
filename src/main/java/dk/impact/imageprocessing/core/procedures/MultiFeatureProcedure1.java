package dk.impact.imageprocessing.core.procedures;

import dk.impact.imageprocessing.core.FeatureExtractorNode;
import dk.impact.imageprocessing.core.ImageProcessNode;
import dk.impact.imageprocessing.core.ImageProcessingParms;
import dk.impact.imageprocessing.core.featureextractors.ExtractDCTSubpixelsOp;
import dk.impact.imageprocessing.core.featureextractors.ExtractEdgeHistogramOp;
import dk.impact.imageprocessing.core.featureextractors.ExtractHistogramOp;
import dk.impact.imageprocessing.core.featureextractors.ExtractPixelsOp;
import dk.impact.imageprocessing.core.featureextractors.ExtractTextureFeaturesOp;
import dk.impact.imageprocessing.core.operators.ConvToIntFormatOp;
import dk.impact.imageprocessing.core.operators.ForwardDCTOp;
import dk.impact.imageprocessing.core.operators.GrayToRGBOp;
import dk.impact.imageprocessing.core.operators.RGBToGrayOp;
import dk.impact.imageprocessing.core.operators.RGBToLUVOp;
import dk.impact.imageprocessing.core.operators.SubsampleAverageAbsoluteOp;

public class MultiFeatureProcedure1 extends ImageProcessingProcedureAbstract {
	ImageProcessingParms	imParms;
	
	public MultiFeatureProcedure1(ImageProcessingParms imParms) {
		this.imParms = imParms;
	}
	
	@Override
	protected void setupNetwork(ImageProcessNode root) {
		ImageProcessNode		resize     = createProcessNode(new SubsampleAverageAbsoluteOp(imParms.imSize, imParms.imSize));
		ImageProcessNode		resizeTiny = createProcessNode(new SubsampleAverageAbsoluteOp(imParms.imTinySize, imParms.imTinySize));
		ImageProcessNode		grayToRGB  = createProcessNode(new GrayToRGBOp());
		ImageProcessNode		rgbToLUV   = createProcessNode(new RGBToLUVOp());
		ImageProcessNode		rgbToGray  = createProcessNode(new RGBToGrayOp());
		ImageProcessNode		intFormat  = createProcessNode(new ConvToIntFormatOp());
		ImageProcessNode		dct        = createProcessNode(new ForwardDCTOp());
//		ImageProcessNode		save       = createProcessNode(new ImageSaveOp("resized_" + (int) (Math.random() * 10000) + ".jpg"));

		FeatureExtractorNode	extractDCTLumChan      = createFeatureExtractorNode(new ExtractDCTSubpixelsOp(imParms.dctLumSize, imParms.dctLumSize, 0));
		FeatureExtractorNode	extractDCTChromChan1   = createFeatureExtractorNode(new ExtractDCTSubpixelsOp(imParms.dctChromSize, imParms.dctChromSize, 1));
		FeatureExtractorNode	extractDCTChromChan2   = createFeatureExtractorNode(new ExtractDCTSubpixelsOp(imParms.dctChromSize, imParms.dctChromSize, 2));
		FeatureExtractorNode	extractEdgeHistogram   = createFeatureExtractorNode(new ExtractEdgeHistogramOp(imParms.edgeThres));
		FeatureExtractorNode	extractTextureFeatures = createFeatureExtractorNode(new ExtractTextureFeaturesOp(imParms.textureMaxDelta, imParms.textureMaxDelta));
		FeatureExtractorNode	extractPixels          = createFeatureExtractorNode(new ExtractPixelsOp());
		FeatureExtractorNode	extractHistogram       = createFeatureExtractorNode(new ExtractHistogramOp(imParms.histogramBins));
//		FeatureExtractorNode	extractMoments    	   = createFeatureExtractorNode(new ExtractHuMomentsOp());
		
		root.addNode(resize);
		resize.addNode(grayToRGB);
		grayToRGB.addNode(rgbToLUV);
		grayToRGB.addNode(rgbToGray);
		rgbToLUV.addNode(resizeTiny);
//		resizeTiny.addNode(save);
		rgbToLUV.addNode(dct);
		rgbToGray.addNode(intFormat);
		
		rgbToLUV.addNode(extractHistogram);
		rgbToLUV.addNode(extractTextureFeatures);
		intFormat.addNode(extractEdgeHistogram);
		resizeTiny.addNode(extractPixels);
		dct.addNode(extractDCTLumChan);
		dct.addNode(extractDCTChromChan1);
		dct.addNode(extractDCTChromChan2);
	}
}
