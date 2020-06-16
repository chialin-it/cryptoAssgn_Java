/*CSCI 361 Cryptography
 *Assignment 2 
 *Q4 DSA
 *
 *UOW Student ID: 6097881
 *Name: Lee Chia Lin
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.math.BigInteger;
import java.nio.file.Paths;
 
public class dsa
{	
	private static Scanner input = new Scanner(System.in);
	private static SecureRandom rand = new SecureRandom();
	private static BigInteger p, q, g, x, y, k;
	
	private static Formatter dataOut;
	private static Scanner dataIn;

	//sha1 Source: http://www.sha1-online.com/sha1-java/
	static String sha1(String input) throws NoSuchAlgorithmException 
	{
	    MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	    byte[] result = mDigest.digest(input.getBytes());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < result.length; i++) {
	        sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    return sb.toString();
	}
	
	static void KeyGen() throws NoSuchAlgorithmException
	{
		/*p = new BigInteger("132323768951986124075479307182674357577285270296"
							+ "23408872245156039757713029036368719146452186041204237350" 
							+ "521785240337048752071462798273003935646236777459223");
		
		q = new BigInteger("857393771208094202104259627990318636601332086981");
		
		g = new BigInteger("5421644057436475141609648488325705128047428394380" 
							+ "474376834667300766108262613900542681289080713724597310673" 
							+ "074119355136085795982097390670890367185141189796");
		
		x = new BigInteger("792647853324835944125296675259316105451780620466");
		
		y = new BigInteger("1078382798593688340780047888437688525801232912481" 
							+ "655299440031866941712227984308664513720074342723253116776" 
							+ "6104260606805303022314906254403593803159583034340");
		*/
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		keyGen.initialize(1024);
		KeyPair keypair = keyGen.genKeyPair();
		DSAPrivateKey priKey = (DSAPrivateKey) keypair.getPrivate();
		DSAPublicKey pubKey = (DSAPublicKey) keypair.getPublic();

		DSAParams dsaParams = priKey.getParams();
		p = dsaParams.getP();
		q = dsaParams.getQ();
		g = dsaParams.getG();
		x = priKey.getX();
		y = pubKey.getY();
		writeParams();
		
		genK();		
	}
	
	//random k	
	static void genK()
	{
		//get new random 
		BigInteger temp = BigInteger.probablePrime(q.bitLength()-1, rand);
		
		//there exist modInverse where gcd = 1
		while(!q.gcd(temp).equals(BigInteger.ONE) || temp.equals(k) || temp.equals(BigInteger.ONE))
		{			
			temp = BigInteger.probablePrime(q.bitLength()-1, rand);
		}
		
		//set new k and compute new r
		k = temp;
	}

	static BigInteger computeR()
	{
		//compute new r
		BigInteger r = g.modPow(k, p);
		r = r.mod(q);
		return r;
	}

	//write KeyGen to params.txt
	static void writeParams()
	{
		try
		{
			dataOut = new Formatter ("params.txt");
			dataOut.format (p + ", ");
			dataOut.format (q + ", ");
			dataOut.format (g + ", ");
			dataOut.format (x + ", ");
			dataOut.format (y.toString());
				
			if (dataOut != null)
			{
				dataOut.close ();
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

	//write result to sig.txt
	static void writeSig(BigInteger hm, BigInteger sig)
	{
		try
		{
			dataOut = new Formatter ("sig.txt");
			dataOut.format (hm + ", ");
			dataOut.format (computeR() + ", ");
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

	static void sign(String m) throws NoSuchAlgorithmException
	{
		//sign
		BigInteger hm = new BigInteger(sha1(m).getBytes());
		
		BigInteger s = k.modInverse(q);
		s = s.multiply(hm.add(x.multiply(computeR())));
		s = s.mod(q);
		
		writeSig(hm, s);
	}
	
	static boolean verify(BigInteger hm, BigInteger r, BigInteger s) throws NoSuchAlgorithmException
	{		
		boolean result;
		
		//verify				
		if(q.gcd(s).equals(BigInteger.ONE))
		{
			BigInteger w = s.modInverse(q);
			BigInteger t1 = hm.multiply(w).mod(q);
			BigInteger t2 = r.multiply(w).mod(q);
			
			BigInteger v = g.modPow(t1, p).multiply(y.modPow(t2, p));
			v = v.mod(p).mod(q);
			
			if(r.compareTo(v) == 0)
				result = true;
			else
				result = false;
			
			System.out.println("s: " + s);
			System.out.print("v: " + v + " = ");
			System.out.println("r: " + r);
		}
		else
		{
			result = false;
			System.out.println("s: " + s);
			System.out.println("s is not a valid modInverse, v could not be computed.");
		}
			
		return result;
	}
	
	public static void main(String [] args) throws NoSuchAlgorithmException
	{
		try
		{
			KeyGen();				
			
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
					KeyGen();
					
					System.out.println("p: " + p);	
					System.out.println("q: " + q);
					System.out.println("g: " + g);
					System.out.println("x: " + x);
					System.out.println("y: " + y);
					
					System.out.println("Successfully generated new keys.");
					System.out.println ("p, q, g, x, y output to params.txt.");
				}
					
				else if(user.equals("2"))
				{
					System.out.print("Enter input filename: ");
					String file = input.next();
					
					String m = readFile(file);	//message m
					sign(m);
				}
				else if(user.equals("3"))
				{
					System.out.print("Enter input filename: ");
					String file = input.next();
					System.out.println("Verifiying " + file + " and signature sig.txt...");
					
					String [] sigFile = readFile("sig.txt").split(", ");	//hm, r, s
					BigInteger hm = new BigInteger(sigFile[0]);	//hashed message hm
					BigInteger r = new BigInteger(sigFile[1]);
					BigInteger s = new BigInteger(sigFile[2]);
					
					boolean result = verify(hm, r, s);
					if(result)
						System.out.println("\nResult: True");
					else
						System.out.println("\nResult: False");
				}
				else
					System.out.println("Invalid choice! Please enter choice 1-3 or 'q' to quit");
			
				System.out.println("---------------------------------------------------------------");
			}						
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println("Error!");
		}
		/*
		//160-bit prime
		BigInteger q = new BigInteger("1079327846108714802061940813092735406976561392671");
									
		
		
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		    kpg.initialize(1024, new SecureRandom());
		    KeyPair pair = kpg.generateKeyPair();
		    
		    PrivateKey priKey = pair.getPrivate();
		    PublicKey pubKey = pair.getPublic();
		    
     		System.out.println("SK = "+priKey.toString());
     		System.out.println("PK = "+pubKey.toString());
		    
		    System.out.println(pair);
		*/
	}
}