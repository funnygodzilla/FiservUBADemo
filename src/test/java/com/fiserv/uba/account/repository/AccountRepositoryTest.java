package com.fiserv.uba.account.repository;

import com.fiserv.uba.account.domain.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .accountNumber("ACC1707210600ABCD1234")
                .customerId(1001L)
                .accountType("CHECKING")
                .balance(new BigDecimal("5000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();
    }

    /*@Test
    void testSaveAccount() {
        Account savedAccount = accountRepository.save(account);

        assertNotNull(savedAccount);
        assertNotNull(savedAccount.getId());
        assertEquals("ACC1707210600ABCD1234", savedAccount.getAccountNumber());
    }*/

    /*@Test
    void testFindByAccountNumber_Success() {
        accountRepository.save(account);

        Optional<Account> found = accountRepository.findByAccountNumber("ACC1707210600ABCD1234");

        assertTrue(found.isPresent());
        assertEquals(account.getAccountNumber(), found.get().getAccountNumber());
        assertEquals(account.getCustomerId(), found.get().getCustomerId());
    }*/

    /*@Test
    void testFindByAccountNumber_NotFound() {
        Optional<Account> found = accountRepository.findByAccountNumber("INVALID_ACCOUNT");

        assertFalse(found.isPresent());
    }*/

    /*@Test
    void testFindByCustomerId_Success() {
        accountRepository.save(account);

        Account account2 = Account.builder()
                .accountNumber("ACC1707210601EFGH5678")
                .customerId(1001L)
                .accountType("SAVINGS")
                .balance(new BigDecimal("10000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();
        accountRepository.save(account2);

        List<Account> accounts = accountRepository.findByCustomerId(1001L);

        assertNotNull(accounts);
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().allMatch(a -> a.getCustomerId().equals(1001L)));
    }*/

    /*@Test
    void testFindByCustomerId_Empty() {
        List<Account> accounts = accountRepository.findByCustomerId(9999L);

        assertNotNull(accounts);
        assertEquals(0, accounts.size());
    }*/

    /*@Test
    void testUpdateAccount() {
        Account savedAccount = accountRepository.save(account);
        savedAccount.setBalance(new BigDecimal("6000.00"));
        savedAccount.setStatus("FROZEN");

        Account updatedAccount = accountRepository.save(savedAccount);

        assertEquals(new BigDecimal("6000.00"), updatedAccount.getBalance());
        assertEquals("FROZEN", updatedAccount.getStatus());
    }*/

    /*@Test
    void testDeleteAccount() {
        Account savedAccount = accountRepository.save(account);
        Long accountId = savedAccount.getId();

        accountRepository.deleteById(accountId);

        Optional<Account> found = accountRepository.findById(accountId);
        assertFalse(found.isPresent());
    }*/

    /*@Test
    void testFindById() {
        Account savedAccount = accountRepository.save(account);

        Optional<Account> found = accountRepository.findById(savedAccount.getId());

        assertTrue(found.isPresent());
        assertEquals(account.getAccountNumber(), found.get().getAccountNumber());
    }*/
}

