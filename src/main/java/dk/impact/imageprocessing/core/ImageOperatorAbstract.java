package dk.impact.imageprocessing.core;
import java.awt.image.RenderedImage;
import java.util.Map;
import java.util.TreeMap;


public abstract class ImageOperatorAbstract implements IImageOperator {
	private		long				deltaMillis;
	private		Map<String,String>	parameters;
	
	protected ImageOperatorAbstract() {
		setDeltaMillis(0);
		parameters = new TreeMap<String,String>();
	}	

	final public RenderedImage invoke(RenderedImage input) {
		long	t1 = System.currentTimeMillis();
		
		RenderedImage result = doProcessing(input);
		
		setDeltaMillis(System.currentTimeMillis() - t1);
		
		return result;
	}
	
	abstract protected RenderedImage doProcessing(RenderedImage input);
	
	public long getDeltaMillis() {
		return deltaMillis;
	}

	protected void setDeltaMillis(long deltaMillis) {
		this.deltaMillis = deltaMillis;
	}	
	
	public Map<String,String> getParameters() {
		return parameters;
	}
	
	protected void setParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	public String getInfo() {
		StringBuffer	sb = new StringBuffer();
		
		sb.append("[" + getName() + "]: ");
		
		for (String key : parameters.keySet()) {
			sb.append("<" + key + "=" + parameters.get(key) + ">");
		}
		
		return sb.toString();
	}

	public String toString() {
		return getInfo();
	}
}
