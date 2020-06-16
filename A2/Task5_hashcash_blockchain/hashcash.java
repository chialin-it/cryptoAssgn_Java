/*CSCI 361 Cryptography
 *Assignment 2 
 *Q5 Proof-of-work
 *
 *UOW Student ID: 6097881
 *Name: Lee Chia Lin
 */

import java.util.*;
import java.io.*; 
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.math.BigInteger;
import java.nio.file.Paths;

public class hashcash
{
	private static Formatter dataOut;
	private static Scanner dataIn;
	private static Scanner input = new Scanner(System.in);
    
    public static void main(String[] args) throws Exception
    {
    	while(true)	
		{
			System.out.println("1. Add new data to existing ledger");
			System.out.println("2. Verify data");
			System.out.println("3. Quit");		
			
			System.out.print("Choose any function (1-3): ");			
			String user = input.next();
			System.out.println();
							
			if(user.equals("1"))				
    			addNewData();
    			
			else if(user.equals("2"))
				verifyData();
				
			else if(user.equals("3"))
				break;		
				
			else
				System.out.println("Invalid choice! Please enter choice 1-3");
		
			System.out.println("---------------------------------------------------------------");
		}				
    }
    
    static void addNewData() throws Exception
    {
    	//request for user input
  		System.out.print("Enter limit: ");
  		int limit = input.nextInt();
  		
  		System.out.print("Enter input filename: ");
  		String dataFile = input.next();
  		
  		System.out.print("Enter ledger filename: ");
  		String ledgerFile = input.next();
  		
  		System.out.print("Enter output filename (to be created): ");
  		String outputFile = input.next();
  		System.out.println();
  		
  		//array to store strings of new data, ledger, nonce
  		ArrayList<String> stringArr = readFile(dataFile);
  		ArrayList<String> ledgerArr = readFile(ledgerFile);
  
  		ArrayList<String> nonceArr = new ArrayList<String>();
  		ArrayList<String> hexArr = new ArrayList<String>();
  
  		//previous hash
  		String prevHash = ledgerArr.get(ledgerArr.size()-1).substring(0,32);
  		
  		//set varibles for the loops
  		String theLimit = "";
  		for(int i=0; i<limit; i++)
  			theLimit += "0";
  		
  		String checkLimit = "";
  		boolean foundLimit = false;
  		
  		for(int i=0; i<stringArr.size(); i++)
  		{
  			//generate key
  			SecretKey skey = getKey(stringArr.get(i));
  			System.out.println(stringArr.get(i));
  			
  			while(!foundLimit)
  			{
  				// generate nonce = IV.
		        byte[] iv = new byte[16];
		        SecureRandom random = new SecureRandom();
		        random.nextBytes(iv);
  				
  				byte [] textByte = stringArr.get(i).getBytes();
  				byte [] text128bit = new byte[16];	
  				
  				//first 128bit of new data String
  				for(int k=0; k<16; k++)
  				{
  					if((k)<stringArr.get(i).length())
  						text128bit[k] = textByte[k];
  					else
  						text128bit[k] = 0;
  				}
  				
	  			byte [] hashResult = hash(text128bit, skey, iv);
  				
  				//remaining 128bit
  				for(int j=1; j<Math.ceil(stringArr.get(i).length()/16.0); j++)
  				{
	  				for(int k=0; k<16; k++)
	  				{
	  					if((j+k)<stringArr.get(i).length())
	  						text128bit[k] = textByte[j+k];
	  					else
	  						text128bit[k] = 0;
	  				}	  				
	  				hashResult = hash(text128bit, skey, hashResult);	
	  			}
  				
  				//hash with prevHash
  				hashResult = hash(hashResult, skey, hexToByte(prevHash));
  				
  				//convery to hex
  				String hexResult = byteToHex(hashResult);
  				
  				//check if the first number of limit is '0'
  				for(int a=0; a<limit; a++)
  					checkLimit += hexResult.charAt(a);
  				
  				if(checkLimit.equals(theLimit))
  				{	
  					foundLimit = true;
  					hexArr.add(hexResult);  					
        			nonceArr.add(byteToHex(iv));
        			
        			System.out.println("Hash: " + hexResult);
        			System.out.println("Nonce: " + byteToHex(iv) + "\n");
  				}
  			
  				checkLimit = "";
  			}  			
  			foundLimit = false;
  		}
  		
  		writeOutput(outputFile, stringArr, nonceArr);
  		addToLedger(ledgerFile, stringArr, hexArr);
    }
    
