package com.study.controller;

import com.study.dto.SignUpFormDto;
import com.study.dto.SignUpFormValidator;
import com.study.entity.Account;
import com.study.repository.AccountRepository;
import com.study.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    /*@InitBinder("signUpFormDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }*/

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpFormDto", new SignUpFormDto());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpFormDto signUpFormDto, Errors errors) {
        if(errors.hasErrors()) {
            return "account/sign-up";
        }

        accountService.processNewAccount(signUpFormDto);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if(account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        if(!account.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return view;
        }
        account.completeSignUp();
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

}
