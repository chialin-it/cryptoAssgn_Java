/*Part 5 OFB
 *
 *CSCI361 Assignment 1
 *UOW ID: 6097881
 *NAME: LEE CHIA LIN
 */
 
import java.util.*;
import java.io.*;

public class Part5
{	
	private final static int DELTA = 0x9e3779b9;
	private final static int DECRYPT_SUM_INIT = 0xC6EF3720;
	private final static long MASK32 = (1L << 32) - 1;

	public static void main (String [] args)
	{	
		long iv = Long.MAX_VALUE;	
		long myId = 6097881;	//input plaintext
		int [] key = {78945677, 87678687, 123234, 234234};
		int kbit = 4;
		
		//SECTION 2
		System.out.println("Section 2 Encrypt student number using 4-bit");
		System.out.println("---------------------------------------------------------------------------------------------");
		
		long cipher = kbitEncrypt(iv, myId, key, kbit);
		long kbitCipher = (cipher >>> 64-kbit);	
				
		//convert to binary and string for display
		String kbitBinary = Long.toBinaryString(kbitCipher);
		kbitBinary = String.format("%4s",kbitBinary);
		kbitBinary = kbitBinary.replace(' ','0');		
			
		//display information
		System.out.println();	
		System.out.println("Cipher (Binary): " + Long.toBinaryString(cipher));
		System.out.println("Cipher: " + cipher);
		System.out.println();
		System.out.println(kbit + "-bit (Binary): " + kbitBinary);	
		System.out.println(kbit + "-bit: " + kbitCipher);
		
		System.out.println("---------------------------------------------------------------------------------------------");
		
		//SECTION 3
		//use mod of studentID as kbit
		int sumOfId = 6 + 0 + 9 + 7 + 8 + 8 + 1;
		kbit = sumOfId%4;
		
		//avoid negative mod value
		if(kbit<0)
			kbit += 4;		
		
		System.out.println("Section 3 Add all the digits of your student number mod 4. Let the result be c.");
		System.out.println("Encrypt student number using c-bit");
		System.out.println("---------------------------------------------------------------------------------------------");
		
		cipher = kbitEncrypt(iv, myId, key, kbit);
		kbitCipher = (cipher >>> 64-kbit);	
				
		//convert to binary and string for display
		kbitBinary = Long.toBinaryString(kbitCipher);
		kbitBinary = String.format("%3s",kbitBinary);
		kbitBinary = kbitBinary.replace(' ','0');		
			
		//display information
		System.out.println();			
		System.out.println("Cipher (Binary): " + Long.toBinaryString(cipher));
		System.out.println("Cipher: " + cipher);
		System.out.println();
		System.out.println(kbit + "-bit (Binary): " + kbitBinary);	
		System.out.println(kbit + "-bit: " + kbitCipher);
	}	
        
    //SECTION 1
    //this algorithm is also valid for 4bit as proved for SECTION 2
    public static long kbitEncrypt(long iv, long input, int [] key, int kbit)
    {
    	long cipher = 0;
    	long output = 0;
		long x = 0;
		long y = 0; 
		long outputK = 0;
		
		System.out.println(kbit + "-bit encrypt " + input);
		System.out.println("Round");
		
		//encrypt
		for(int i=0; i<(64/kbit); i++)
		{		
			output = encrypt(iv, key);
			x = input ^ output;
			y = (x << kbit);		
			cipher = (cipher << kbit) | (x >>> 64-kbit);
			
			//shifting iv
			input = (input << kbit);
			iv = (iv << kbit);
			outputK = (output >>> 64-kbit);
			iv = iv | outputK;			
			
			//display cipher (binary) each round
			String str = String.format("%64s",Long.toBinaryString(cipher));
			str = str.replace(' ','0');
			
		//	System.out.println(i + "\tiv: " + Long.toBinaryString(iv) + "\t" + iv);
			System.out.println(" " + i + "\tcipher: " + str);
		}			
		return cipher;
    }
    
    public static long encrypt(long in, int [] key) 
    {
        int v1 = (int) in;
        int v0 = (int) (in >>> 32);
        int sum = 0;
        
        for (int i=0; i<32; i++) 
        {
            sum += DELTA;
            v0 += ((v1<<4) + key[0]) ^ (v1 + sum) ^ ((v1>>>5) + key[1]);
            v1 += ((v0<<4) + key[2]) ^ (v0 + sum) ^ ((v0>>>5) + key[3]);
        }
        return (v0 & MASK32) << 32 | (v1 & MASK32);
    }
}