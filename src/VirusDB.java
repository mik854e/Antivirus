import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class VirusDB implements Serializable {
	
	private Hashtable<String, Integer> virusHT;
	private Hashtable<String, Integer> benignHT;
	private ArrayList<String> files;
	private int num;
	static final int N = 4;
	private File dir;

	/* 
	 * Default Constructor
	 */
	public VirusDB() {
			virusHT = new Hashtable<String, Integer>();
			benignHT = new Hashtable<String, Integer>();
			files = new ArrayList<String>();
			num = 0;
			dir = null;
	}
	
	/* 
	 * Loads a serialized VirusDB by the name of "virusdb.ser"
	 * in the directory specified by f.
	 */
	public static VirusDB loadDB(File f) {
		try {
	         FileInputStream fileIn = new FileInputStream(f.getAbsolutePath() + "/virusdb.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         VirusDB db = (VirusDB) in.readObject();
	         in.close();
	         fileIn.close();
	         //System.out.printf("Serialized data loaded from " + f.getAbsolutePath() + "/virusdb.ser");
	         return db;
	    }
		catch(IOException i) {
	         // i.printStackTrace();
	         return null;
	    }
		catch(ClassNotFoundException c) {
	         System.out.println("VirusDB class not found");
	         c.printStackTrace();
	         return null;
	    }
	}
	
	/*
	 * Saves the current VirusDB to the directory specified by
	 * this.dir. It will be serialized to the file virusdb.ser.
	 */
	public void saveDB() {
		if (dir == null) {
			return;
		}
		try {
			 String fname = dir.getAbsolutePath() + "/virusdb.ser";
			 File dbfile = new File(fname);
			 if (dbfile.exists())
				 dbfile.delete();
	         FileOutputStream fileOut = new FileOutputStream(fname);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this);
	         out.close();
	         fileOut.close();
	         //System.out.printf("Serialized data is saved in " + fname);
	    }
		catch (FileNotFoundException e) {
	         System.out.println("Not a valid directory.");
		}
		catch (IOException i) {
	          i.printStackTrace();
	    }
	}
	
	/*
	 * Used for training the program.
	 *
	 * 0 indicates a virus 
	 * 1 indicates a benign file
	 * 
	 * Scans all of the files in the directory f and updates the 
	 * respective hash tables.
	 */
	public void addProgram(File f, int n) {
		Hashtable<String, Integer> ht;
		if (n == 0) {
			ht = virusHT;
		}
		else {
			ht = benignHT;
		}
		String[] flist = f.list();
		String fname;
		
		for (String s: flist) {
			fname = f.getAbsolutePath() + "/" + s;
			if (files.contains(s)) {
				//System.out.println(s + " already added.");
				continue;
			}
			Scanner sc = null;
			
			try {
				sc = new Scanner(new File(fname));
			} 
			catch (FileNotFoundException e) {
				//System.out.println("File not found: " + f.getName());
				continue;
			}
			String ngram;
			while (sc.hasNext()) {
				ngram = sc.next();
			    Integer count = ht.get(ngram);
			    if (count == null) {
			    	count = 0;
			    }
			    
			    count++;
		    	//System.out.println(ngram + ": " + count);
		    	ht.put(ngram, count);
			}
	    	files.add(s);
	    	num++;
	    	//System.out.println(s + " added to DB.");
			sc.close();
		}
	}
	
	/*
	 * Computes the probability that f is a virus. Method for
	 * computation is described in README.txt.
	 */
	public double scanFile(File f) {
		Scanner sc = null;
		
		try {
			sc = new Scanner(f);
		} 
		catch (FileNotFoundException e) {
			//System.out.println("File not found: " + f.getName());
			return 0.0;
		}
		
		String ngram;
		double gamma = 0.0;
		double pi = 0.0;
		while (sc.hasNext()) {
			ngram = sc.next();
		    Integer numvirus = virusHT.get(ngram);
		    Integer numbenign = benignHT.get(ngram);
		    if (numvirus == null && numbenign == null) {
		    	continue;
		    }
		    else if (numvirus == null) {
		    	pi = 0.05;
		    }
		    else if (numbenign == null) {
		    	pi = 0.99;
		    }
		    else {
		    	pi = numvirus / (double) (numvirus + numbenign);
		    }
		    
		    gamma += Math.log(pi/(1-pi));
		}
		sc.close();
		return gamma;
	}
	
	public File getDir() {
		return dir;
	}
	
	public void setDir(File f) {
		dir = f;
	}
	
	public int getNum() {
		return num;
	}
}
