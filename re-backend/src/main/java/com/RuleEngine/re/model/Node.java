package com.RuleEngine.re.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // "operator" or "operand"

    @Column(nullable = true)
    private String value; // Value for the operand nodes, e.g., a field name or a constant

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule; // Link back to the Rule

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "left_child_id")
    private Node left; // Reference to the left child node

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "right_child_id")
    private Node right; // Reference to the right child node

    // Constructor for operator nodes
    public Node(String type, String value, Node left, Node right) {
        this.type = type;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    // Constructor for operand nodes
    public Node(String type, String value) {
        this.type = type;
        this.value = value;
        this.left = null; // Operand nodes do not have children
        this.right = null; // Operand nodes do not have children
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(id, node.id) &&
                Objects.equals(type, node.type) &&
                Objects.equals(value, node.value) &&
                Objects.equals(rule, node.rule) &&
                Objects.equals(left, node.left) &&
                Objects.equals(right, node.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, value, rule, left, right);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", left=" + left +
                ", right=" + right +
                '}';
    }



}
