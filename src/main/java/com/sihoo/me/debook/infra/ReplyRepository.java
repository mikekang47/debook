package com.sihoo.me.debook.infra;

import com.sihoo.me.debook.domains.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyCustomRepository {
}
