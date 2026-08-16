package com.autoprtz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoprtz.entity.AccountUser;
import com.autoprtz.repository.AccountUserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AccountsAuthController {

    private static final String OWNER_USERNAME = "Prasanna Juvvi";

    private static final String DEFAULT_OWNER_PASSWORD =
            "PJ@Autoprtz@1359";

    private static final String CHANGE_PIN =
            "Prasanna@autoprtz@1359";

    @Autowired
    private AccountUserRepository accountUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // =========================================================
    // LOGIN PAGE
    // =========================================================

    @GetMapping("/accounts/login")
    public String loginPage(HttpSession session) {

        if (session.getAttribute("accountUserId") != null) {
            return "redirect:/accounts";
        }

        return "accounts-login";
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/accounts/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        String enteredUsername =
                username == null ? "" : username.trim();

        String enteredPassword =
                password == null ? "" : password;


        // =====================================================
        // OWNER LOGIN
        // =====================================================

        if (OWNER_USERNAME.equals(enteredUsername)
                && DEFAULT_OWNER_PASSWORD.equals(enteredPassword)) {

            session.setAttribute(
                    "accountUserId",
                    "OWNER");

            session.setAttribute(
                    "accountUsername",
                    OWNER_USERNAME);

            session.setAttribute(
                    "accountOwner",
                    true);

            return "redirect:/accounts";
        }


        // =====================================================
        // DATABASE USER LOGIN
        // =====================================================

        AccountUser user =
                accountUserRepository
                        .findByUsername(enteredUsername)
                        .orElse(null);


        if (user != null
                && user.isActive()
                && user.getPasswordHash() != null
                && passwordEncoder.matches(
                        enteredPassword,
                        user.getPasswordHash())) {

            session.setAttribute(
                    "accountUserId",
                    user.getId());

            session.setAttribute(
                    "accountUsername",
                    user.getUsername());

            session.setAttribute(
                    "accountOwner",
                    user.isOwner());

            return "redirect:/accounts";
        }


        // =====================================================
        // INVALID LOGIN
        // =====================================================

        model.addAttribute(
                "errorMessage",
                "Invalid username or password");

        return "accounts-login";
    }


    // =========================================================
    // USER CONTROL
    // =========================================================

    @GetMapping("/accounts/users")
    public String usersPage(
            HttpSession session,
            Model model) {

        if (!isOwner(session)) {
            return "redirect:/accounts";
        }

        model.addAttribute(
                "users",
                accountUserRepository.findAll());

        return "accounts-users";
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    @PostMapping("/accounts/users/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        if (!isOwner(session)) {
            return "redirect:/accounts";
        }


        String newUsername =
                username == null ? "" : username.trim();

        String newPassword =
                password == null ? "" : password;


        if (newUsername.isEmpty()
                || newPassword.isEmpty()) {

            return "redirect:/accounts/users";
        }


        if (OWNER_USERNAME.equalsIgnoreCase(newUsername)) {
            return "redirect:/accounts/users";
        }


        if (accountUserRepository
                .existsByUsername(newUsername)) {

            return "redirect:/accounts/users";
        }


        AccountUser user =
                new AccountUser();

        user.setUsername(newUsername);

        // IMPORTANT:
        // Password must be BCrypt encoded

        user.setPasswordHash(
                passwordEncoder.encode(newPassword));

        user.setSecurityPinHash(
                passwordEncoder.encode(CHANGE_PIN));

        user.setOwner(false);

        user.setActive(true);


        accountUserRepository.save(user);


        return "redirect:/accounts/users";
    }


    // =========================================================
    // ENABLE / DISABLE USER
    // =========================================================

    @PostMapping("/accounts/users/toggle")
    public String toggleUser(
            @RequestParam Long id,
            HttpSession session) {

        if (!isOwner(session)) {
            return "redirect:/accounts";
        }


        accountUserRepository
                .findById(id)
                .ifPresent(user -> {

                    if (!user.isOwner()) {

                        user.setActive(
                                !user.isActive());

                        accountUserRepository.save(user);
                    }

                });


        return "redirect:/accounts/users";
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    @PostMapping("/accounts/users/delete")
    public String deleteUser(
            @RequestParam Long id,
            HttpSession session) {

        if (!isOwner(session)) {
            return "redirect:/accounts";
        }


        accountUserRepository
                .findById(id)
                .ifPresent(user -> {

                    if (!user.isOwner()) {

                        accountUserRepository
                                .delete(user);
                    }

                });


        return "redirect:/accounts/users";
    }


    // =========================================================
    // CHANGE OWNER PASSWORD
    // =========================================================

    @PostMapping("/accounts/owner/change-password")
    public String changeOwnerPassword(
            @RequestParam String pin,
            @RequestParam String newPassword,
            HttpSession session) {

        if (!isOwner(session)) {
            return "redirect:/accounts";
        }


        if (!CHANGE_PIN.equals(pin)) {
            return "redirect:/accounts";
        }


        if (newPassword == null
                || newPassword.trim().isEmpty()) {

            return "redirect:/accounts";
        }


        /*
         * NOTE:
         * Current owner password is kept as the existing
         * application password.
         */

        return "redirect:/accounts";
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @GetMapping("/accounts/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/accounts/login";
    }


    // =========================================================
    // OWNER CHECK
    // =========================================================

    private boolean isOwner(
            HttpSession session) {

        return Boolean.TRUE.equals(
                session.getAttribute(
                        "accountOwner"));
    }
}