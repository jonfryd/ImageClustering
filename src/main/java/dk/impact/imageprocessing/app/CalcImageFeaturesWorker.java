package dk.impact.imageprocessing.app;

import dk.impact.imageprocessing.core.ImageProcessingParms;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Vector;

public class CalcImageFeaturesWorker {
	private String			baseDir;
	
	private ServerSocket	socket;
	
    private ProcessImagesThread threadPool[];
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
	
    public CalcImageFeaturesWorker(String baseDir, int port) throws IOException {
    	this.baseDir = baseDir;
    	
    	socket = new ServerSocket(port);
    	threadPool = new ProcessImagesThread[Runtime.getRuntime().availableProcessors()];
    	clientSocket = null;
    	inputStream = null;
    	outputStream = null;
    }
    
    public boolean isWorkerSocketBound() {
    	return socket.isBound();
    }
    
    public void closeAcceptor() throws IOException {
    	socket.close();
    }    
    
    private void storeResult(String filename, Vector<String> results) throws IOException {
		synchronized(outputStream) {
			outputStream.writeObject(filename);
			outputStream.writeObject(results);
		}
	}

    class ProcessImagesThread extends Thread {
    	private Vector<String>		images;
    	private volatile boolean	stop;

    	private ImageProcessing imgProcessing;
    	
    	ProcessImagesThread(ImageProcessingParms imParms) {
    		images = new Vector<String>();
    		stop = false;
    		imgProcessing = new ImageProcessing(imParms);
    	}
    	
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

    			Vector<String> results = null;
    			
    			try {
    				File file = new File(baseDir, image);
    				results = imgProcessing.processImage(file.getAbsolutePath());
				} catch (Exception e) {
					System.out.println("Exception thrown while processing image: " + image);
				} 
				
				try {
					// save results for file
					storeResult(image, results);
				} catch (IOException e) {
					System.out.println(e);
					break;
				}
    		}
    	}
    	
    	public synchronized void stopRunning() {
    		stop = true;
       		notify();    		
    	}
    	
    	public synchronized void addImage(String image) {
    		images.add(image);
    		notify();
    	}
    	
    	public synchronized int getQueueLength() {
			return images.size();
		}
    }

    public void cleanUpThreads() {
	    for (int i = 0; i < threadPool.length; i++) {
		    if (threadPool[i] != null) {
		    	threadPool[i].stopRunning();
		    }
	    }
	    
	    for (int i = 0; i < threadPool.length; i++) {
		    if (threadPool[i] != null) {
			    try {
					threadPool[i].join();
				} catch (InterruptedException e) {
				}
		    }
	    }	
	    
	    if (inputStream != null) {
    		try {
				inputStream.close();
			} catch (IOException e) {
			}
    		inputStream = null;
	    }
	    
	    if (outputStream != null) {
	    	synchronized (outputStream) {
		    	try {
					outputStream.close();
				} catch (IOException e) {
				}
		    	outputStream = null;
			}
	    }
	    
	    if (clientSocket != null) {
	    	try {
				clientSocket.close();
			} catch (IOException e) {
			}
			clientSocket = null;
	    }	    
    }
    
    public void process() throws IOException, ClassNotFoundException {
        // wait for connection
	    System.out.println("Waiting for connection on port " + socket.getLocalPort() + ".");        
        
    	clientSocket = socket.accept();
    	
    	//
	    System.out.println("Connection accepted.");

    	inputStream = new ObjectInputStream(clientSocket.getInputStream());
    	outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
    	
    	ImageProcessingParms imageProcessingParms = (ImageProcessingParms) inputStream.readObject();
    	
	    for (int i = 0; i < threadPool.length; i++) {
		    threadPool[i] = new ProcessImagesThread(imageProcessingParms);
		    threadPool[i].setName("ProcessThread" + i);
		    threadPool[i].start();
	    }
	    
	    Random rnd = new Random();
	    
	    System.out.println("Now performing multi-threaded image processing on " + threadPool.length + " threads.");

	    int i = 0;

    	//
	    System.out.println("Starting processing.");
    	clientSocket.setSoTimeout(20);
	    
	    while(true) {
	    	String 	file = null;
	    	
    		try {
		    	file = (String) inputStream.readObject();
	    	}
	    	catch (SocketTimeoutException ste) {
	    		// Read timed out
	    	}

	    	if (file != null) {
		    	boolean queuedImage = false;

		    	if (file.length() == 0) {
		    		break; // empty string received, then end processing
		    	}
		    	
		    	while(!queuedImage) {
			    	for (int j = 0; j < threadPool.length; j++) {
			    		if (!threadPool[j].isAlive()) {
			    		    System.out.println("Dead worker thread detected.");
			    			return;
			    		}
			    	}
			    	
					for (int j = 0; j < threadPool.length; j++) {
						int k = rnd.nextInt(threadPool.length);
						
						if (threadPool[k].getQueueLength() < 3) {
							threadPool[k].addImage(file);
							
							queuedImage = true;
							i++;

							if ((i % 50) == 0) {
								System.out.println(i + " queued.");
							}
							
							break;
						}
					}
					
					if (!queuedImage) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}				
		    	}
	    	}
	    }	
	    
	    System.out.println("No more input signal received.");
    }  
    
    public static void main (String args[]) {
        CalcImageFeaturesWorker ht = null;
        int	port;
    	
    	if ((args == null) || (args.length != 1)) {
        	System.out.println("Takes one argument: [port to listen on]");
			System.exit(-1);
        }

    	port = Integer.parseInt(args[0]);
    	
		//JAI.getDefaultInstance().getTileScheduler().setParallelism(4);
		//JAI.getDefaultInstance().getTileScheduler().setPrefetchParallelism(4);
		
        System.out.println("CalcImageFeaturesWorker configured to listen on port " + args[0]);

        try {
	        ht = new CalcImageFeaturesWorker(null, port);
        }
		catch(IOException e) {
			System.out.println(e);
			System.exit(-1);
		}

	    while(true) {
	        try {
		        ht.cleanUpThreads();
		        ht.process();
	        }
			catch(Exception e) {
				System.out.println(e);
			}        
	    }
    }
}
