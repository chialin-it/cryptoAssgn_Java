/*CSCI 361 Cryptography
 *Assignment 2 
 *Q3 Hash Function Collision Finding 
 *
 *UOW Student ID: 6097881
 *Name: Lee Chia Lin
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.math.BigInteger;
 
public class collision
{
	//sha1 Source: http://www.sha1-online.com/sha1-java/
	//returns a string hex
	static String sha1(String input) throws NoSuchAlgorithmException 
	{
	    MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	    byte[] result = mDigest.digest(input.getBytes());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < result.length; i++) {
	        sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    //convert to string hex
		BigInteger hex = new BigInteger(sb.toString(), 16);		
	    return hex.toString(16);
	}
	
	//ssha1 - returns only first 36 bits
	static String ssha1(String input) throws NoSuchAlgorithmException 
	{
	    String s = sha1(input);  
	    return s.substring(0,9);
	}
		
	static int [] mapCollision(String s) throws NoSuchAlgorithmException
	{
		Random rand = new Random(); 
		int [] collisionData = new int [3];	//stores x, x', number of trials
		HashMap<String, Integer> colMap = new HashMap<>(); 
          
        String s1 = s + "0 dollars";
        colMap.put(ssha1(s1), 0); 		
        
        s1 = s + "1 dollars";
		collisionData[1] = 1;
				
		System.out.println("Finding collision... ");
		collisionData[2]++;	//count number of trials
				
		while(!colMap.containsKey(ssha1(s1)))
		{				
			colMap.put(ssha1(s1), collisionData[1]);
			
			collisionData[1]++;		//next x'
			collisionData[2]++;		//count number of trials			
							
			//set s1 for next loop
			s1 = s + collisionData[1] + " dollars";				
			System.out.print("\r" + collisionData[2]);	
		}
		System.out.println(" number of trials");
		
		collisionData[0] = colMap.get(ssha1(s1));				
		return collisionData; 	
	}
	
	public static void main (String [] args) throws Exception 
	{
		try
    	{	    	
    		String s = "The Cat-In-The-Hat owes CHIA LIN ";
    		int [] collisionData = mapCollision(s);
    		
    		String s1 = s + collisionData[0] + " dollars";
			String s2 = s + collisionData[1] + " dollars";
			
			System.out.println("\ns1: " + s1);
			System.out.println("s2: " + s2);			
			System.out.println();
			System.out.println("ssha1(s1): " + ssha1(s1));			
			System.out.println("ssha1(s2): " + ssha1(s2)); 	
			
			//System.out.println("sha1(s1): " + sha1(s1));			
			//System.out.println("sha1(s2): " + sha1(s2)); 	
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println("Error in hashing.");
		}
	}
}