package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author Sarah McNey
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {

		Node ptr1 = poly1, ptr2 = poly2;
		Node copyFront = null;
		Node copyPrev = null, copyCurrent = null;
		//edge cases:
		if(poly1 == null && poly2 == null) { return null; }
		else if(poly1 == null && poly2 != null) {
			//can't just return poly2, need to copy
			while(ptr2!=null) {
				Node copyNode = new Node(ptr2.term.coeff, ptr2.term.degree, null);
				if(copyPrev == null) {
					copyFront = copyNode;
					copyPrev = copyFront;
				}
				else {
					copyCurrent = copyNode;
					copyPrev.next = copyCurrent;
					copyPrev = copyCurrent;
				}
				ptr2 = ptr2.next;
			}
			return copyFront;
		}
		else if(poly2 == null && poly1 != null) {
			//can't just return poly2, need to copy
			while(ptr1!=null) {
				Node copyNode = new Node(ptr1.term.coeff, ptr1.term.degree, null);
				if(copyPrev == null) {
					copyFront = copyNode;
					copyPrev = copyFront;
				}
				else {
					copyCurrent = copyNode;
					copyPrev.next = copyCurrent;
					copyPrev = copyCurrent;
				}
				ptr1 = ptr1.next;
			}
			return copyFront;
		}
		ptr1 = poly1;
		ptr2 = poly2;
		
		Node prev = null, current = null; //prev and current apply to the new linked list only
		Node sumFront = null;
		
		while(poly1 != null && poly2 != null) { //this is okay bc we aren't altering poly1 or poly2 at all
			int p1deg = poly1.term.degree;
			float p1coeff = poly1.term.coeff;
			int p2deg = poly2.term.degree;
			float p2coeff = poly2.term.coeff;
			
			if(p1deg < p2deg) {
				Node tmp = new Node(p1coeff, p1deg, null);
				if(prev == null) { //must set up new front
					sumFront = tmp;
					prev = sumFront;
				}
				else {
					current = tmp;
					prev.next = current;
					prev = current;
				}
				poly1 = poly1.next;
			}
			else if(p2deg < p1deg) {
				Node tmp = new Node(p2coeff, p2deg, null);
				if(prev == null) {
					sumFront = tmp;
					prev = sumFront;
				}
				else {
					current = tmp;
					prev.next = current;
					prev = current;
				}
				poly2 = poly2.next;
			}
			else { //equal, must add
				Node tmp = new Node(p1coeff + p2coeff, p1deg, null);
				if(prev == null) {
					sumFront = tmp;
					prev = sumFront;
				}
				else {
					current = tmp;
					prev.next = current;
					prev = current;
				}
				poly1 = poly1.next;
				poly2 = poly2.next;
			}
		}
		//now at least one list is null - determine which
		Node remainder = null;
		if(poly1 == null && poly2 != null) { remainder = poly2; }
		else if(poly2 == null && poly1 != null) { remainder = poly1; }
		while(remainder != null) {
			Node tmp = new Node(remainder.term.coeff, remainder.term.degree, null);
			if(prev == null) {
				sumFront = tmp;
				prev = sumFront;
			}
			else {
				current = tmp;
				prev.next = current;
				prev = current;
			}
			remainder = remainder.next;
		}
		sumFront = removeZeroTerms(sumFront);
		return sumFront;
	}
	
	private static Node removeZeroTerms(Node head) {
		if(head == null) { return null; }

		Node ptr = head, prev = null;
		while(ptr!=null) {
			if(ptr.term.coeff == 0) {
				if(prev == null) {
					head = ptr.next;
				}
				else {
					prev.next = ptr.next;
				}
				
			}
			else {
				prev = ptr;
			}
			ptr = ptr.next;
		}
		return head;
	}
	
	private static void printList(Node head) {
		while(head!=null) {
			System.out.print(head.term.coeff + "^" + head.term.degree + " ");
			head = head.next;
		}
		System.out.println();
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	
	public static Node multiply(Node poly1, Node poly2) { 

		//edge cases
		if(poly1 == null || poly2 == null) {
			return null;
		}
		Node ptr1 = poly1, temp = null, tempPrev = null, productFront = null, current = null, prev = null;
		while(ptr1!=null) {
			Node ptr2 = poly2; //reset ptr2 to the head of poly2 after each iteration of the outer loop
			while(ptr2!=null) {
				Node newNode = new Node(ptr1.term.coeff * ptr2.term.coeff, ptr1.term.degree + ptr2.term.degree, null);
				if(prev==null) {
					temp = newNode;
					prev = temp;
				} else {
					current = newNode;
					prev.next = current;
					prev = current;
				}
				ptr2 = ptr2.next;
			}
			
			if(productFront!=null) {
				productFront = add(productFront, temp);

				prev = null;
				temp = null;
			} else {
				productFront = temp;
				prev = null;
				temp = null;
			}
			ptr1 = ptr1.next;
		}
		return productFront;
	}
	

		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {

		float total = 0;
		while(poly!=null) {
			float coeff = poly.term.coeff;
			int degree = poly.term.degree;
			total += coeff * Math.pow(x, degree);
			poly = poly.next;
		}
		return total;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
