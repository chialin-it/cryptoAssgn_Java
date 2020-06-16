/*Part 4 MDES
 *ECB CBC
 *
 *CSCI361 Assignment 1
 *UOW ID: 6097881
 *NAME: LEE CHIA LIN
 */
 
import java.util.*;
import java.io.*;

public class Part4_ecb_cbc
{
	private static String a, b;
	private static final String separator = "-------------------------------" 
											+"------------------------------" 
											+"------------------------------";
	
	public static void main (String [] args)
	{
		String [] key = {"00", "01", "10", "11"};
		String [] msg = {"0000", "0001", "0010", "0011", 
					   	 "0100", "0101", "0110", "0111", 
					 	 "1000", "1001", "1010", "1011", 
					   	 "1100", "1101", "1110", "1111"};
		
		String hexCode = "0123456789abcdef";
		
		//Scanner
		Scanner user = new Scanner(System.in);
		System.out.println("*************All input are CASE sensitive**********");
		System.out.print("What are you entering? (ECB/CBC): ");
		String mode = user.next();
		
		String [] bin;
		System.out.println();
		
		//loop if user did not enter ECB or CBC
		while(!mode.equals("ECB") && !mode.equals("CBC"))
		{
			System.out.print("Error! Please enter ECB or CBC only: ");
			mode = user.next();
			System.out.println();
		}
		
		//if ECB
		if(mode.equals("ECB"))
		{
			System.out.println("Enter key, encrypt/decrypt, hex string: ");
			System.out.println("SAMPLE: 01 decrypt b6f7a11");
			String K = user.next();
			String enc = user.next();
			String hexInput = user.next();
			
			bin = new String [hexInput.length()];		
			for(int i=0; i<hexInput.length(); i++)
			{
				int tempIdx = hexCode.indexOf(hexInput.charAt(i));
				bin[i] = msg[tempIdx];
			} 
			
			System.out.println("\nMessage: " + hexInput);
			
			if(enc.equals("encrypt"))			
				bin = ECBencrypt(bin, K);
			else if(enc.equals("decrypt"))
				bin = ECBdecrypt(bin, K);
			else
				System.out.println("Please state encrypt or decrypt!");			
		}	
		//else CBC
		else
		{
			System.out.println("Enter key, iv(0-f), encrypt/decrypt, hex string: ");
			System.out.println("SAMPLE: 11 a encrypt 2a45def");
			String K = user.next();
			String iv = user.next();
			String enc = user.next();
			String hexInput = user.next();
			
			bin = new String [hexInput.length()];		
			for(int i=0; i<hexInput.length(); i++)
			{
				int tempIdx = hexCode.indexOf(hexInput.charAt(i));
				bin[i] = msg[tempIdx];
			} 
			
			System.out.println("\nMessage: " + hexInput);
			iv = msg[hexCode.indexOf(iv.charAt(0))];
			
			if(enc.equals("encrypt"))			
				bin = CBCencrypt(bin, K, iv);
			else if(enc.equals("decrypt"))
				bin = CBCdecrypt(bin, K, iv);
			else
				System.out.println("Please state encrypt or decrypt!");
		}	
							
		//convert binary to string cipher
		String cipher = "";
		for(int i=0; i<bin.length; i++)
		{
			for(int j=0; j<16; j++)
			{
				if(bin[i].equals(msg[j]))
				{
					cipher += hexCode.charAt(j);
				}
			}
		} 
		System.out.println("Ciphertext: " + cipher);
	}
		
	//ECB
	public static String [] ECBencrypt(String [] bin, String K)
	{
		for(int i=0; i<bin.length; i++)
		{
			bin[i] = encrypt(bin[i], K);
		}		
		return bin;
	}
	
	public static String [] ECBdecrypt(String [] bin, String K)
	{
		for(int i=0; i<bin.length; i++)
		{
			bin[i] = decrypt(bin[i], K);
		}		
		return bin;
	}
	//CBC
	public static String [] CBCencrypt(String [] bin, String K, String iv)
	{
		//first XOR
		bin[0] = XOR(bin[0], iv);	
		bin[0] = encrypt(bin[0], K);
		
		for(int i=1; i<bin.length; i++)
		{
			bin[i] = XOR(bin[i-1], bin[i]);
			bin[i] = encrypt(bin[i], K);
		}		
		return bin;
	}
	