    static void verifyData() throws Exception
    {
    	System.out.println("****IMPORTANT NOTE****: This program does not take into consideration of String that appears more than once in the ledger.");
    	System.out.println("The ledger will compare with the output starting from the 1st string found in ledger that = 1st string of output.");
    	
    	//request for user input  		
  		System.out.print("Enter output filename: ");
  		String outputFile = input.next();
  		
  		System.out.print("Enter ledger filename: ");
  		String ledgerFile = input.next();
  		System.out.println();
  		
  		//array to store strings of new data, ledger, nonce
  		ArrayList<String> stringArr = readFile(outputFile);
  		ArrayList<String> ledgerArr = readFile(ledgerFile);
  		
  		//set varibles for loop
  		int outputIndex = ledgerArr.indexOf(stringArr.get(0));
  		
  		if(outputIndex >= 0)
  		{
	  		String prevHash = ledgerArr.get(outputIndex-1).substring(0,32);
	  		boolean verify = true;
	  		
	  		for(int i=0; i<stringArr.size() && verify; i=i+2)
	  		{
	  			System.out.println(stringArr.get(i));
	  			
	  			//generate key
	  			SecretKey skey = getKey(stringArr.get(i));
	  			
				//get nonce
		        byte[] nonce = hexToByte(stringArr.get(i+1));
		        System.out.println("Nonce: " + stringArr.get(i+1));
				
				byte [] textByte = stringArr.get(i).getBytes();
				byte [] text128bit = new byte[16];	
				
				//first 128bit of new data String
				for(int k=0; k<16; k++)
				{
					if((k)<stringArr.get(i).length())
						text128bit[k] = textByte[k];
					else
						text128bit[k] = 0;
				}
				
	  			byte [] hashResult = hash(text128bit, skey, nonce);
				
				//remaining 128bit
				for(int j=1; j<Math.ceil(stringArr.get(i).length()/16.0); j++)
				{
	  				for(int k=0; k<16; k++)
	  				{
	  					if((j+k)<stringArr.get(i).length())
	  						text128bit[k] = textByte[j+k];
	  					else
	  						text128bit[k] = 0;
	  				}
	  				hashResult = hash(text128bit, skey, hashResult);	
	  			}
				
				//hash with prevHash
				hashResult = hash(hashResult, skey, hexToByte(prevHash));
				
				//convery to hex
				String hexResult = byteToHex(hashResult);
				System.out.println("Hash: " + hexResult);
				
				if(!ledgerArr.get(outputIndex + i + 1).equals(hexResult))
					verify = false;
				
				System.out.println("Verify result: " + verify + "\n");	
			}
		}
		else
			System.out.println("String not found in ledger");
    }
    
    static ArrayList<String> readFile(String file)
	{
		ArrayList<String> stringArr = new ArrayList<String>();
		
		try
		{
			dataIn = new Scanner (Paths.get(file));
			String s = dataIn.nextLine();
			String[] sArr = s.split(", ");
			
			for(String str: sArr)
				stringArr.add(str);
			
			dataIn.close();			
		}
		catch (IOException e)
		{
			System.err.println ("Error in loading " + file);
			System.exit (1);
		}
		
		return stringArr;	
	}
	
