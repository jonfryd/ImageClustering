package dk.impact.imageprocessing.app;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.SocketFactory;

import dk.impact.imageprocessing.core.FeatureCollection;
import dk.impact.imageprocessing.core.ImageProcessingParms;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class CalcImageFeaturesDelegator {
	private String 	baseDir;
	private String 	hostsAndPorts[];

	private ConcurrentHashMap<String, Vector<String>> 	fileToResultMap = new ConcurrentHashMap<String, Vector<String>>();
	
	Vector<String> getResult(String filename) {
		return fileToResultMap.get(filename);
	}
	
	void storeResult(String filename, Vector<String> results) {
		if (results == null) {
			results = new Vector<String>();
		}
		
		fileToResultMap.put(filename, results);
	}

    private ImageProcessingParms imageProcessingParms;
    
    public CalcImageFeaturesDelegator(String baseDir, ImageProcessingParms imParms, String[] hostsAndPorts) {
    	this.baseDir = baseDir;
    	this.imageProcessingParms = imParms;
    	this.hostsAndPorts = hostsAndPorts;
    }

    class ProcessImagesThread extends Thread {
    	private Vector<String>		images;
    	private volatile boolean	stop;

        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        
    	ProcessImagesThread(ImageProcessingParms imParms, String host, int port) throws UnknownHostException, IOException {
    		images = new Vector<String>();
    		stop = false;
			socket = SocketFactory.getDefault().createSocket(host, port);
	    	outputStream = new ObjectOutputStream(socket.getOutputStream());
	    	inputStream = new ObjectInputStream(socket.getInputStream());
	    	outputStream.writeObject(imageProcessingParms);
    	}
    	
    	public void cleanUp() throws IOException {
    		outputStream.writeObject("");
    		inputStream.close();
    		outputStream.close();
    		socket.close();
    	}
    	
    	@SuppressWarnings("unchecked")
		public void run() {
			String 	image;
	   	
    		while(true) {
				synchronized (this) {
    				image = null;

        			if (getQueueLength() < 1) {
        				if (stop) {
            				break;
            			}    				
        				
        				try {
    						wait();
    					} catch (InterruptedException e) {
    					}
        			} else {
        				image = images.remove(0);
        			}
				}

    			if (image == null) {
    				continue;
    			}
    			
				//System.out.println("begin - " + getQueueLength() + " - " + this);

				//mt.processImage(image);
    			
    			try {
					String filename = (String) inputStream.readObject();
	    			Vector<String> results = (Vector<String>) inputStream.readObject();
	    			
	    			storeResult(filename, results);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				//System.out.println("end - " + getQueueLength() + " - " + this);
    		}
    	}
    	
    	public synchronized void stopRunning() {
    		stop = true;
       		notify();    		
    	}
    	
    	public synchronized void addImage(String image) throws IOException {
    		images.add(image);
			outputStream.writeObject(image);
    		notify();
    	}
    	
    	public synchronized int getQueueLength() {
			return images.size();
		}
    }

    
    public void processDir() {
		try {
		    File thisDir = new File(baseDir, ".");
		    final File[] files = thisDir.listFiles(new FilenameFilter() {        
				    public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jpg");
			    } });		
		    
		    ProcessImagesThread threadPool[] = new ProcessImagesThread[hostsAndPorts.length / 2];
		    
		    for (int i = 0; i < threadPool.length; i++) {
			    threadPool[i] = new ProcessImagesThread(imageProcessingParms, hostsAndPorts[i * 2], Integer.parseInt(hostsAndPorts[i * 2 + 1]));
			    threadPool[i].setName("ProcessThread" + i);
			    threadPool[i].start();
		    }
		    
		    Random rnd = new Random();
		    
		    System.out.println("Delegating files to workers.");
		    
			long t1 = System.currentTimeMillis();
		    
		    for(int i = 0; i < files.length; ) {
				boolean	queuedImage = false;
		    	String 	file = files[i].getName();

				for (int j = 0; j < threadPool.length; j++) {
					int k = rnd.nextInt(threadPool.length);
					
					if (threadPool[k].getQueueLength() < 20) {
						threadPool[k].addImage(file);
						queuedImage = true;

						i++;

						if (((i % 50) == 0) || (i == files.length)) {
							System.out.println(i + "/" + files.length + " delegated.");
						}
						
						break;
					}
				}
				
				if (!queuedImage) {
					Thread.sleep(200);
				}
		    }	

		    for (int i = 0; i < threadPool.length; i++) {
			    threadPool[i].stopRunning();
		    }
		    
		    for (int i = 0; i < threadPool.length; i++) {
			    threadPool[i].join();
			    threadPool[i].cleanUp();
		    }		
		    
			long t2 = System.currentTimeMillis();
			System.out.println("Image processing took " + (t2 - t1) / 1000 + " seconds.");
			
		    // write output
			PrintWriter outFiles = new PrintWriter (new BufferedWriter (new FileWriter (new File(baseDir, "img_names.txt"))));

			Vector<Vector<String>>	stringVectors = new Vector<Vector<String>>();
			
		    for(int i = 0; i < files.length; i++) {
		    	String 	key = files[i].getName();
		    	String 	file = files[i].getName();

		    	Vector<String> res = getResult(key);
		    	
		    	if (res != null) {
		    		if (res.size() > 0) {
				    	outFiles.println(file);
			    		stringVectors.add(res);
		    		}
		    	} else {
		    		System.out.println("This is bad!! Result not found for file key: " + key);
		    		System.exit(-1);
		    	}
		    }

			FeatureCollection fc = FeatureCollection.createFeatureCollection(stringVectors);
			fc.write(baseDir, "img_features.txt");
					    
		    outFiles.close ();
		}
		catch(Exception e) {
        	System.out.println(e);
		}
    }  
    
    public static void main (String args[]) {
    	String	hostsAndPorts[] = {"localhost", "9000"};
    	
        if (args == null) {
        	System.out.println("Missing arguments");
        	System.exit(-1);
        }

        System.out.println("CalcImageFeatures");
        
        try {
        	ImageProcessingParms imParms = new ImageProcessingParms(args);
        	
            for (int i = 0; i < args.length; i++) {
            	System.out.println(i + " - " + imParms.getParmDescription(i) + ": " + imParms.getParmStringValue(i));
            }

            CalcImageFeaturesDelegator ht = new CalcImageFeaturesDelegator(null, imParms, hostsAndPorts);
            
            ht.processDir();            
            
        } catch(Exception e) {
        	System.out.println(e);
        }
    }
}
