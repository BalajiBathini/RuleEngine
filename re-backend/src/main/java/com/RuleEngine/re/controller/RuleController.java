package com.RuleEngine.re.controller;

import com.RuleEngine.re.model.Node;
import com.RuleEngine.re.model.Rule;
import com.RuleEngine.re.service.RuleService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/rules")
public class RuleController {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(RuleController.class);

    @Autowired
    private RuleService ruleService;

    @PostMapping("/create")
    public ResponseEntity<Rule> createRule(@RequestBody String ruleString) {
        Rule createdRule = ruleService.saveRule(ruleString);
        return ResponseEntity.ok(createdRule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRule(@PathVariable Long id) {
        Rule rule = ruleService.getRule(id);
        if (rule != null) {
            return ResponseEntity.ok(rule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Rule>> getAllRules() {
        List<Rule> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @PostMapping("/combine")
    public ResponseEntity<Node> combineRules(@RequestBody List<Long> ruleIds) {
        Node combinedNode = ruleService.combineRules(ruleIds);
        return ResponseEntity.ok(combinedNode);
    }

    @PostMapping("/{id}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateRule(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            log.info("Evaluating rule with ID: {}", id);
            boolean result = ruleService.evaluateRule(id, data);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (NoSuchElementException e) { // Assuming you throw this if rule is not found
            log.error("Rule not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.error("Error evaluating rule: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during rule evaluation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody Rule updatedRule) {
        Rule rule = ruleService.updateRule(id, updatedRule);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
