package com.jpabook.jpashop.repository;

import com.jpabook.jpashop.domain.Member;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByName(String name);

    default Member findOne(Long id) {
        return this.findById(id).orElse(null);
    }

    Member findTopByOrderByIdDesc();
}