package com.RuleEngine.re.util;

import com.RuleEngine.re.model.Node;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class RuleParser {

    public Node createRule(String ruleString) {
        // Remove unnecessary quotes around the whole string
        if (ruleString.startsWith("\"") && ruleString.endsWith("\"")) {
            ruleString = ruleString.substring(1, ruleString.length() - 1);
        }

        String[] tokens = tokenize(ruleString);
        printTokens(tokens); // Debugging step to ensure correct tokenization
        return parseExpression(tokens);
    }

    private void printTokens(String[] tokens) {
        System.out.println("Tokens: ");
        for (String token : tokens) {
            System.out.println("'" + token + "'");
        }
    }

    private String[] tokenize(String ruleString) {
        // Normalize whitespace and ensure spaces around parentheses and operators
        ruleString = ruleString.replaceAll("\\s+", " "); // Normalize multiple spaces to a single space

        // Add spaces around parentheses
        ruleString = ruleString.replaceAll("([()])", " $1 ");

        // Ensure operators like >, <, = are separated by spaces
        ruleString = ruleString.replaceAll("([><=])", " $1 ");

        // Split based on spaces
        return ruleString.trim().split("\\s+");
    }

    private Node parseExpression(String[] tokens) {
        Stack<Node> operands = new Stack<>();
        Stack<String> operators = new Stack<>();
        int parenthesesBalance = 0; // Keep track of open/close parentheses

        for (String token : tokens) {
            System.out.println("Processing token: " + token); // Debugging statement

            if (token.equals("(")) {
                operators.push(token);
                parenthesesBalance++; // Increment for opening parentheses
            } else if (token.equals(")")) {
                parenthesesBalance--; // Decrement for closing parentheses
                if (parenthesesBalance < 0) {
                    throw new IllegalArgumentException("Mismatched parentheses: No matching opening parenthesis for ')'.");
                }
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    processOperator(operators, operands);
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Remove the '('
                } else {
                    throw new IllegalArgumentException("Mismatched parentheses: No matching opening parenthesis.");
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    processOperator(operators, operands);
                }
                operators.push(token);
            } else {
                // Handle operands (like "salary", "50000", "'Sales'")
                if (!token.isEmpty()) {
                    operands.push(new Node("operand", token));
                } else {
                    throw new IllegalArgumentException("Empty operand encountered.");
                }
            }
        }

        // Final check for unbalanced parentheses
        if (parenthesesBalance != 0) {
            throw new IllegalArgumentException("Mismatched parentheses in the rule: Unbalanced '(' and ')'.");
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            if (operators.peek().equals("(")) {
                throw new IllegalArgumentException("Mismatched parentheses: Unmatched opening parenthesis.");
            }
            processOperator(operators, operands);
        }

        return operands.pop(); // Return the root of the AST
    }




    private void processOperator(Stack<String> operators, Stack<Node> operands) {
        String operator = operators.pop();
        Node right = operands.pop();
        Node left = operands.pop();
        operands.push(createNode(operator, left, right));
    }

    private Node createNode(String operator, Node left, Node right) {
        return new Node("operator", operator, left, right);
    }

    private boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR") ||
                token.equals(">") || token.equals("<") ||
                token.equals("=") || token.equals("!=");
    }

    private int precedence(String operator) {
        switch (operator) {
            case "AND":
                return 1; // Higher precedence than OR
            case "OR":
                return 0; // Lower precedence than AND
            case ">":
            case "<":
            case "=":
            case "!=":
                return 2; // Highest precedence for comparison operators
            default:
                return -1; // Non-operators or unknown
        }
    }
}