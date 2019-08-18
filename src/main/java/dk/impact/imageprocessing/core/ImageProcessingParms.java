package dk.impact.imageprocessing.core;
import java.io.Serializable;
import java.util.Vector;


public class ImageProcessingParms implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer 	imSize;
	public Double 	edgeThres;
	public Integer 	dctLumSize;
	public Integer 	dctChromSize;
	public Integer 	imTinySize;
	public Integer 	textureMaxDelta;
	public Integer 	histogramBins;
	
	private final static String parmDescription[] = {
		"Image size",
		"Edge histogram threshold",
		"DCT luminance features (n x n)",
		"DCT chrominance features (n x n)",
		"Thumbnail image size",
		"Texture features max delta",
		"Histogram bins"
	};

	private Vector<Number> parmList;
	
	public ImageProcessingParms () {
		this(new String[]{"0", "0.0", "0", "0", "0", "0", "0"});			
	}
	
	public ImageProcessingParms (String strs[]) {
		int i = 0;
		
		this.imSize        		= Integer.parseInt(strs[i++]);
		this.edgeThres     		= Double.parseDouble(strs[i++]);
		this.dctLumSize    		= Integer.parseInt(strs[i++]);
		this.dctChromSize  		= Integer.parseInt(strs[i++]);
		this.imTinySize  		= Integer.parseInt(strs[i++]);
		this.textureMaxDelta  	= Integer.parseInt(strs[i++]);
		this.histogramBins  	= Integer.parseInt(strs[i++]);

		updateParmList();
	}
	
	/**
	 * Must be called if parameters are changed
	 */
	public void updateParmList() {
		parmList = new Vector<Number>();

		parmList.add(imSize);
		parmList.add(edgeThres);
		parmList.add(dctLumSize);
		parmList.add(dctChromSize);
		parmList.add(imTinySize);
		parmList.add(textureMaxDelta);
		parmList.add(histogramBins);
	}
	
	public int getNumberOfParms() {
		return parmList.size();
	}
	
	public String getParmDescription(int parmIdx) {
		return parmDescription[parmIdx];
	}

	public Number getParm(int parmIdx) {
		return parmList.elementAt(parmIdx);
	}

	public String getParmStringValue(int parmIdx) {
		return getParm(parmIdx).toString();
	}

	public String[] toStringArray() {
		String[] strArray = new String[getNumberOfParms()];
		
		for (int i = 0; i < getNumberOfParms(); i++) {
			strArray[i] = getParmStringValue(i);
		}
		
		return strArray;
	}
	
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		
		for (int i = 0; i < getNumberOfParms(); i++) {
			strBuf.append(getParmStringValue(i));
			
			if (i < (getNumberOfParms() - 1)) {
				strBuf.append(" ");
			}
		}
	
		return strBuf.toString();
	}
}
