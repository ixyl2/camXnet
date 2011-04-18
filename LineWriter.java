/*
 * Created on 06-Nov-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package camxnet;


import java.io.*;

/**
 * @author Gareth Gibbs, Ian Leung
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LineWriter
{
	File file;
	BufferedWriter bufWrite;
	
	public LineWriter(File file, boolean append)
	{
		try
		{
			this.file = file;
			FileWriter write = new FileWriter(file, append);
			bufWrite = new BufferedWriter(write);			
		}
		catch(IOException e)
		{
			System.out.println("Can't write to file");
		}
		
	}

        public LineWriter(File file)
	{
		try
		{
			this.file = file;
			FileWriter write = new FileWriter(file);
			bufWrite = new BufferedWriter(write);
		}
		catch(IOException e)
		{
			System.out.println("Can't write to file");
		}

	}
	
	public void writeLine(String str) throws IOException
	{
		bufWrite.write(str);
		bufWrite.newLine();		
	}
        
        public void write(String str) throws IOException
	{
		bufWrite.write(str);	
	}
	
	public void writeLine(double[] doubles) throws IOException
	{
		bufWrite.write(Double.toString(doubles[0]) + "	" + Double.toString(doubles[1]));
		bufWrite.newLine();
	}
	
	public void writeLine() 
	{
		try{bufWrite.newLine();}catch(Exception e){;}
	}
	
	public void flush() throws IOException
	{
		bufWrite.flush();
	}
	
	public void close() throws IOException
	{
		bufWrite.close();
	}
}