	public static String [] CBCdecrypt(String [] bin, String K, String iv)
	{		
		for(int i=bin.length-1; i>0; i--)
		{
			bin[i] = decrypt(bin[i], K);			
			bin[i] = XOR(bin[i], bin[i-1]);	
		}			
		
		bin[0] = decrypt(bin[0], K);			
		bin[0] = XOR(bin[0], iv);				
		return bin;		
	}
	
	//get K1 = getSubK(K, 1);
	public static String getSubK(String s, int i)
	{
		//K = (k1 k2)
		//K1 = (k1 k1 k1)
		
		String temp = "";		
		temp += s.charAt(i-1);
		temp += s.charAt(i-1);
		temp += s.charAt(i-1);
		
		return temp;
	}
	
	public static String rotateLeft(String s)
	{
		s += s.substring(0,1);
		return s.substring(1);
	}
	
	public static String rotateRight(String s)
	{
		String temp = "";
		int size = s.length();
		
		temp += s.substring(size-1);
		temp += s;
		
		size = temp.length();		
		return temp.substring(0,size-1);
	}
	
	public static String XOR(String s1, String s2)
	{		
		String temp = "";
		for(int i=0; i<s1.length();i++)
		{
			temp += ((s1.charAt(i)-'0') ^ (s2.charAt(i)-'0')); 	
		}	
			
		return temp;
	}
	
	public static String expandE(String s)
	{
		//x1 x2 x1
		s += s.charAt(0);
		return s;
	}
	
	public static String linear(String s)
	{
		//new SBOX
		String [] sboxI = {"000", "001", "010", "011",
							"100", "101", "110", "111"};
							
		String [] sboxJ = {"00", "00", "00", "01", 
							"00", "00", "10", "11"};
		
		String j = "";
		
		for (int i = 0; i <8; i++)
			if (sboxI[i].equals(s))
				j = sboxJ[i];
		
		//s = i1 i2 i3				
		/*String j = "";
		String i2 = "" , i3 = "";
		
		i2 += s.charAt(1);
		i3 += s.charAt(2);
		
		j += XOR(i2, "1");
		j += XOR(i3, "1");
		*/
		
		return j;
	}
	
	public static String funcF(String s, String subK)
	{
		String j = XOR(expandE(s), subK);
		String temp = linear(j);
		return rotateLeft(temp);
	}
	
	public static String encrypt(String M, String K)
	{
		String K1 = getSubK(K, 1);
		String K2 = getSubK(K, 2);
		
		String temp = rotateLeft(M);
		
		//split A and B from 4bits M
		String A0 = temp.substring(0, 2);
		String B0 = temp.substring(2);
	//	System.out.println("A0: " + A0);
		
		//B = f with K1 and XOR
		String A1 = B0;
		String B1 = XOR(A0, funcF(B0, K1));
		
		//B = f with K2 and XOR
		String A2 = B1;
		String B2 = XOR(A1, funcF(B1, K2));
		
		//swap the order, therefore B2 first then A2
		temp = B2 + A2;
		return rotateRight(temp);
	}
	
	public static String decrypt(String C, String K)
	{
		String K1 = getSubK(K, 1);
		String K2 = getSubK(K, 2);
		
		String temp = rotateLeft(C);
		
		//split A and B from 4bits M
		//swap the order, therefore B first then A
		String B2 = temp.substring(0, 2);
		String A2 = temp.substring(2);
		
		//A = f with K2 and XOR
		String B1 = A2;
		String A1 = XOR(funcF(A2, K2), B2);
		
		//A = f with K1 and XOR
		String B0 = A1;
		String A0 = XOR(funcF(A1, K1), B1);
		
		temp = A0 + B0;
		return rotateRight(temp);
	}
	
	public static ArrayList<String> writeEquation(String M)
	{		
		ArrayList<String> arr = new ArrayList<String>();
		String [] temp = {"1000", "0100", "0010", "0001"};	
				
		for(int i=0; i<M.length(); i++)	
		{
			if(M.charAt(i) == '1')
				arr.add(temp[i]);
		}
		
		if(arr.size()%2 == 0)
		{
			arr.add("0000");
		}
		return arr;
	}
}