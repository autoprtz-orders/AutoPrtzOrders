package com.autoprtz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoprtz.entity.AccountUser;
import com.autoprtz.repository.AccountUserRepository;

@Controller
public class AccountSetupController {

    @Autowired
    private AccountUserRepository accountUserRepository;


    // =========================
    // OWNER SETUP PAGE
    // =========================

    @GetMapping("/accounts/setup")
    public String setupPage(Model model) {

        if (accountUserRepository.count() > 0) {
            return "redirect:/accounts/login";
        }

        return "account-setup";
    }


    // =========================
    // CREATE OWNER ACCOUNT
    // =========================

    @PostMapping("/accounts/setup")
    public String createOwner(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String securityPin,
            Model model) {

        if (accountUserRepository.count() > 0) {
            return "redirect:/accounts/login";
        }


        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.isEmpty()
                || securityPin == null
                || securityPin.isEmpty()) {

            model.addAttribute(
                    "error",
                    "All fields are required."
            );

            return "account-setup";
        }


        AccountUser user = new AccountUser();

        user.setUsername(username.trim());

        /*
         * TEMPORARY ONLY.
         * Secure hashing will be added after the
         * application starts correctly.
         */
        user.setPasswordHash(password);

        user.setSecurityPinHash(securityPin);

        user.setOwner(true);

        user.setActive(true);

        accountUserRepository.save(user);


        return "redirect:/accounts/login";
    }
}