package com.fiserv.uba.account.service;

                import com.fiserv.uba.account.dto.AccountDTO;
                import com.fiserv.uba.account.dto.CreateAccountRequest;
                import com.fiserv.uba.account.dto.TransactionRequest;

                import java.util.List;

                public interface AccountService {
                    AccountDTO createAccount(CreateAccountRequest request);
                    AccountDTO getAccountByNumber(String accountNumber);
                    List<AccountDTO> getCustomerAccounts(Long customerId);
                    AccountDTO debitAccount(TransactionRequest request);
                    AccountDTO creditAccount(TransactionRequest request);
                    AccountDTO freezeAccount(String accountNumber);
                    AccountDTO unfreezeAccount(String accountNumber);
                }