package com.jpabook.jpashop.controller;

import com.jpabook.jpashop.service.MemberService;
import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Member;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) throws HeuristicRollbackException, SystemException, HeuristicMixedException, NotSupportedException {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }

    @GetMapping("/members/latest")
    public ResponseEntity<Member> latestMember() {
        Member member = memberService.findLatestMembers();
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @DeleteMapping("/members/latest")
    public ResponseEntity<Member> deleteLatestMember() {
        Member member = memberService.findLatestMembers();
        memberService.delete(member.getId());
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}
