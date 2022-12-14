package com.sihoo.me.debook.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Table(name = "reply", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_review_id", columnList = "review_id")
})
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name= "target_reply_id")
    private Long targetReplyId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @CreatedDate
    @Column(insertable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void changeMessage(Reply reply) {
        this.message = reply.message;
    }

    public void deleteReply() {
        this.isDeleted = true;
    }


}
