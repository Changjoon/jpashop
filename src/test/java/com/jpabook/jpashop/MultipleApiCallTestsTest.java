package com.jpabook.jpashop;

import com.jpabook.jpashop.controller.MemberForm;
import com.jpabook.jpashop.domain.Address;
import com.jpabook.jpashop.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MultipleApiCallTestsTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @Transactional
    //@Rollback(false)    // com.atomikos.icatch.HeurHazardException: Heuristic Hazard Exception
    public void multiplePostApiCallTest() {
        MemberForm memberForm = new MemberForm();
        memberForm.setName("John Doe");
        memberForm.setCity("New York");
        memberForm.setStreet("5th Avenue");
        memberForm.setZipcode("10001");

        ResponseEntity<String> responseSeoul = restTemplate.postForEntity("http://localhost:8091/members/member", memberForm, String.class);
        ResponseEntity<String> responseBusan = restTemplate.postForEntity("http://localhost:8092/members/member", memberForm, String.class);
        assertNotNull(responseSeoul);
        assertNotNull(responseBusan);
    }

    @Test
    @Transactional
    public void ChainedApiCallTest() {
        MemberForm memberForm = new MemberForm();
        memberForm.setName("Jane Doe");
        memberForm.setCity("Los Angeles");
        memberForm.setStreet("Hollywood Blvd");
        memberForm.setZipcode("90001");
        Member expectedMember = new Member();
        expectedMember.setName(memberForm.getName());
        expectedMember.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8091/members/chain", memberForm, String.class);
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Member created successfully"));
    }

    @Test
    public void ParitialTransactionChaindApiCallTest() {
        MemberForm memberForm = new MemberForm();
        memberForm.setName("Jane Doe");
        memberForm.setCity("Los Angeles");
        memberForm.setStreet("Hollywood Blvd");
        memberForm.setZipcode("90001");
        Member expectedMember = new Member();
        expectedMember.setName(memberForm.getName());
        expectedMember.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                restTemplate.postForEntity("http://localhost:8091/members/chain", memberForm, String.class);
            }
        });
        Member latestMember = restTemplate.getForObject("http://localhost:8092/members/latest", Member.class);
        assertEquals(expectedMember.getName(), latestMember.getName());
        assertEquals(expectedMember.getAddress().getCity(), latestMember.getAddress().getCity());
        assertEquals(expectedMember.getAddress().getStreet(), latestMember.getAddress().getStreet());
        assertEquals(expectedMember.getAddress().getZipcode(), latestMember.getAddress().getZipcode());
    }

    @Test
    public void ParitialTransactionWithExceptionChaindApiCallTest() {
        MemberForm memberForm = new MemberForm();
        memberForm.setName("Exception Doe");
        memberForm.setCity("Los Angeles");
        memberForm.setStreet("Hollywood Blvd");
        memberForm.setZipcode("90001");
        Member expectedMember = new Member();
        expectedMember.setName(memberForm.getName());
        expectedMember.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    restTemplate.postForEntity("http://localhost:8091/members/chain", memberForm, String.class);
                    if (true) {
                        throw new RuntimeException("Rollback");
                    }
                }
            });
        } catch (Exception e) {
            assertEquals("Rollback", e.getMessage());
        }
        Member latestMember = restTemplate.getForObject("http://localhost:8092/members/latest", Member.class);
        assertNotEquals(expectedMember.getName(), latestMember.getName());
    }

    @Test
    public void MultipleGetApiCallTest() {
        MemberForm memberForm = new MemberForm();
        memberForm.setName("Test for Get");
        memberForm.setCity("Los Angeles");
        memberForm.setStreet("Hollywood Blvd");
        memberForm.setZipcode("90001");

        Member expectedMember = new Member();
        expectedMember.setName(memberForm.getName());
        expectedMember.setAddress(new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode()));

        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    restTemplate.postForEntity("http://localhost:8091/members/member", memberForm, String.class);
                    restTemplate.postForEntity("http://localhost:8092/members/member", memberForm, String.class);
                }
            });
        } catch (Exception e) {
            fail("Exception occurred");
        }
        Member memberSeoul = restTemplate.getForObject("http://localhost:8091/members/latest", Member.class);
        Member memberBusan = restTemplate.getForObject("http://localhost:8092/members/latest", Member.class);

        assertEquals(expectedMember.getName(), memberSeoul.getName());
        assertEquals(expectedMember.getName(), memberBusan.getName());
    }
}