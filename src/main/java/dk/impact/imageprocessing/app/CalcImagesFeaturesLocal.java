package dk.impact.imageprocessing.app;

import java.io.IOException;

import dk.impact.imageprocessing.core.ImageProcessingParms;


public class CalcImagesFeaturesLocal {
    protected static final int DEFAULT_PORT = 9000;

    private CalcImageFeaturesWorker	worker;
    private ImageProcessingParms 	imParms;
    private int						port;
    private String					baseDir;

    public CalcImagesFeaturesLocal(String baseDir, int port, ImageProcessingParms imParms)
    {
    	this.baseDir = baseDir;
    	this.port = port;
    	this.imParms = imParms;
    	this.worker = null;
    }
    
    public void execute() {
    	Thread	workerThread = new Thread() {
    		@Override
    		public void run() {
                try {
        	        worker = new CalcImageFeaturesWorker(baseDir, port);
                }
        		catch(IOException e) {
        			System.out.println(e);
        			System.exit(-1);
        		}

    	        try {
    		        worker.process();
    		        worker.cleanUpThreads();
    	        }
    			catch(Exception e) {
    				System.out.println(e);
    				return;
    			}        
    		}
    	};
    	
    	workerThread.start();
    	
    	while((worker == null) || !worker.isWorkerSocketBound()) {
    		try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
        try {
            for (int i = 0; i < imParms.getNumberOfParms(); i++) {
            	System.out.println(i + " - " + imParms.getParmDescription(i) + ": " + imParms.getParmStringValue(i));
            }

            CalcImageFeaturesDelegator ht = new CalcImageFeaturesDelegator(baseDir, imParms, new String[]{"localhost", String.valueOf(port)});
            ht.processDir();            
        } catch(Exception e) {
        	System.out.println(e);
        } 		
        
        try {
			worker.closeAcceptor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
    }
    
	public static void main (String args[]) {
    	String	baseDir = null;
		int		port = DEFAULT_PORT;
		
		if (args != null && args.length > 0) {
			baseDir = args[0];
		}		

		ImageProcessingParms imParms = new ImageProcessingParms();

		imParms.imSize 		 	= 250; 
		imParms.edgeThres 	 	= 100.0;
		imParms.dctLumSize   	= 2;
		imParms.dctChromSize	= 3;
		imParms.imTinySize		= 6;
		imParms.histogramBins	= 10;
		imParms.textureMaxDelta	= 2;
		
		imParms.updateParmList();
		
		CalcImagesFeaturesLocal instance = new CalcImagesFeaturesLocal(baseDir, port, imParms);
		instance.execute();
    }
}
