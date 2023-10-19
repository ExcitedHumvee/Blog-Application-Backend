package com.stanydesa.blog.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Builder//build is better than setter because more readability, u dont need many set() arguments
//also in builder, its immutable, its creates new instance after each modification, more safe
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)//used in conjunction with @CreatedDate to automatically populate createdDate
@AllArgsConstructor(access = AccessLevel.PRIVATE)//By setting the access level of the all-args constructor to private, the class encapsulates its internal details.
//External classes or components are discouraged from directly creating instances of User using the all-args constructor.
// Instead, they are expected to use the builder pattern or other provided methods
@NoArgsConstructor(access = AccessLevel.PROTECTED)//Hibernate/ORM compatibility
//RM frameworks use reflection to instantiate objects and populate their fields. A no-args constructor, often with at least protected visibility, is needed for this purpose.
//Making the no-args constructor protected allows subclasses to call it
//Inheritance is a common practice in JPA entities, and subclasses may need to initialize their state or invoke the no-args constructor during their own construction process
//By setting the access level to protected, the class retains control over how it is instantiated. It signals that the no-args constructor is intended for use by subclasses and the ORM framework
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)//Universally Unique Identifier
    private UUID id;

    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Builder.Default
    @Column(length = 500, nullable = false)
    private String bio = "";

    private String image;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient//field should not be persisted to the database
    private String token;

    @Transient
    @Builder.Default
    //way of representing User if not logged in
    //default permissions
    //track these users
    private boolean anonymous = false;

    public User setToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token must not be null or blank");
        }

        this.token = token;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User other && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public static User anonymous() {
        return User.builder().id(null).anonymous(true).build();
    }
}
