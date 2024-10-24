package com.RuleEngine.re.service.impl;

import com.RuleEngine.re.controller.RuleController;
import com.RuleEngine.re.exception.ResourceNotFoundException;
import com.RuleEngine.re.model.Node;
import com.RuleEngine.re.model.Rule;
import com.RuleEngine.re.repository.RuleRepository;
import com.RuleEngine.re.service.RuleService;

import com.RuleEngine.re.util.RuleParser;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RuleServiceImpl implements RuleService {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(RuleController.class);

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleParser ruleParser;

    @Override
    public Rule saveRule(String ruleString) {
        try {
            // Parse the rule string to create the AST
            Node root = ruleParser.createRule(ruleString);

            // Create a new Rule object and set its properties
            Rule rule = new Rule();
            rule.setRuleString(ruleString);
            rule.setAst(root); // Set the AST in the Rule object
            

            // Save the rule to the repository
            return ruleRepository.save(rule);
        } catch (Exception e) {
            log.error("Error saving rule: ", e); // Log full stack trace
            throw new RuntimeException("Failed to save rule due to parsing error: " + e.getMessage());
        }
    }


    @Override
    public Rule getRule(Long id) {
        return ruleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public Node combineRules(List<Long> ruleIds) {
        List<Rule> rules = ruleRepository.findAllById(ruleIds);
        Node root = null;

        // Use a set to avoid duplicates if needed
        Set<String> seenRules = new HashSet<>();

        for (Rule rule : rules) {
            String ruleString = rule.getRuleString();

            // Check for duplicates
            if (seenRules.add(ruleString)) { // Only add if not already present
                Node ruleNode = ruleParser.createRule(ruleString);
                root = (root == null) ? ruleNode : new Node("operator", "AND", root, ruleNode);
            }
        }

        return root; // Returns the combined AST
    }


    // Service method to evaluate the rule

    @Override
    public boolean evaluateRule(Long ruleId, Map<String, Object> data) {
        Rule rule = getRule(ruleId);
        log.info("Retrieved rule: {}", rule);
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found with ID: " + ruleId);
        }

        try {
            Node ruleNode = rule.getAst();

            return evaluateNode(ruleNode, data);
        } catch (Exception e) {
            log.error("Error evaluating rule [ID: {}]: {}", ruleId, e.getMessage(), e);
            throw new RuntimeException("Error evaluating rule", e);
        }
    }

    private boolean evaluateNode(Node node, Map<String, Object> data) {
        if (node == null) {
            return false; // or handle according to your requirement
        }

        // Evaluate based on the node type
        if ("operand".equals(node.getType())) {
            return evaluateCondition(node, data);
        } else if ("operator".equals(node.getType())) {
            boolean leftResult = evaluateNode(node.getLeft(), data);
            boolean rightResult = evaluateNode(node.getRight(), data);
            return evaluateOperator(node.getValue(), leftResult, rightResult);
        }

        throw new IllegalArgumentException("Unknown node type: " + node.getType());
    }

    private boolean evaluateOperator(String operator, boolean leftResult, boolean rightResult) {
        return switch (operator) {
            case "AND" -> leftResult && rightResult;
            case "OR" -> leftResult || rightResult;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    private boolean evaluateCondition(Node conditionNode, Map<String, Object> data) {
        String[] parts = conditionNode.getValue().trim().split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + conditionNode.getValue());
        }

        String attribute = parts[0];
        String operator = parts[1];
        String valueStr = parts[2].replace("'", ""); // Remove quotes for comparison

        Object attributeValue = data.get(attribute);
        if (attributeValue == null) {
            throw new IllegalArgumentException("Attribute not found in data: " + attribute);
        }

        // Handle number comparisons
        if (attributeValue instanceof Number numberValue) {
            double number = numberValue.doubleValue();
            double comparisonValue = Double.parseDouble(valueStr);
            return switch (operator) {
                case ">" -> number > comparisonValue;
                case "<" -> number < comparisonValue;
                case "=" -> number == comparisonValue;
                case "!=" -> number != comparisonValue;
                case ">=" -> number >= comparisonValue;
                case "<=" -> number <= comparisonValue;
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        }

        // Handle string comparisons
        else if (attributeValue instanceof String stringValue) {
            return switch (operator) {
                case "=" -> stringValue.equals(valueStr);
                case "!=" -> !stringValue.equals(valueStr);
                default -> throw new IllegalArgumentException("Unsupported operator for string: " + operator);
            };
        }

        throw new IllegalArgumentException("Unsupported attribute type: " + attribute);
    }

    @Override
    public Rule updateRule(Long id, Rule updatedRule) {
        Rule existingRule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + id));

        existingRule.setRuleString(updatedRule.getRuleString());

        return ruleRepository.save(existingRule);
    }

    @Override
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }
}
