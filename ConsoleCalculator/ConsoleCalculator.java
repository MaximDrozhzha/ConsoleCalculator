package ConsoleCalculator;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ConsoleCalculator {

	public static void main(String[] args) {
		
		try (Scanner in = new Scanner(System.in)) {
			System.out.println(
					"Enter an expression. Supported: numbers, operations +,-,*,/,^ and priorities ( and ):\"");
			String s = in.nextLine();
			List<String> postfixExpr = convertToPostfixNotation(s);

			for (String x : postfixExpr) {
				System.out.print(x + " ");
			}
			System.out.println();

			System.out.println(calculate(postfixExpr));

		} catch (Exception e) {
			System.out.println("An exception occurred: " + e.getMessage());
		}
	}

	static List<String> convertToPostfixNotation(String infixExpr) throws Exception {

		infixExpr = infixExpr.replace(',', '.');

		List<String> postfix = new ArrayList<String>();
		Deque<String> stack = new ArrayDeque<String>();
		StringTokenizer tokenizer = new StringTokenizer(infixExpr, "+-*/^() ", true);
		String prev = "";
		String current = "";
		while (tokenizer.hasMoreTokens()) {
			current = tokenizer.nextToken();
			if (!tokenizer.hasMoreTokens() && isOperator(current)) {
				throw new Exception("Incorrect expression.");
			}
			if (current.equals(" "))
				continue;

			if (isDelimiter(current)) {
				if (current.equals("("))
					stack.push(current);
				else if (current.equals(")")) {
					while (!stack.peek().equals("(")) {
						postfix.add(stack.pop());
						if (stack.isEmpty()) {
							throw new Exception("Brackets has not been agreed.");
						}
					}
					stack.pop();
					if (!stack.isEmpty()) {
						postfix.add(stack.pop());
					}
				} else {
					if (current.equals("-") && (prev.equals("") || (isDelimiter(prev) && !prev.equals(")")))) {
						// unary minus
						current = "u-";
					} else {
						while (!stack.isEmpty() && (priority(current) <= priority(stack.peek()))) {
							postfix.add(stack.pop());
						}
					}
					stack.push(current);
				}
			}

			else {
				postfix.add(current);
			}
			prev = current;
		}

		while (!stack.isEmpty()) {
			if (isOperator(stack.peek()))
				postfix.add(stack.pop());
			else {
				throw new Exception("Brackets has not been agreed.");
			}
		}
		return postfix;
	}

	private static boolean isDelimiter(String token) {
		String delimiters = "+-*/^() ";
		if (token.length() != 1)
			return false;
		for (int i = 0; i < delimiters.length(); i++) {
			if (token.charAt(0) == delimiters.charAt(i))
				return true;
		}
		return false;
	}

	private static boolean isOperator(String token) {
		String operators = "+-*/^";
		if (token.equals("u-"))
			return true;
		for (int i = 0; i < operators.length(); i++) {
			if (token.charAt(0) == operators.charAt(i))
				return true;
		}
		return false;
	}

	private static int priority(String token) {
		switch (token) {
		case "u-":
			return 5;
		case "^":
			return 4;
		case "*":
		case "/":
			return 3;
		case "+":
		case "-":
			return 2;
		}
		return 1;
	}

	public static BigDecimal calculate(List<String> postfix) throws Exception {
		Deque<BigDecimal> stack = new ArrayDeque<>();
		BigDecimal a, b;
		try {
			for (String x : postfix) {
				switch (x) {
				case ("+"):
					stack.push(stack.pop().add(stack.pop()));
					break;
				case ("-"):
					b = stack.pop();
					a = stack.pop();
					stack.push(a.subtract(b));
					break;
				case ("*"):
					stack.push(stack.pop().multiply(stack.pop()));
					break;
				case ("/"):
					b = stack.pop();
					a = stack.pop();
					stack.push(a.divide(b));
					break;
				case ("u-"):
					b = new BigDecimal(0);
					stack.push(b.subtract(stack.pop()));
					break;
				case ("^"):
					b = stack.pop();
					a = stack.pop();
					stack.push(a.pow(b.intValue(), new MathContext(4)));
					break;
				default:
					stack.push(new BigDecimal(x));
				}
			}

			if (stack.size() > 1) {
				throw new Exception("The number of operators does not match the number of operands");
			}

			return stack.pop();

		} catch (ArithmeticException e) {
			throw new ArithmeticException("Arithmetic Exception");
		} catch (NumberFormatException e) {
			throw new NumberFormatException("The expression contains invalid characters");
		}
	}

}
