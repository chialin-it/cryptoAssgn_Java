/*Part 2 Encryption Question
 *Index of Coincidence
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

public class Part2
{	
	private static Scanner userInput = new Scanner (System.in);
	private static Scanner input;
	
	public static void main(String [] args)
	{		
		System.out.println("Please enter filename of textA and textB: ");
		String fileA = userInput.next();
		String fileB = userInput.next();
		System.out.println();	
		
		try
		{
			//read file containing text A
			input = new Scanner (Paths.get(fileA));
			Part2Alphabets textA = new Part2Alphabets();
			
			while (input.hasNextLine()) 
			{
				//read from input and store data
				String s = input.nextLine();
				countChar(s, textA); 
				
			}
			//close
			input.close();				
			System.out.println("Successfully read from " + fileA);
		
			//read file containing text B
			input = new Scanner (Paths.get(fileB));
			Part2Alphabets textB = new Part2Alphabets();
			
			while (input.hasNextLine()) 
			{
				//read from input and store data
				String s = input.nextLine();
				countChar(s, textB); 
				
			}
			//close
			input.close();				
			System.out.println("Successfully read from " + fileB);
			System.out.println("-----------------------------------------------------------");
			
			double MIC = computeMIC(textA, textB);
			
			System.out.println("Index of Coincidence (IC) of " + fileA + ": " + textA.getIC());
			System.out.println("Index of Coincidence (IC) of " + fileB + ": " + textB.getIC());
			System.out.println("Mutual IC of textA and textB: " + MIC);			
		}
		catch (IOException e)
		{
			System.err.println ("Error opening file.");
			System.exit (1);
		}
			
	}
	
   //save count of Char to object
   public static void countChar(String input, Part2Alphabets alpha) 
   {             
       for (int i = 0; i < input.length(); i++) 
       {
           char M = input.charAt(i);
        
           if (Character.isLetter(M)) 
           {
           		alpha.addTotal();
           		alpha.addCount(Character.toLowerCase(M));           				
           }
       }
   }	
   
   //display frequency of a-z
   public static void printCount(Part2Alphabets alpha)
   {
   		//print 
		System.out.println("-------------------------------------------------------------------");
		System.out.println("Track count of Char");
		System.out.println("  Index\tChar\tCount");
		for(int i=0; i<26; i++)
		{
			System.out.printf("  %d\t%s\t%d%n", alpha.getIndex(alpha.getAlpha(i)), alpha.getAlpha(i), alpha.getCount(i));
		}	
   }
   
   //compute MIC
   public static double computeMIC(Part2Alphabets textA, Part2Alphabets textB)
   {
   		double MIC = 0.0;
   		
   		for(int i=0; i<26; i++)
   		{
   			MIC += textA.getCount(i)*textB.getCount(i);   			
   		}
   		
   		MIC = MIC/(textA.getTotal()*textB.getTotal());
   		return MIC;
   }   
}

class Part2Alphabets
{
	private String alpha = "abcdefghijklmnopqrstuvwxyz";
	private int [] countAlpha = new int [26];
	private int totalCount = 0;	
	
	public Part2Alphabets()
	{
		//default constructor
	}
	
	//compute and return IC
	public double getIC()
	{
		double IC = 0;
		
		for(int i=0; i<26; i++)
		{
			IC += countAlpha[i]*(countAlpha[i]-1);		
		}
		
		IC = IC/(totalCount*(totalCount-1.0));
		return IC;
	}
	public char getAlpha(int i)
	{
		return alpha.charAt(i);
	}
	
	public int getIndex(char c)
	{
		return alpha.indexOf(c);
	}
	
	public int getCount(char c)
	{
		return countAlpha[getIndex(c)];
	}
	
	public int getCount(int i)
	{
		return countAlpha[i];
	}
	
	public void addCount(char c)
	{
		countAlpha[getIndex(c)]++;
	}
	
	public void addCount(int i)
	{
		countAlpha[i]++;
	}
	public void addTotal()
	{
		totalCount++;
	}
	public int getTotal()
	{
		return totalCount;
	}	
}