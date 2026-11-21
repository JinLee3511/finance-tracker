package com.github.jinlee3511.finance_tracker.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
/*
Should have Public or Protected access level default constructor
-> Due to JPA Spec

Used Protected to not allow default constructor outside the package..
-> Also in order for Hibernate to extend from parent class
--> Hibernate uses Proxy (fake class: only get real data when actually needed! -> Lazy Loading)
--> Proxy extends the real Entity (Category) -> Required to call parent constructor -> cannot be private! so Protected
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB creates the ID automatically (AUTO_INCREMENT)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // In order to force input NAME field..
    public Category(String name) {
        validateName(name);
        this.name = convertNameFormat(name);
    }

    public void changeName(String name) {
        validateName(name);
        this.name = convertNameFormat(name);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name Cannot be empty");
        }
    }

    private String convertNameFormat(String name) {
        String stripped = name.strip();
        return stripped.substring(0, 1).toUpperCase() + stripped.substring(1).toLowerCase();
    }
}
