package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		CardNode current = deckRear.next;
		while(current.cardValue != 27)
			current = current.next;
		swap(current, current.next);
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
	    CardNode current = deckRear.next;
	    while(current.cardValue != 28)
	    	current = current.next;
	    swap(current, current.next);
	    swap(current.next, current.next.next);
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		// COMPLETE THIS METHOD
		if(deckRear.cardValue + deckRear.next.cardValue == 55)
			return;
				
//		else if(deckRear.cardValue == 28 || deckRear.cardValue == 27){
//			CardNode otherJ = null;
//			CardNode current = deckRear.next;
//			while(otherJ == null){
//				if(current.next.cardValue + deckRear.cardValue == 55)
//					otherJ = current;
//				current = current.next;
//			}
//			deckRear = otherJ;
//			
//		}
//		else if(deckRear.next.cardValue == 27 || deckRear.next.cardValue == 28){
//			CardNode midJ = null;
//			CardNode current = deckRear.next.next;
//			while(midJ == null){
//				if(current.cardValue + deckRear.next.cardValue == 55)
//					midJ = current;
//				current = current.next;
//			}
//			deckRear = midJ;
//		}
//		else{
//			CardNode current = deckRear.next;
//			CardNode firstJ = null;
//			CardNode secondJ = null;
//			while(firstJ == null || secondJ == null){
//				if((current.next.cardValue == 27 || current.next.cardValue == 28) && firstJ == null){
//					firstJ = current;
//					current = current.next.next;
//				}
//				else if(firstJ != null && current.cardValue + firstJ.next.cardValue == 55){
//					secondJ =current;
//					current = current.next;
//				}
//				else
//					current = current.next;
//			}
//			
//			CardNode temp = firstJ.next;
//			firstJ.next = current;
//			secondJ.next = deckRear.next;
//			deckRear.next = temp;
//			deckRear = firstJ;
//			
//		}
		CardNode current = deckRear;
		CardNode firstJ = null;
		CardNode secondJ = null;
		while(firstJ == null || secondJ == null){
			if((current.next.cardValue == 27 || current.next.cardValue == 28) && firstJ == null){
				firstJ = current;
				current = current.next.next;
			}
			else if(firstJ != null && current.next.cardValue + firstJ.next.cardValue == 55){
				secondJ = current;
				current = current.next.next;
			}
			else
				current = current.next;
		}
		if(firstJ.next == deckRear || secondJ.next == deckRear)
			deckRear = deckRear.cardValue == firstJ.next.cardValue? secondJ:firstJ;
		else if(firstJ.next == deckRear.next || secondJ.next == deckRear.next)
			deckRear = deckRear.next.cardValue == firstJ.next.cardValue? secondJ.next: firstJ.next;
		else{
			CardNode temp = firstJ.next;
			CardNode temp2 = secondJ.next;
			firstJ.next = current;
			temp2.next = deckRear.next;
			deckRear.next = temp;
			deckRear = firstJ;
		}
		
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		// COMPLETE THIS METHOD
		if(deckRear.cardValue == 28 || deckRear.cardValue == 27)
			return;
		CardNode current = deckRear.next;
		CardNode temp = null;
		for(int x = 0; x < deckRear.cardValue-1;x++)
			current = current.next;
		temp = current;
		current = deckRear.next;
		while(current.next != deckRear)
			current = current.next;
		current.next = deckRear.next;
		deckRear.next = temp.next;
		temp.next = deckRear;
	}
	
	/**
	 * Swaps the two node values
	 */
	void swap(CardNode a, CardNode b){
		int temp = a.cardValue;
		a.cardValue = b.cardValue;
		b.cardValue = temp;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {	    
	    int x = -1;
	    int key = 0;
	    do{
	    	System.out.println("Start");
	    	printList(deckRear);
	    	jokerA();
	    	System.out.println("JokerA");
	    	printList(deckRear);
			jokerB();
			System.out.println("JokerB");
	    	printList(deckRear);
			tripleCut();
			System.out.println("Triple");
	    	printList(deckRear);
			countCut();
			System.out.println("Count");
	    	printList(deckRear);
			x = deckRear.next.cardValue;
			if(x == 28)
				x = 27;
			CardNode current = deckRear.next;
			for(int y = 0; y < x; y++)
				current = current.next;
			key = current.cardValue;
	    }while(key > 26);
	    
	    return key;
	}

	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String msg = "";
		message = message.toUpperCase();
		for(int x = 0;x < message.length(); x++){
			if(Character.isLetter(message.charAt(x))){
				msg += message.charAt(x);
			}
		}
		
		String enmsg = "";
		for(int x = 0; x < msg.length(); x++){
			System.out.println(x + "\n");
			int temp2 = getKey();
			int temp = msg.charAt(x) - 64 + temp2;
			if(temp > 26)
				enmsg += (char)(temp-26+64);
			else
				enmsg += (char)(temp+64);
		}
	    return enmsg;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String demsg = "";
		for(int x = 0; x < message.length(); x++){
			int temp = getKey();
			if(temp >= message.charAt(x)-64)
				demsg += (char)(message.charAt(x)+26-temp);
			else
				demsg += (char)(message.charAt(x)-temp);
		}
	    return demsg;
	}
}
