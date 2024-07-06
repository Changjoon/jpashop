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
}
//@RequiredArgsConstructor
//public class MemberRepository {
//
//    private final EntityManager em;
//
//    public void save(Member member) {
//        em.persist(member);
//    }
//
//    public Member findOne(Long id) {
//        return em.find(Member.class, id);
//    }
//
//    public List<Member> findAll() {
//        return em.createQuery("select m from Member m", Member.class)
//                .getResultList();
//    }
//
//    public List<Member> findByName(String name) {
//        return em.createQuery("select m from Member m where m.name = :name", Member.class)
//                .setParameter("name", name)
//                .getResultList();
//    }
//}
