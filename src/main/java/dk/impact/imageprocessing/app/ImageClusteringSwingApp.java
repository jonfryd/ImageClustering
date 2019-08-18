package dk.impact.imageprocessing.app;

import fr.inria.axis.clustering.dsom.RunDSOM;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ImageClusteringSwingApp extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel jPanel1;
	private JButton select;
	private JCheckBox renameBack;
	private JCheckBox imageProcessing;
	private JLabel jLabel2;
	private JCheckBox rename;
	private JCheckBox classify;
	private JCheckBox dissimilarity;
	private JTextField relRadiusTextField;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private JTextField clustersTextField;
	private JPanel jPanel4;
	private JLabel jLabel3;
	private JButton process;
	private JPanel jPanel3;
	private JButton reset;
	private JPanel jPanel2;
	private JLabel jLabel1;
	private JTextField directory;
	
	private JFrame	  consoleFrame;
	private JTextArea txtConsole;
	
	private Thread doWork;

	static DataFlavor urlFlavor, uriListFlavor;
	static {
		try { 
			urlFlavor = new DataFlavor ("application/x-java-url; class=java.net.URL"); 
			uriListFlavor = new DataFlavor ("text/uri-list; class=java.lang.String");
		} catch (ClassNotFoundException cnfe) { 
			cnfe.printStackTrace( );
		}
	}
	
	private class MyTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] flavors) {
			for (int i=0; i<flavors.length; i++) {
				if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
					return true;
				}
			}
			
			return super.canImport(comp, flavors);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(JComponent comp, Transferable t) {
			DataFlavor[] flavors = t.getTransferDataFlavors();
			for (int i=0; i<flavors.length; i++) {
				if (t.isDataFlavorSupported(urlFlavor)) {
					try {
	                    URL url = (URL)t.getTransferData(urlFlavor);
						JTextComponent textComp = (JTextComponent)comp;
						
						try {
							File file = new File(url.toURI().getPath());
							textComp.setText("");
							if (file.isFile()) {
								textComp.replaceSelection(file.getParent());
							}
							else {
								textComp.replaceSelection(file.getPath());
							}
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
					try {
						List<File> fileList = (List<File>)t.getTransferData(flavors[i]);
						for (int j=0; j<fileList.size(); j++) {
							File file = (File)fileList.get(j);
							JTextComponent textComp = (JTextComponent)comp;
							textComp.setText("");
							
							if (file.isFile()) {
								textComp.replaceSelection(file.getParent());
							}
							else {
								textComp.replaceSelection(file.getPath());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
	                try{
	                    String textString = (String)t.getTransferData(DataFlavor.stringFlavor);
						JTextComponent textComp = (JTextComponent)comp;
	                    textComp.replaceSelection(textString);
	                }catch(Exception e){
	                    System.out.println("Exception in importData");
	                }

				}
				return true;
			}
			return super.importData(comp, t);
		}
	}


	/*
	* @(#) TextAreaOutputStream.java
	*
	*/


	/**
	* An output stream that writes its output to a javax.swing.JTextArea
	* control.
	*
	* @author Ranganath Kini
	* @see javax.swing.JTextArea
	*/
	class TextAreaOutputStream extends OutputStream {
		private JTextArea textControl;
	
		/**
		* Creates a new instance of TextAreaOutputStream which writes
		* to the specified instance of javax.swing.JTextArea control.
		*
		* @param control A reference to the javax.swing.JTextArea
		* control to which the output must be redirected
		* to.
		*/
		public TextAreaOutputStream( JTextArea control ) {
			textControl = control;
		}
	
		/**
		* Writes the specified byte as a character to the
		* javax.swing.JTextArea.
		*
		* @param b The byte to be written as character to the
		* JTextArea.
		*/
		@Override		
		public void write( int b ) throws IOException {
			// append the data as character to the JTextArea control
			updateTextPane(String.valueOf( ( byte )b ));
		}
		
		@Override  
		public void write(byte[] b, int off, int len) throws IOException {  
			updateTextPane(new String(b, off, len));
		}  
		   
		@Override  
		public void write(byte[] b) throws IOException {  
			write(b, 0, b.length);  
		}  		
		
		private void updateTextPane(final String text) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Document doc = textControl.getDocument();
					try {
						doc.insertString(doc.getLength(), text, null);
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
					textControl.setCaretPosition(doc.getLength() - 1);
				}
			});
		}  		
	}	
	
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ImageClusteringSwingApp inst = new ImageClusteringSwingApp();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public ImageClusteringSwingApp() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				this.setTitle("Image Clustering");
       			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       			this.setResizable(false);
       			{
       				jPanel1 = new JPanel();
       				getContentPane().add(jPanel1, BorderLayout.WEST);
       				jPanel1.setPreferredSize(new java.awt.Dimension(574, 255));
       				jPanel1.setLayout(null);
       				{
       					process = new JButton();
       					jPanel1.add(process);
       					process.setText("Process");
       					process.setBounds(268, 231, 295, 34);
       					process.addMouseListener(new MouseAdapter() {
       						public void mouseClicked(MouseEvent evt) {
       							processMouseClicked(evt);
       						}
       					});
       					
       					consoleFrame = new JFrame("Console");
       					txtConsole = new JTextArea();
       					JScrollPane jsp = new JScrollPane(txtConsole);
       					consoleFrame.add(jsp);
       					PrintStream out = new PrintStream(new TextAreaOutputStream(txtConsole));
       					System.setOut(out);
       					System.setErr(out);
       				}
       				{
       					jPanel2 = new JPanel();
       					jPanel1.add(jPanel2);
       					jPanel2.setBounds(12, 12, 550, 43);
       					jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
       					{
       						jLabel1 = new JLabel();
       						jPanel2.add(jLabel1);
       						jLabel1.setText("Directory");
       						jLabel1.setBounds(12, 8, 57, 14);
       					}
       					{
       						directory = new JTextField();
       						jPanel2.add(directory);
       						directory.setTransferHandler(new MyTransferHandler());
       						directory.setBounds(81, 5, 210, 21);
       						directory.setPreferredSize(new java.awt.Dimension(380, 21));
       					}
       					{
       						select = new JButton();
       						jPanel2.add(select);
       						select.setText("Select");
       						select.setBounds(362, 5, 82, 21);
       						select.addMouseListener(new MouseAdapter() {
       							public void mouseClicked(MouseEvent evt) {
       								selectMouseClicked(evt);
       							}
       						});
       					}
       				}
       				{
       					reset = new JButton();
       					jPanel1.add(reset);
       					reset.setText("Reset");
       					reset.setBounds(12, 231, 100, 34);
       					reset.addMouseListener(new MouseAdapter() {
       						public void mouseClicked(MouseEvent evt) {
       							resetMouseClicked(evt);
       						}
       					});
       				}
       				{
       					jPanel3 = new JPanel();
       					jPanel1.add(jPanel3);
       					jPanel3.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
       					jPanel3.setBounds(408, 90, 154, 125);
       					{
       						renameBack = new JCheckBox();
       						jPanel3.add(renameBack);
       						renameBack.setText("Rename back");
       						renameBack.setPreferredSize(new java.awt.Dimension(135, 18));
       						renameBack.setSelected(true);
       					}
       					{
       						imageProcessing = new JCheckBox();
       						jPanel3.add(imageProcessing);
       						imageProcessing.setText("Process images");
       						imageProcessing.setPreferredSize(new java.awt.Dimension(135, 18));
       						imageProcessing.setSize(135, 18);
       						imageProcessing.setSelected(true);
       					}
       					{
       						dissimilarity = new JCheckBox();
       						jPanel3.add(dissimilarity);
       						dissimilarity.setText("Dissimilarity");
       						dissimilarity.setPreferredSize(new java.awt.Dimension(135, 18));
       						dissimilarity.setSize(135, 18);
       						dissimilarity.setOpaque(true);
       						dissimilarity.setSelected(true);
       					}
       					{
       						classify = new JCheckBox();
       						jPanel3.add(classify);
       						classify.setText("Classify");
       						classify.setPreferredSize(new java.awt.Dimension(135, 18));
       						classify.setSelected(true);
       					}
       					{
       						rename = new JCheckBox();
       						jPanel3.add(rename);
       						rename.setText("Rename");
       						rename.setPreferredSize(new java.awt.Dimension(135, 18));
       						rename.setSelected(true);
       					}
       				}
       				{
       					jLabel2 = new JLabel();
       					jPanel1.add(jLabel2);
       					jLabel2.setText("Execute these steps");
       					jLabel2.setBounds(408, 70, 154, 14);
       				}
       				{
       					jLabel3 = new JLabel();
       					jPanel1.add(jLabel3);
       					jLabel3.setText("Classification settings");
       					jLabel3.setBounds(221, 71, 154, 14);
       				}
       				{
       					jPanel4 = new JPanel();
       					jPanel1.add(jPanel4);
       					jPanel4.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
       					jPanel4.setBounds(221, 90, 154, 125);
       					{
       						jLabel4 = new JLabel();
       						jPanel4.add(jLabel4);
       						jLabel4.setText("Clusters");
       						jLabel4.setBounds(221, 71, 154, 14);
       						jLabel4.setPreferredSize(new java.awt.Dimension(68, 14));
       					}
       					{
       						clustersTextField = new JTextField();
       						jPanel4.add(clustersTextField);
       						clustersTextField.setPreferredSize(new java.awt.Dimension(60, 21));
       						clustersTextField.setText("100");
       					}
       					{
       						jLabel5 = new JLabel();
       						jPanel4.add(jLabel5);
       						jLabel5.setText("Radius");
       						jLabel5.setBounds(221, 71, 154, 14);
       						jLabel5.setPreferredSize(new java.awt.Dimension(68, 14));
       					}
       					{
       						relRadiusTextField = new JTextField();
       						jPanel4.add(relRadiusTextField);
       						relRadiusTextField.setPreferredSize(new java.awt.Dimension(60, 21));
       						relRadiusTextField.setText("0.08");
       					}
       				}

       			}
				
			}
			this.setSize(582, 310);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void selectMouseClicked(MouseEvent evt) {
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File dir = new File(directory.getText());
		fc.setCurrentDirectory(dir);

		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			directory.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void processMouseClicked(MouseEvent evt) {
		doProcessing();

		consoleFrame.setVisible(true);
		center(consoleFrame);
	}
	
	private void doProcessing() {
		if ((doWork == null) || !doWork.isAlive()) {
			try {
				txtConsole.getDocument().remove(0, txtConsole.getDocument().getLength());
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			doWork = new Thread() {
				@Override
				public void run() {
					String	baseDir = directory.getText();
					
					if (!baseDir.equals("")) {
						if (renameBack.isSelected()) {
							RenameBack.main(new String[] {baseDir});
						}
						if (imageProcessing.isSelected()) {
							CalcImagesFeaturesLocal.main(new String[] {baseDir});
						}
						if (dissimilarity.isSelected()) {
							CalcDisMatrix.main(new String[] {baseDir});
						}
						if (classify.isSelected()) {
							int clusters          = Integer.parseInt(clustersTextField.getText());
							double relativeRadius = Double.parseDouble(relRadiusTextField.getText());						
							
							System.out.println(clusters + " " + relativeRadius);
							
							File distMat = new File(baseDir, "dismat.txt");
							File output = new File(baseDir, "output.txt");
							
							String dsomArgs[] = new String[]{"-clust", String.valueOf((int) Math.ceil(Math.sqrt(clusters))), "-relativeradius", String.valueOf(relativeRadius), "-data", distMat.getAbsolutePath(), "-outpart", output.getAbsolutePath()};
							try {
								RunDSOM.main(dsomArgs);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}						
						}
						if (rename.isSelected()) {
							Rename.main(new String[] {baseDir});
						}
						
						System.out.println("\nAll done.");
					}
					else {
						System.out.println("Need a directory containing JPEG images.");
					}
				}
			};
			
			doWork.start();
		}
	}

	private static void center(JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int w = Math.max(bounds.width/2, Math.min(frame.getWidth(), bounds.width));
        int h = Math.max(bounds.height/2, Math.min(frame.getHeight(), bounds.height));
        int x = center.x - w/2, y = center.y - h/2;
        frame.setBounds(x, y, w, h);
        if (w == bounds.width && h == bounds.height) {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        frame.validate();
    }
	
	private void resetMouseClicked(MouseEvent evt) {
		directory.setText("");
	}
}
