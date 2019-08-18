package dk.impact.imageprocessing.core;

import java.util.Vector;

public class BasicFeatureResult implements IFeatureResult {
	String							source;
	DistanceMeasuring.MethodId		dmi;
	FeatureNormalization.MethodId	nmi;
	Vector<Double>					data;
	
	BasicFeatureResult(String source, DistanceMeasuring.MethodId dmi, FeatureNormalization.MethodId nmi, Vector<Double> result) {
		this.source = source;
		this.dmi = dmi;
		this.nmi = nmi;
		this.data = result;
	}
	
	public String getSource() {
		return source;
	}

	public DistanceMeasuring.MethodId getDistanceMeasureId() {
		return dmi;
	}

	public FeatureNormalization.MethodId getFeatureNormalizationMethodId() {
		return nmi;
	}

	public Vector<Double> getResult() {
		return data;
	}
}

