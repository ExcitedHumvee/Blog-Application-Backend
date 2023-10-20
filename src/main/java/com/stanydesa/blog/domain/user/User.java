package com.stanydesa.blog.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    //FetchType is LAZY or EAGER, in EAGER, the when you load an entity, JPA will automatically fetch its associated entities or collections from the database as well
    //LAZY, the associated entity or collection should be loaded lazily, i.e., only when it is explicitly accessed
    //CascadeType ALL means PERSIST, MERGE, REMOVE, REFRESH, DETACH
    //when user is persisted (all Follower will also be persisted), merge means update, refresh means reloaded, detach is deleted
    //Refresh, suppose some other part of the system is updating user, then state of user will have to get updated (usual select statement)
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "from", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> following = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "to", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> follower = new HashSet<>();

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

    public void updateEmail(String email) {
        if (email == null || email.isBlank() || this.email.equals(email)) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.email = email;
    }

    public void updateUsername(String username) {
        if (username == null || username.isBlank() || this.username.equals(username)) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.username = username;
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String plaintext) {
        if (passwordEncoder == null) {
            return;
        }

        if (plaintext == null || plaintext.isBlank()) {
            return;
        }

        // Note: You can add some more validations here if you want. (ex. regex)
        this.password = passwordEncoder.encode(plaintext);
    }

    public void updateBio(String bio) {
        if (bio == null || bio.isBlank()) {
            return;
        }

        this.bio = bio;
    }

    public void updateImage(String imageUrl) {
        if (imageUrl != null && imageUrl.isBlank()) {
            return;
        }

        this.image = imageUrl;
    }

    public boolean isAlreadyFollowing(User target) {
        if (target == null || target.isAnonymous()) {
            throw new IllegalArgumentException("target must not be null or anonymous");
        }

        Follow follow = new Follow(this, target);
        return this.following.stream().anyMatch(follow::equals);//OR (f -> follow.equals(f)) f is element of the stream

        //OR (below same time complexity)
//        // Iterate through each Follow object in the set
//        for (Follow existingFollow : this.following) {
//            // Check if the current Follow object is equal to the new Follow object
//            if (existingFollow.equals(follow)) {
//                return true; // Already following
//            }
//        }
//
//        return false; // Not following
    }
}
