package com.sihoo.me.debook.domains;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "review_count")
    private int reviewCount;

    @Column(name = "reply_count")
    private int replyCount;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public void changeStatus(boolean status) {
        this.isDeleted = status;
    }

    public void changeWith(User user, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(user.password);
        this.githubId = user.githubId;
        this.nickName = user.nickName;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void increaseReviewCount() {
        this.reviewCount += 1;
    }

    public void decreaseReviewCount() {
        this.reviewCount -= 1;
    }

    public void increaseReplyCount() {
        this.replyCount += 1;
    }

    public void decreaseReplyCount() {
        this.replyCount -= 1;
    }

    public boolean authenticate(String password, PasswordEncoder passwordEncoder) {
        return !isDeleted && passwordEncoder.matches(password, this.password);
    }
}
