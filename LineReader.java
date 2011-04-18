/*
 * Created on 06-Nov-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package camxnet;


import java.io.*;
import java.util.*;

/**
 * @author Gareth Gibbs, Ian Leung
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LineReader
{
	File file;
	StringTokenizer token;
	BufferedReader bufRead;
	
	public LineReader(File file)
	{
		this.file = file;
		try
		{
			FileReader read = new FileReader(file);
			bufRead = new BufferedReader(read);
			token = new StringTokenizer("temp");
		}
		catch(FileNotFoundException notFound)
		{
			System.out.println("Couldn't find the file");
		}
	}
	
	public String nextToken() throws IOException
	{
//		if(!token.hasMoreTokens())
//			token = new StringTokenizer(nextLine(),",");
		
		return nextLine();
		
		
	}
	
	public String newLineToken() throws IOException
	{
		token = new StringTokenizer(nextLine(),",");
		return token.nextToken();
	}
	
	public static String firstToken(String str)
	{
		StringTokenizer temp = new StringTokenizer(str,",");
		return temp.nextToken();
	}
	
	public static int getNumberOfTokens(String str)
	{		
		StringTokenizer tempTokens = new StringTokenizer(str, ",");
		return tempTokens.countTokens();
	}
	
	public String nextLine() throws IOException
	{
		return bufRead.readLine();		
	}
	
	public String nextLine(FileReader read) throws IOException
	{
		BufferedReader temp = new BufferedReader(read);
		return temp.readLine();
	}
	
	public void close() throws IOException
	{
		bufRead.close();
	}
}
