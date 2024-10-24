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
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found with ID: " + ruleId);
        }

        try {
            String ruleString = rule.getRuleString();
            return evaluateRuleString(ruleString, data);
        } catch (Exception e) {
            log.error("Error evaluating rule [ID: {}]: {}", ruleId, e.getMessage(), e);
            throw new RuntimeException("Error evaluating rule", e);
        }
    }

    private boolean evaluateRuleString(String ruleString, Map<String, Object> data) {
        log.info("Evaluating rule string: {}", ruleString);

        // Split rule string into conditions and operators (assuming a simple format like "age > 30 AND salary >= 50000")
        String[] conditions = ruleString.split("\\s+(AND|OR)\\s+");
        String[] operators = ruleString.split("[^AND|OR]+");

        boolean result = evaluateCondition(conditions[0], data); // Evaluate the first condition

        for (int i = 1; i < conditions.length; i++) {
            String operator = operators[i].trim();
            boolean nextConditionResult = evaluateCondition(conditions[i], data);

            result = switch (operator) {
                case "AND" -> result && nextConditionResult;
                case "OR" -> result || nextConditionResult;
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        }

        return result;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        String[] parts = condition.trim().split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
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