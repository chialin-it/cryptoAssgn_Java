/*Part 1 Affine Cipher
 *
 *CSCI361 Assignment 1
 *UOW ID: 6097881
 *NAME: LEE CHIA LIN
 */

import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.lang.Math; 
import java.math.*;

public class Part1
{	
	private static Scanner userInput = new Scanner (System.in);
	private static Scanner input;
	private static Formatter output;
	private static final String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static void main(String [] args)
	{		
		System.out.println("Do you have the key? Y/N: ");
		String userKey = userInput.nextLine();
		System.out.println();
		
		//if user enters other value, request for input again
		while(!userKey.equals("Y") && !userKey.equals("N"))
		{
			System.out.println();
			System.out.println("Please enter Y/N only: ");
			userKey = userInput.nextLine();
		}
		
		if(userKey.equals("Y"))
		{
			knownKey();
		}
		else
		{			
			String key = unknownKey();		
			System.out.println(key);
		}
	}
		
	//user knows the keys
	public static void knownKey()
	{
		System.out.println("Please enter key, encrypt or decrypt, input file name and output file name: ");
		System.out.println("SAMPLE: 3 8 encrypt fileIn.txt fileOut.txt");
		
		int a = userInput.nextInt();
		int b = userInput.nextInt();
		String flag = userInput.next();
		String fileIn = userInput.next();
		String fileOut = userInput.next();
		
		if(BigInteger.valueOf(a).gcd(BigInteger.valueOf(26)).equals(BigInteger.ONE))
		{
			if(flag.equals("encrypt") || flag.equals("decrypt"))
			{		
				try
				{
					input = new Scanner (Paths.get(fileIn));
					output = new Formatter (fileOut);
					
					while (input.hasNextLine()) 
					{
						//read from input
						String s = input.nextLine();
						
						if(flag.equals("encrypt"))
							s = encrypt(s, a, b);
							
						else if(flag.equals("decrypt"))
							s = decrypt(s, a, b);
						
						//save to output
						output.format ("%s%n", s);
					}
					
					input.close();
					output.close();
					System.out.println("Successfully saved to " + fileOut);
				}
				catch (IOException e)
				{
					System.err.println ("Error opening file.");
					System.exit (1);
				}
			}
			else
			{
				System.out.println("Please indicate encrypt OR decrypt");
			}
		}
		else
		{
			System.out.println("Invalid key!");
		}
	}
	
	public static String encrypt(String input,int a,int b) 
	{
       String str = "";
       
       for (int i = 0; i < input.length(); i++) 
       {
           char M = input.charAt(i);
           
           if (Character.isLetter(M)) 
           {          	
           		// aM + b % 26
				if(Character.isLowerCase(M))
				{
					M = Character.toUpperCase(M);
					int temp = (a * (letter.indexOf(M) + b)) % 26;
					M = Character.toLowerCase(letter.charAt(temp));
				}					
				else			
				{					
					int temp = (a * (letter.indexOf(M) + b)) % 26;
					M = letter.charAt(temp);
				}				
			}
           str +=M;
       }
       return str;
   }
   
   public static String decrypt(String input,int a,int b) 
   {
       String str = "";
       int inverseA = 0;
       int inverse = 0;
       
       // find 1 by using modular inverse
       // 11 * i mod 26 == 1
       // if total == 1, then i is the inverse modular
       while(true)
       {
         inverse = (a * inverseA) % 26;
            if(inverse == 1)
               break;
         inverseA++;
       }
       
       for (int i = 0; i < input.length(); i++) 
       {
           char C = input.charAt(i);
        
           if (Character.isLetter(C)) 
           {
			   // inverseA *(C-b) mod 26
				if(Character.isLowerCase(C))
				{
					C = Character.toUpperCase(C);
					int temp = (inverseA * (letter.indexOf(C) - b)) % 26;
					
					if(temp < 0)
						temp += 26;	
					
					C = Character.toLowerCase(letter.charAt(temp));
					
					//C = (char)((inverseA * (C - b) % 26) + 'A');
					//C = Character.toLowerCase(C);
				}
				else
				{					
					int temp = (inverseA * (letter.indexOf(C) - b)) % 26;
					
					if(temp < 0)
						temp += 26;	
					
					C = letter.charAt(temp);
				}
				
           }
           str += C;
       }
       return str;
   }
   
   	//user does not know the key
   public static String unknownKey()
   {
		System.out.println("Please input file name to be decrypted and output file name: ");
   		System.out.println("SAMPLE: fileIn.txt fileOut.txt");
		
		String fileIn = userInput.next();
		String fileOut = userInput.next();
		String key = "Unable to find the keys";
			
		for(int a=0; a<26; a++)
		{
			if(BigInteger.valueOf(a).gcd(BigInteger.valueOf(26)).equals(BigInteger.ONE))
			{			
				for(int b=0; b<26; b++)
				{
					try
					{
						input = new Scanner (Paths.get(fileIn));
						output = new Formatter (fileOut);
					
						while (input.hasNextLine()) 
						{
							//read from input
							String s = input.nextLine();
							s = decrypt(s, a, b);
							
							//save to output
							output.format ("%s%n", s);
						}
						input.close();
						output.close();	
						
						//read the output to check if decryption might be correct
						input = new Scanner (Paths.get(fileOut));
						
						while (input.hasNextLine()) 
						{
							//read from input
							String s = input.nextLine();
							
							//check if the most common 3 letter word " the " exist in the decryption 
							if(s.toLowerCase().indexOf(" the ") >= 0)
							{		
								key = "Successfully decrypted with keys (" + a + ", " + b + ")";						
								return key;
							}							
						}
						output.close();
						
					}
					catch (IOException e)
					{
						System.err.println ("Error opening file.");
						System.exit (1);
					}				
				}	
			}				
		}
		return key;
	}   
}