package com.RuleEngine.re.service;

import com.RuleEngine.re.model.Node;
import com.RuleEngine.re.model.Rule;

import java.util.List;
import java.util.Map;

public interface RuleService {
    Rule saveRule(String ruleString);
    Rule getRule(Long id);
    List<Rule> getAllRules();
    Node combineRules(List<Long> ruleIds);
    boolean evaluateRule(Long ruleId, Map<String, Object> data);
    void deleteRule(Long id);
    Rule updateRule(Long id, Rule updatedRule);
}
