package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	
    	String[] strings = expr.split("[\\s]|[+]|[/]|[-]|[]]|[*]|[(]|[)]|[\\d]");
    	
    	for(String x: strings){
			if(x.length() == 0 || containsArray(arrays, x) || containsScalar(scalars, x))
    			continue;
    		else if(x.contains("[")){
    			String temp = x;
    			int endy = x.indexOf('[');
    			do{    				
    				if(temp.charAt(endy) == '[' && !(containsArray(arrays, temp.substring(0, endy))))
    					arrays.add(new ArraySymbol(temp.substring(0,endy)));
    				
    				temp = temp.substring(endy+1);
    				endy = temp.indexOf("[");
    			}while(endy > 0 || temp.contains("["));
    			if(temp.length() != 0 && !containsScalar(scalars, temp.substring(0)))
    				scalars.add(new ScalarSymbol(temp.substring(0)));
    		}
    		else
    			scalars.add(new ScalarSymbol(x));
    	}
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation
    		expr = expr.replaceAll("\\s", "");
    		return evaluate(expr, 0, expr.length()-1);
    }
    
    
    /**
     * 
     * Helper method, this uses recursion to evaluate the subexpressions
     * 
     * @return Result
     */
    private float evaluate(String expr, int start, int end) {
    	Stack<Float> oper = new Stack<Float>();
    	Stack<Character> rators = new Stack<Character>();
       	
    	for(int i = start; i <= end; i++){
    		if(Character.isLetter(expr.charAt(i))){
    			int st = i+1;
    			String var = expr.charAt(i)+"";
    			while(st <= end && Character.isLetter(expr.charAt(st))){
    				var += expr.charAt(st);
    				st++;
    			}
    			if(containsArray(arrays,var)){
        			int[] temp = findArray(arrays, var).values;
        			int endOfArray = endOfRay(expr, i+var.length());
        			oper.push((float)temp[(int)evaluate(expr,i+var.length(),endOfArray-1)]);
        			i = endOfArray;
        		}
    			else if(containsScalar(scalars,var)){
        			oper.push((float)findScalar(scalars,var).value);
        			i = st-1; //Check the Index if it works
        		}
        		
    		}
    		else if(isFloat(expr.charAt(i))){
    			int st = i+1;
    			String var = expr.charAt(i) + "";
    			while(st <= end && isFloat(expr.charAt(st))){
    				var += expr.charAt(st);
    				st++;
    			}
    			oper.push(Float.parseFloat(var));
    			i = st-1; // Check the Index if it works;
    		}
    		else if(expr.charAt(i) == '('){
    			int endOfArray = endOfRay(expr, i);
    			oper.push(evaluate(expr, i+1, endOfArray-1));
    			i = endOfArray;
    		}
    		if(!rators.isEmpty() && (rators.peek() == '/' || rators.peek() == '*')){
    			char andrea = rators.pop();
    			float b = oper.pop();
    			float a = oper.pop();
    			switch(andrea){
    			case '*':oper.push(a*b);
    				break;
    			case '/':oper.push(a/b);
    				break;
    			}
    		}
    		else if(expr.charAt(i) == '+' || expr.charAt(i) == '-')
    			rators.push(expr.charAt(i));
    		else if(expr.charAt(i) == '*' || expr.charAt(i) == '/')
    			rators.push(expr.charAt(i));
    	}
    	
    	Stack<Float> reoper = new Stack<Float>();
    	Stack<Character> rerators = new Stack<Character>();
    	while(!oper.isEmpty())
    		reoper.push(oper.pop());
    	while(!rators.isEmpty())
    		rerators.push(rators.pop());
    	while(reoper.size() > 1){
    		float b = reoper.pop();
			float a = reoper.pop();
			char andrea = rerators.pop();
			switch(andrea){
			case '+':reoper.push(b+a);
				break;
			case '-':reoper.push(b-a);
				break;
			}
    	}
    	return reoper.pop();
    }
    
    /**
     * finds the index of the closing bracket or parentheses
     * 
     * @param expression
     * @param start	of the bracket or parentheses
     * @return index of end of the closing bracket or parentheses
     */
    public int endOfRay(String e, int start){
    	Stack<Character> opens = new Stack<Character>();
    	opens.push(e.charAt(start));
    	int index = start + 1;
    	while(!opens.isEmpty()){
    		if(e.charAt(index) == '[' || e.charAt(index) == '(')
    			opens.push(e.charAt(index));
    		else if(e.charAt(index) == ']' || e.charAt(index) == ')')
    			opens.pop();
    		index++;
    	}
    	return index-1;
    }
    
    /**
     * Checks if blah is a digit
     * 
     * @param character
     * @return if the character is digit or decimal
     */
    private boolean isFloat(char blah){
    	return (blah>=48 && blah<=57) || blah == 46? true: false;
    }
    
    /**
     * Checks if the name of the ArraySymbol is already in the ArrayList
     * 
     * @param list of the Array ArrayList
     * @param name of the ArraySymbol
     * @return if the list contains the ArraySymbol
     */
    private boolean containsArray(ArrayList<ArraySymbol> list, String name){
    	for(ArraySymbol temp: list){
    		if(temp.name.equals(name))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Checks if the name of the Scalarsymbol is already in the ArrayList
     * 
     * @param list of the Scalar ArrayList
     * @param name of the Scalar Symbol
     * @return if list has the Scalar Symbol
     */
    private boolean containsScalar(ArrayList<ScalarSymbol> list, String name){
    	for(ScalarSymbol temp: list){
    		if(temp.name.equals(name))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Find the ArraySymbol with that name
     * 
     * @param list of the Array ArrayList
     * @param name of the Array Object
     * @return the existing ArraySymbol
     */
    private ArraySymbol findArray(ArrayList<ArraySymbol> list, String name){
    	ArraySymbol temp = null;
    	for(ArraySymbol t: list){
    		if(t.name.equals(name))
    			temp = t;
    	}
    	return temp;
    }
    
    /**
     * Finds the ScalarSymbol with that name
     * 
     * @param list of the Scalar ArrayList
     * @param name of the Scalar Object
     * @return the exiting ScalarSymbol
     */
    private ScalarSymbol findScalar(ArrayList<ScalarSymbol> list, String name){
    	ScalarSymbol temp = null;
    	for(ScalarSymbol t: list){
    		if(t.name.equals(name))
    			temp = t;
    	}
    	return temp;
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
