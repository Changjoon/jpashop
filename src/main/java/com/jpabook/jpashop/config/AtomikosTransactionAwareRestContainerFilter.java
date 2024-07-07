package com.jpabook.jpashop.config;

import com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter;
import org.springframework.stereotype.Component;

@Component
public class AtomikosTransactionAwareRestContainerFilter extends TransactionAwareRestContainerFilter {

}
