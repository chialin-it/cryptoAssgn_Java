/*Part 6 Stream cipher
 *
 *CSCI361 Assignment 1
 *UOW ID: 6097881
 *NAME: LEE CHIA LIN
 */
 
import java.util.*;
import java.io.*;

public class Part6
{	
	private static String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static void main (String [] args)
	{		
		//part c
		System.out.println("Part c, encrypt WOLLONGONG with key = 3");
		String m = "WOLLONGONG";
		int k = 3;
		
		String cipher = encrypt(m,k);		
		System.out.println(cipher);		
		System.out.println("------------------------------------------------");
		
		//part d
		System.out.println("Part d, decrypt MQJJ with key = 3");
		cipher = "MQJJ";
		k = 3;
		
		m = decrypt(cipher,k);
		System.out.println(m);		
		System.out.println("------------------------------------------------");
	}
	
	//c = m + k (mod 26)
	public static String encrypt(String m, int k)
	{
		String cipher = "";
		
		//k1
		k = (k*k+1)%26;		
		if(k<0)
			k += 26;		
		
		int cIndex = (letter.indexOf(m.charAt(0)) + k)%26;
		cipher += letter.charAt(cIndex);
		
		//loop to get kn
		for(int n=2; n<=m.length(); n++)
		{
			k = (k*k+n)%26; 
			if(k<0)
				k += 26;
			
			//compute index c and add to ciphertext
			cIndex = (letter.indexOf(m.charAt(n-1)) + k)%26;
			cipher += letter.charAt(cIndex);
		}
		return cipher;
	}
	
	//(c - k) mod 26 = m
	public static String decrypt(String c, int k)
	{
		String m = "";
		
		//k1
		k = (k*k+1)%26;		
		if(k<0)
			k += 26;		
		
		int cIndex = (letter.indexOf(c.charAt(0)) - k)%26;
		if(cIndex<0)
				cIndex += 26;
		
		m += letter.charAt(cIndex);
		
		//loop to get kn
		for(int n=2; n<=c.length(); n++)
		{
			k = (k*k+n)%26; 
			if(k<0)
				k += 26;
			
			//compute index c and add to ciphertext
			cIndex = (letter.indexOf(c.charAt(n-1)) - k)%26;
			if(cIndex<0)
				cIndex += 26;
			
			m += letter.charAt(cIndex);
		}
		return m;
		
	}
}