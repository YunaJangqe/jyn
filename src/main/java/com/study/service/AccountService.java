package com.study.service;

import com.study.dto.SignUpFormDto;
import com.study.entity.Account;
import com.study.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpFormDto signUpFormDto) {
        Account newAccount = saveNewAccount(signUpFormDto);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
    }

    private Account saveNewAccount(SignUpFormDto signUpFormDto) {
        Account account = Account.builder()
                .email(signUpFormDto.getEmail())
                .nickname(signUpFormDto.getNickname())
                .password(signUpFormDto.getPassword())
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디, 회원가입 인증");
        mailMessage.setText("check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }
}
