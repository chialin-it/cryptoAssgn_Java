/*CSCI 361 Cryptography
 *Assignment 2 
 *Q1 RSA
 *
 *UOW Student ID: 6097881
 *Name: Lee Chia Lin
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.math.BigInteger;
import java.nio.file.Paths;
 
public class rsa
{
	private static SecureRandom rand = new SecureRandom();
	private static Formatter dataOut;
	private static Scanner dataIn;
	private static Scanner input = new Scanner(System.in);
	
	static void KeyGen(int bit)
	{
		BigInteger p = BigInteger.probablePrime(bit, rand);	
		BigInteger q = BigInteger.probablePrime(bit, rand);	
		
		BigInteger N = p.multiply(q);
		BigInteger pqMinus1 = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		
		//generate e and d
		BigInteger e = genRandE(pqMinus1);	
		BigInteger d = e.modInverse(pqMinus1);	
		
		writePk(N, e);
		writeSk(N, p, q, d);
	}
		
	static BigInteger genRandE(BigInteger modValue)
	{
		int bit = modValue.bitLength()-1;
		
		BigInteger e = BigInteger.probablePrime(bit, rand);		
		BigInteger checkGcd = modValue.gcd(e);
		
		//loop if gcd not 1
		while(!checkGcd.equals(BigInteger.ONE))
		{
			e = BigInteger.probablePrime(bit, rand);
			checkGcd = modValue.gcd(e);
		}
		return e;
	}
	
	static void writePk(BigInteger N, BigInteger e)
	{
		try
		{
			dataOut = new Formatter ("pk.txt");
			dataOut.format (N + ", " + e);
		
			if (dataOut != null)
			{
				dataOut.close ();
				System.out.println (N + ", " + e + " output to pk.txt.");
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
	
	static void writeSk(BigInteger N, BigInteger p, BigInteger q, BigInteger d)
	{
		try
		{
			dataOut = new Formatter ("sk.txt");
			dataOut.format (N + ", " + p + ", " + q + ", " + d);
										
			if (dataOut != null)
			{
				dataOut.close ();
				System.out.println (N + ", " + p + ", " + q + ", " + d + " output to sk.txt.");
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
	
	static String readFile(String file)
	{
		String s = "";
		
		try
		{
			dataIn = new Scanner (Paths.get(file));
			s = dataIn.nextLine();
			dataIn.close();			
		}
		catch (IOException e)
		{
			System.err.println ("Error in loading " + file);
			System.exit (1);
		}
		
		return s;	
	}
	
	static void Sign()
	{
		try
		{
			System.out.println("Taking values from sk.txt and mssg.txt...");
			
			//read from sk.txt for values of N, p, q, d
			String sk = readFile("sk.txt");
			System.out.println("sk: " + sk);
			String[] skArray = sk.split(", ");
			
			BigInteger N = new BigInteger(skArray[0]);
			BigInteger d = new BigInteger(skArray[3]);			
			
			//read mssg.txt
			String stringM = readFile("mssg.txt");
			System.out.println("M: " + stringM);
			BigInteger M = new BigInteger(stringM);
			
			if(N.compareTo(M) < 0)
			{
				System.out.println("Error. M must be a positive integer smaller than N.");	
				return;
			}
			else
			{
				//calculate Signature S and write to sig.txt
				BigInteger S = M.modPow(d, N);
				writeSig(S);
			}
		}
		catch (NumberFormatException ex)
		{
			System.out.println("Error. Format of mssg.txt is wrong.");
		}
	}
	
	//write result to sig.txt
	static void writeSig(BigInteger sig)
	{
		try
		{
			dataOut = new Formatter ("sig.txt");
			dataOut.format (sig.toString());
										
			if (dataOut != null)
			{
				dataOut.close ();
				System.out.println (sig + " output to sig.txt.");
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
	
	static boolean Verify(String stringM)
	{
		boolean result = false;
		
		try
		{
			BigInteger M = new BigInteger(stringM);
			
			System.out.println("Taking values from pk.txt and sig.txt...");
			
			//read from pk.txt for values of N, e
			String pk = readFile("pk.txt");
			System.out.println("pk: " + pk);
			String[] pkArray = pk.split(", ");
			
			BigInteger N = new BigInteger(pkArray[0]);
			BigInteger e = new BigInteger(pkArray[1]);
			
			//read from pk.txt for value of Signature S
			String sig = readFile("sig.txt");
			System.out.println("sig: " + sig);
			BigInteger S = new BigInteger(sig);
			
			if(N.compareTo(M) < 0)
				result = false;
				
			else if(M.compareTo(S.modPow(e, N)) == 0)
				result = true;
			
			System.out.println("\nM = " + M + " = " + S.modPow(e, N));	
		}
		catch (NumberFormatException ex)
		{
			System.out.println("Error. Format of message M is wrong.");
		}
		
		return result;
	}
	
	static void menu()
	{
		while(true)	
		{
			System.out.println("1. KeyGen");
			System.out.println("2. Sign");
			System.out.println("3. Verify");		
			
			System.out.print("Choose any function (1-3 or 'q' to quit): ");			
			String user = input.next();
			System.out.println();
			
			if(user.equals("q"))
				return;
				
			else if(user.equals("1"))
			{
				System.out.print("Enter number of bits for p and q (up to 32): ");
				int bit = input.nextInt();
				
				while(bit > 32)
				{
					System.out.println("\nError! Too big!"); 
					System.out.print("Enter a smaller number of bits for p and q (up to 32): ");
					bit = input.nextInt();
				}
				
				KeyGen(bit);
				System.out.println("\nKeyGen function completed.");
			}
			else if(user.equals("2"))
			{
				Sign();		
				System.out.println("\nSign function completed.");
			}
			else if(user.equals("3"))
			{
				System.out.print("Enter M: ");
				boolean v = Verify(input.next());
				System.out.println("Result: " + v);
						
			}
			else
				System.out.println("Invalid choice! Please enter choice 1-3 or 'q' to quit");
		
			System.out.println("---------------------------------------------------------------");
		}					
	}
	
	public static void main(String [] args)
	{		
		menu();
	}
}