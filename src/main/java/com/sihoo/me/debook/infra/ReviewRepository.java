package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
