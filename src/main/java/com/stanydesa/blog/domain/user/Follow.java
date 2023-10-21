package com.stanydesa.blog.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Builder
@Table(name = "user_follow")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {
    //Composite primary key (fromId + toID)
    //PRIMARY KEY (`from_id`,`to_id`)
    @EmbeddedId
    private FollowId id;

    @MapsId("fromId")
    @JoinColumn(name = "from_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User from;

    @MapsId("toId")
    @JoinColumn(name = "to_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User to;

    @CreatedDate
    @Builder.Default
    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Follow(User from, User to) {
        this.id = new FollowId(from.getId(), to.getId());
        this.from = from;
        this.to = to;
    }

    @PrePersist
    public void prePersist() {
        //this is there because whenever Follow object is being created, createdAt is null, which is raising error
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Follow other
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.from, other.from)
                && Objects.equals(this.to, other.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.from, this.to);
    }
}
