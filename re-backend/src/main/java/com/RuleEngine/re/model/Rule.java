package com.RuleEngine.re.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruleString;

    @OneToOne(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Node ast; // Link to the AST Node

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rule rule)) return false;
        return Objects.equals(id, rule.id) &&
                Objects.equals(ruleString, rule.ruleString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ruleString);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", ruleString='" + ruleString + '\'' +
                '}';
    }

}