	static SecretKey getKey(String s)
	{
		//first 128bit of string
		String key = "";
		for(int i=0; i<16; i++)
			key += s.charAt(i);
		
		// decode the base64 encoded string
		byte[] decodedKey = key.getBytes();
		
		// rebuild key using SecretKeySpec
		SecretKey skey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
		return skey;
	}
	
	static void displayOutput(ArrayList<String> stringArr, ArrayList<String> nonceArr)
	{
		for(int i=0; i<stringArr.size(); i++) 
    	{
    		System.out.println(stringArr.get(i) + "\t" + nonceArr.get(i));
    	}											
	}
	
	static void writeOutput(String filename, ArrayList<String> stringArr, ArrayList<String> nonceArr)
	{
		try
		{
			dataOut = new Formatter (filename);
			
	    	for (int i=0; i<stringArr.size(); i++)
	    	{
	    		dataOut.format (stringArr.get(i) + ", ");
	    		dataOut.format (nonceArr.get(i));
	    		
	    		if(i != stringArr.size()-1)
	    			dataOut.format (", ");
	    	}
										
			if (dataOut != null)
			{
				dataOut.close ();
				System.out.println ("output.txt saved.");
			}
		}
		catch (FileNotFoundException ex)
		{
			System.err.println ("Error in openinging the file");
			System.exit (1);
		}
		catch (SecurityException ex)
		{
			System.err.println ("Write permissin denied");
			System.exit (1);
		}	
	}
    
    //add string and hex of new data to ledger
    static void addToLedger(String filename, ArrayList<String> stringArr, ArrayList<String> hexArr)
	{
		try
		{
			//append to file
			FileWriter f = new FileWriter(filename, true);
			dataOut = new Formatter (f);
			dataOut.format (", ");
			
	    	for (int i=0; i<stringArr.size(); i++)
	    	{
	    		dataOut.format (stringArr.get(i) + ", ");
	    		dataOut.format (hexArr.get(i));
	    		
	    		if(i != stringArr.size()-1)
	    			dataOut.format (", ");
	    	}
										
			if (dataOut != null)
			{
				dataOut.close ();
				System.out.println (filename + " saved.");
			}
		}
		catch (FileNotFoundException ex)
		{
			System.err.println ("Error in openinging the file");
			System.exit (1);
		}
		catch(IOException ex)
		{
			System.err.println ("Error! IOException");
			System.exit (1);	
		}
		catch (SecurityException ex)
		{
			System.err.println ("Write permissin denied");
			System.exit (1);
		}	
	}
    
 	//Hash Function AES CBC source: https://www.javainterviewpoint.com/aes-encryption-and-decryption/
    static byte[] hash (byte[] plaintext, SecretKey key, byte[] IV ) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        
        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        
        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        
        byte [] cipher128bit = new byte [16];
        for(int i=0; i<16; i++)
        	cipher128bit[i] = cipherText[i];
        
        return cipher128bit;
    }
    
    static String byteToHex (byte [] text)
    {
    	StringBuilder sb = new StringBuilder();
	    for (byte b : text) {
	        sb.append(String.format("%02x", b));
	    }
	    return sb.toString();
    }
    
    static byte[] hexToByte(String hex) 
    {
    	int size = hex.length()/2;
    	
	    // Make sure the byte [] is always the correct length.
	    byte[] key = new byte[size];
	    
	    for (int i = 0; i < hex.length() && (i / 2) < size; i++) 
	    {
	        // Pull out the hex value of the character.
	        int nybble = Character.digit(hex.charAt(hex.length() - 1 - i), 16);
	        if ((i & 1) != 0) {
	            // When i is odd we shift left 4.
	            nybble = nybble << 4;
	        }
	        // Use OR to avoid sign issues.
	        key[size - 1 - (i / 2)] |= (byte) nybble;
	    }
	    return key;
	}
	
	static void displayByte(byte [] bytes)
	{
		System.out.print("\nByte: ");
		
		for (byte b : bytes) 
    	{
    		System.out.print(b + ", ");
    	}	
	}
}