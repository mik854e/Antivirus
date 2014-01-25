import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.*;


public class ScannerFrame extends JFrame {
	
	VirusDB db;
	JTextField directory;
	JTextField numfiles;
	JTextArea textArea;
	
	public ScannerFrame()
	{
		int FRAME_WIDTH = 600;
		int FRAME_HEIGHT = 320;
		
		initialize();
		setTitle("virusscanner.exe");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		db = new VirusDB();
		addWListener();
		this.pack();
		this.setResizable(false);
	}
	
	public void addWListener() {
		addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e){
				int confirmed = JOptionPane.showConfirmDialog(null, 
		                "Would you like to save before quitting?", "Confirm Quit", 
		                JOptionPane.YES_NO_CANCEL_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					if (db.getDir() != null) {
						db.saveDB();
					}
					else {
						textArea.append("Choose a directory in order to save.\n\n");
						return;
					}
				}
				else if (confirmed == JOptionPane.CANCEL_OPTION) {
					return;
				}
				dispose();
			} 
			public void windowClosed(WindowEvent e) {} 
			public void windowOpened(WindowEvent e) {} 
			public void windowIconified(WindowEvent e) {} 
			public void windowDeiconified(WindowEvent e) {} 
			public void windowActivated(WindowEvent e) {} 
			public void windowDeactivated(WindowEvent e) {}	
		});
	}
	
	public void initialize()
	{
		JPanel mainPanel = (JPanel) getContentPane();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(getTextPanel(), BorderLayout.EAST);
		mainPanel.add(getButtonPanel(), BorderLayout.WEST);
	}
	
	public JPanel getButtonPanel()
	{
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		// Choose Directory
		final JButton open = new JButton("Open a Directory");
		
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (e.getSource() == open) {
					int returnVal = fc.showOpenDialog(buttonPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File dir = fc.getSelectedFile();
						db.setDir(dir);
						directory.setText(dir.getAbsolutePath());
						VirusDB dbtmp = VirusDB.loadDB(dir);
						if (dbtmp != null) {
							db = dbtmp;
							numfiles.setText("" + db.getNum());
							textArea.append("Serialized data loaded from " + dir.getAbsolutePath() + "/virusdb.ser\n");
						}
					}
				}
			}
    	});
		buttonPanel.add(open);
		
		// Add Benign Files
		final JButton benign = new JButton("Learn Benign Files");
		benign.setPreferredSize(new Dimension(50, 80));
		benign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (e.getSource() == benign) {
					int returnVal = fc.showOpenDialog(buttonPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File dir = fc.getSelectedFile();
						textArea.append("Learning benign files from " + dir.getAbsolutePath()+ ".....\n");
						db.addProgram(dir, 1);
						numfiles.setText("" + db.getNum());
						textArea.append("Learning complete.\n\n");
					}
				}
			}
		});
		buttonPanel.add(benign);

		// Add Virus Files
		final JButton virus = new JButton("Learn Viruses");
		virus.setPreferredSize(new Dimension(50, 80));
		virus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (e.getSource() == virus) {
					int returnVal = fc.showOpenDialog(buttonPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File dir = fc.getSelectedFile();
						textArea.append("Learning virus files from " + dir.getAbsolutePath()+ ".....\n");
						textArea.repaint();
						db.addProgram(dir, 0);
						numfiles.setText("" + db.getNum());
						textArea.append("Learning complete.\n\n");
					}
				}
			}
		});
		buttonPanel.add(virus);

		// Clear Hash Tables
		JButton clear = new JButton("Clear Hash Tables");
		clear.setPreferredSize(new Dimension(50, 80));
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (db.getDir() != null) {
					//File f = new File(db.getDir().getAbsolutePath() + "/virusdb.ser");
					db = new VirusDB();
					numfiles.setText("0");
					directory.setText("");
					textArea.append("Working database cleared.\n\n");
				}
			}
		});
		buttonPanel.add(clear);	
		
		// Scan File
		final JButton scan = new JButton("Scan File");
		scan.setPreferredSize(new Dimension(50, 80));
		scan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if (e.getSource() == scan) {
					int returnVal = fc.showOpenDialog(buttonPanel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						textArea.append("Scanning " + f.getAbsolutePath() + ".....\n");
						double ratio = db.scanFile(f);
						DecimalFormat df = new DecimalFormat("0.000");
						textArea.append("Virus/Benign Ratio: " + df.format(ratio) + "\n");
						if (ratio > 0.0) {
							textArea.append("Prediction: Virus\n\n");
						}
						else if (ratio < 0.0) {
							textArea.append("Prediction: Benign\n\n");
						}
						else {
							textArea.append("Prediction: Inconclusive\n\n");
						}
					}
				}
			}
		});
		buttonPanel.add(scan);
		buttonPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		JPanel infoPanel = new JPanel();
		JTextArea infoText = new JTextArea(3, 5);
		infoText.setEditable(false);
		infoText.setText("Created By:\n    Michael Saltzman\n    mjs2287");
		infoText.setOpaque(false);
		infoPanel.add(infoText);
		
		JPanel r = new JPanel();
		r.setLayout(new BorderLayout());
		r.add(buttonPanel, BorderLayout.LINE_START);
		r.add(infoPanel, BorderLayout.PAGE_END);
		return r;
	}
	
	public JPanel getTextPanel() {
		JPanel textPanel = new JPanel();
		JPanel bottomPane = new JPanel();
		JPanel topPane = new JPanel();
		
		topPane.setBorder(BorderFactory.createTitledBorder("Info"));
		bottomPane.setBorder(BorderFactory.createTitledBorder("Output"));
		
		directory = new JTextField(40);
		directory.setEditable(false);
		JLabel directoryLabel = new JLabel("Directory: ");
        directoryLabel.setLabelFor(directory);
		topPane.add(directory);
		
		numfiles = new JTextField(5);
		numfiles.setEditable(false);
		JLabel numLabel = new JLabel("# Files: ");
        numLabel.setLabelFor(numfiles);
		topPane.add(numfiles);
		
		//Lay out the labels in a panel.
        JPanel labelPane = new JPanel(new GridLayout(0,1));
        labelPane.add(directoryLabel);
        labelPane.add(numLabel);

        //Layout the text fields in a panel.
        JPanel fieldPane = new JPanel(new GridLayout(0,1));
        fieldPane.add(directory);
        fieldPane.add(numfiles);
        
        topPane.setLayout(new BorderLayout());
        topPane.add(labelPane, BorderLayout.CENTER);
        topPane.add(fieldPane, BorderLayout.LINE_END);


		textArea = new JTextArea(20, 50);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		JScrollPane textScroll = new JScrollPane(textArea);
		bottomPane.add(textScroll);

		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                topPane, bottomPane
                );
		textPanel.add(splitPane);
		return textPanel;
	}
}
