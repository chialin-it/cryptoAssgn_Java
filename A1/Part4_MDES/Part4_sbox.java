/*Part 4 MDES
 *SBOX implemented at Line 196 
 *
 *CSCI361 Assignment 1
 *UOW ID: 6097881
 *NAME: LEE CHIA LIN
 */
 
import java.util.*;
import java.io.*;

public class Part4_sbox
{
	private static String a, b;
	private static final String separator = "-------------------------------" 
											+"------------------------------" 
											+"------------------------------";
	
	public static void main (String [] args)
	{
		String s = "1100";
		String [] key = {"00", "01", "10", "11"};
		String [] msg = {"0000", "0001", "0010", "0011", 
					   	 "0100", "0101", "0110", "0111", 
					 	 "1000", "1001", "1010", "1011", 
					   	 "1100", "1101", "1110", "1111"};
		
		String [] verify = {"1100", "1000", "0100", "0000"};

		System.out.println("After changing the SBOX...");
		System.out.println(separator);
		
		//display the encryption table
		System.out.println("Encryption table");
		System.out.print("   Key\t");			
		for(int i=0; i<16; i++)
		{
			System.out.print(msg[i] + " ");
		}
		System.out.println();
		
		for(int i=0; i<4; i++)
		{			
			System.out.print("   " + key[i] + "\t");			
			for(int j=0; j<16; j++)
			{
				System.out.print(encrypt(msg[j],key[i]) + " ");
			}
			System.out.println();
		}
		System.out.println(separator);
		
		//display the decryption table
		System.out.println("Decryption table");
		System.out.print("   Key\t");			
		for(int i=0; i<16; i++)
		{
			System.out.print(msg[i] + " ");
		}
		System.out.println();
		
		for(int i=0; i<4; i++)
		{			
			System.out.print("   " + key[i] + "\t");			
			for(int j=0; j<16; j++)
			{
				System.out.print(decrypt(msg[j],key[i]) + " ");
			}
			System.out.println();
		}
		System.out.println(separator);
		System.out.println("Verifying that question 3 equations are no longer valid...");
		
		System.out.println(separator);
		System.out.println("Verifying E(1100) = E(1000) + E(0100) + E(0000)" 
							+ " with the keys 00, 01, 10, 11");
							
		//Check againest the 4 keys
		for(int i=0; i<4; i++)
		{
			System.out.print("E(1000) + E(0100) + E(0000) = ");
			
			String encryptSum = XOR(XOR(encrypt(verify[1], key[i]), encrypt(verify[2], key[i])),
								encrypt(verify[3], key[i])); 
								
			System.out.print(encryptSum);
			System.out.print(",\tE(" + verify[0] 
								+ "): " + encrypt(verify[0], key[i]));
								
			if(encryptSum.equals(encrypt(verify[0], key[i])))
				System.out.println("\tVerified");
			else
				System.out.println("\tNot verified");
		}
		
		String [] formEqn = {"1010", "1001", "0110", "0101", 
					   		 "0011", "0111", "1011", "1101", 
							"1110", "1111"};
		
		//loop size of formEqn
		for(int i=0; i<10; i++)
		{
			System.out.println(separator);
			System.out.println("Form equation and check with keys for E(" 
								+ formEqn[i] + ")");
			
			//loop with the keys		
			for(int j=0; j<4; j++)
			{			
				//form the arr with each string and each key
				ArrayList<String> arr = writeEquation(formEqn[i]);
				String encryptSum = encrypt(arr.get(0),key[j]);
			
				//display eqn and compute encryptSum
				for(int k=0; k<arr.size(); k++)
				{					
					System.out.print("E(" + arr.get(k) + ")");
					
					if(k==arr.size()-1)
						System.out.print(" = ");
								
					else
					{
						if(k<arr.size()-1)
							encryptSum = XOR(encryptSum, encrypt(arr.get(k+1), key[j]));
						
						System.out.print(" + ");
					}						
				}
				System.out.print(encryptSum);
				
				String enc = encrypt(formEqn[i], key[j]);
				System.out.print(",\tE(" + formEqn[i] + ") = " + enc);
					
				//check				
				if(encryptSum.equals(enc))
					System.out.println("\tVerified");
				else
					System.out.println("\tNot verified");
			}
		}			
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