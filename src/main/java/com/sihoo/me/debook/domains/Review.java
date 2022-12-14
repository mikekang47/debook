package com.sihoo.me.debook.domains;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review", indexes = {
        @Index(name = "idx_title_body", columnList = "title, body"),
        @Index(name = "idx_book_id", columnList = "book_id")
})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name= "book_id")
    private Long bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "reply_count")
    private int replyCount;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void changeWith(Review review) {
        this.title = review.title;
        this.body = review.body;
    }

}
