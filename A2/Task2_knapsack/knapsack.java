/*CSCI 361 Cryptography
 *Assignment 2 
 *Q2 Super-increaseing Knapsack
 *
 *UOW Student ID: 6097881
 *Name: Lee Chia Lin
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.math.BigInteger;
 
public class knapsack
{
	private static Scanner input = new Scanner(System.in);
	
	//check if condition of multipllier is satised
	static boolean checkMulti(int p, int m)
	{		
		//check if m is greater or equal to p
		if(m >= p)
		{
			return false;
		}
		else
		{		
			//convert to bigInt
			BigInteger mBigInt = BigInteger.valueOf(m);
			BigInteger pBigInt = BigInteger.valueOf(p);
			
			BigInteger checkM = pBigInt.gcd(mBigInt);
			
			//check if gcd = 1
			if(checkM.equals(BigInteger.ONE))
			{
				return true;
			}
			return false;
		}
	}
	
	static int modInverse(int a, int m) 
    { 
        a = a % m; 
        for (int x = 1; x < m; x++) 
           if ((a * x) % m == 1) 
              return x; 
        return 1; 
    } 
	
	//encrypt
	static void encrypt(int size, ArrayList<Integer> publicArr)
	{
		System.out.print("Enter a message in decimal (E.g 35): ");
		int msg = input.nextInt();
		String binary = Integer.toBinaryString(msg);	//convert to binary
		
		while(binary.length() > size)
		{
			System.out.println("\nError! Keysize too small to encrypt the decimal!");
			System.out.print("Enter a smaller message in decimal (up to " + size + "-bit): ");
			msg = input.nextInt();
			
			//convert to binary
			binary = Integer.toBinaryString(msg);
		}
		
		while(binary.length() < size)
		{
			binary = "0" + binary;
		}
		
		//encrypt
		int cipher = 0;
		for(int i=0; i<size; i++)
		{
			if(binary.charAt(i) == '0')
				cipher += 0*publicArr.get(i);
			else
				cipher += 1*publicArr.get(i);
		}
		
		//display
		System.out.println("Message: " + msg);
		System.out.println("Binary: " + binary);
		System.out.println("\nCipher: " + cipher);
	}
	
	//decrypt
	static void decrypt(int size, ArrayList<Integer> privateArr, int multi, int modValue)
	{
		System.out.print("Enter a cipher in decimal (E.g 35): ");
		int cipher = input.nextInt();
		
		//y = c * (w^-1) mod p;
		int y = cipher * modInverse(multi, modValue);
		y = y % modValue;	
		
		//convert to binary	
		String binary = "";
		for(int i=size-1; i>=0; i--)
		{
			if(y >= privateArr.get(i))
			{
				binary = "1" + binary;
				y -= privateArr.get(i);
			}
			else
				binary = "0" + binary;
		}		
		//convert to decimal
		int msg = Integer.parseInt(binary, 2);  				
		
		//display
		System.out.println("Cipher: " + cipher);
		System.out.println("\nBinary: " + binary);
		System.out.println("Message: " + msg);
	}
	
	public static void main (String [] args)
	{		
		ArrayList<Integer> privateArr = new ArrayList<Integer>();
		ArrayList<Integer> publicArr = new ArrayList<Integer>();
		
		System.out.print("Enter size of super-increasing knapsack: ");
		int size = input.nextInt();
		
		//user enters private value of each a		
		int increasingSum = 0;
		for(int i=0; i<size; i++)
		{
			System.out.print("Enter value of a" + i + ": ");
			int a = input.nextInt();
			
			//a has to be greater than increasingSum
			while(a <= increasingSum)
			{
				System.out.print("Too small! Re-enter value of a" + i + ": ");
				a = input.nextInt();
			}
			privateArr.add(a);
			increasingSum += a;
		}
		
		System.out.print("Enter the modulus: ");
		int modValue = input.nextInt();
		
		//mod has to be greater than sum
		while(modValue <= increasingSum)
		{
			System.out.print("Too small! Re-enter the modulus: ");
			modValue = input.nextInt();
		}
		
		System.out.print("Enter the multiplier: ");
		int multi = input.nextInt();
		
		//check multiplier
		while(checkMulti(modValue, multi) != true)
		{
			System.out.print("Invalid! Re-enter the multipler: ");
			multi = input.nextInt();
		}
		
		System.out.println("\nComputing public key...");
		System.out.print("Public key: {");
		
		//compute public key array and display
		for(int i=0; i<privateArr.size(); i++)
		{
			int b = (multi * privateArr.get(i)) % modValue;			
			//to avoid negative b
			if(b < 0)
				b += modValue;
				
			publicArr.add(b);
			
			//display
			if(i<privateArr.size()-1)
				System.out.print(b + ", ");
			else
				System.out.println(b + "}");
		}			
		System.out.println("---------------------------------------------------------------");
		
		//encrypt and decrypt
		while(true)	
		{
			System.out.println("1. To enter message (encrypt)");
			System.out.println("2. To enter ciphertext (decrypt)");		
			
			System.out.print("Enter choice (1-2 or 'q' to quit): ");			
			String user = input.next();
			System.out.println();
			
			if(user.equals("q"))
				break;
				
			else if(user.equals("1"))
				encrypt(size, publicArr);
			
			else if(user.equals("2"))	
				decrypt(size, privateArr, multi, modValue);			
	
			else
				System.out.println("Invalid choice! Please enter choice 1-2 or 'q' to quit");
		
			System.out.println("---------------------------------------------------------------");
		}	
	}
}