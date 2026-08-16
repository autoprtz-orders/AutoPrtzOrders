package com.autoprtz.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoprtz.entity.BankBalance;
import com.autoprtz.entity.ExtraAmount;
import com.autoprtz.entity.PendingAmount;
import com.autoprtz.repository.BankBalanceRepository;
import com.autoprtz.repository.ExtraAmountRepository;
import com.autoprtz.repository.PendingAmountRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountsController {

    @Autowired
    private BankBalanceRepository bankBalanceRepository;

    @Autowired
    private PendingAmountRepository pendingAmountRepository;

    @Autowired
    private ExtraAmountRepository extraAmountRepository;


    // =========================================================
    // ACCOUNTS DASHBOARD
    // =========================================================

    @GetMapping("/accounts")
    public String accounts(
            @RequestParam(required = false) String search,
            HttpSession session,
            Model model) {

        // LOGIN CHECK
        Object userId = session.getAttribute("accountUserId");

        if (userId == null) {
            return "redirect:/accounts/login";
        }


        // USER INFORMATION

        Object username =
                session.getAttribute("accountUsername");

        Object owner =
                session.getAttribute("accountOwner");

        boolean isOwner =
                Boolean.TRUE.equals(owner);


        model.addAttribute(
                "username",
                username
        );

        model.addAttribute(
                "owner",
                isOwner
        );


        // =====================================================
        // SEARCH
        // =====================================================

        boolean hasSearch =
                search != null
                && !search.trim().isEmpty();

        String searchValue =
                hasSearch
                        ? search.trim()
                        : "";


        // =====================================================
        // BANK BALANCE
        // =====================================================

        List<BankBalance> bankBalances;

        if (hasSearch) {

            bankBalances =
                    bankBalanceRepository
                    .findByCdNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
                            searchValue,
                            searchValue
                    );

        } else {

            bankBalances =
                    bankBalanceRepository.findAll();
        }


        // =====================================================
        // PENDING
        // =====================================================

        List<PendingAmount> pendingAmounts;

        if (hasSearch) {

            pendingAmounts =
                    pendingAmountRepository
                    .findByNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
                            searchValue,
                            searchValue
                    );

        } else {

            pendingAmounts =
                    pendingAmountRepository.findAll();
        }


        // =====================================================
        // EXTRA
        // =====================================================

        List<ExtraAmount> extraAmounts;

        if (hasSearch) {

            extraAmounts =
                    extraAmountRepository
                    .findByNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
                            searchValue,
                            searchValue
                    );

        } else {

            extraAmounts =
                    extraAmountRepository.findAll();
        }


        // =====================================================
        // BANK TOTAL
        // =====================================================

        BigDecimal totalIncome =
                bankBalances.stream()
                .map(BankBalance::getIncome)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal totalExpense =
                bankBalances.stream()
                .map(BankBalance::getExpense)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal bankTotal =
                totalIncome.subtract(
                        totalExpense
                );


        // =====================================================
        // PENDING TOTAL
        // =====================================================

        BigDecimal totalDue =
                pendingAmounts.stream()
                .map(PendingAmount::getDue)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal totalReturn =
                pendingAmounts.stream()
                .map(PendingAmount::getReturnAmount)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal pendingTotal =
                totalDue.subtract(
                        totalReturn
                );


        // =====================================================
        // EXTRA TOTAL
        // =====================================================

        BigDecimal totalHave =
                extraAmounts.stream()
                .map(ExtraAmount::getHaveAmount)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal totalGive =
                extraAmounts.stream()
                .map(ExtraAmount::getGiveAmount)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        BigDecimal extraTotal =
                totalHave.subtract(
                        totalGive
                );


        // =====================================================
        // AUDIT
        //
        // Bank + Pending - Extra
        // =====================================================

        BigDecimal auditAmount =
                bankTotal
                .add(pendingTotal)
                .subtract(extraTotal);


        // =====================================================
        // SEND DATA TO HTML
        // =====================================================

        model.addAttribute(
                "bankBalances",
                bankBalances
        );

        model.addAttribute(
                "pendingAmounts",
                pendingAmounts
        );

        model.addAttribute(
                "extraAmounts",
                extraAmounts
        );


        // BANK

        model.addAttribute(
                "totalIncome",
                totalIncome
        );

        model.addAttribute(
                "totalExpense",
                totalExpense
        );

        model.addAttribute(
                "bankTotal",
                bankTotal
        );


        // PENDING

        model.addAttribute(
                "totalDue",
                totalDue
        );

        model.addAttribute(
                "totalReturn",
                totalReturn
        );

        model.addAttribute(
                "pendingTotal",
                pendingTotal
        );


        // EXTRA

        model.addAttribute(
                "totalHave",
                totalHave
        );

        model.addAttribute(
                "totalGive",
                totalGive
        );

        model.addAttribute(
                "extraTotal",
                extraTotal
        );


        // AUDIT

        model.addAttribute(
                "auditAmount",
                auditAmount
        );


        // SEARCH

        model.addAttribute(
                "search",
                searchValue
        );


        return "accounts";
    }


    // =========================================================
    // ADD BANK BALANCE
    // OWNER ONLY
    // =========================================================

    @PostMapping("/accounts/bank/add")
    public String addBankBalance(

            @RequestParam String cdName,

            @RequestParam(required = false)
            String orderNumber,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal income,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal expense,

            @RequestParam(required = false)
            LocalDate transactionDate,

            HttpSession session) {


        // LOGIN CHECK

        Object userId =
                session.getAttribute(
                        "accountUserId"
                );

        if (userId == null) {

            return "redirect:/accounts/login";
        }


        // OWNER CHECK

        Object owner =
                session.getAttribute(
                        "accountOwner"
                );

        if (!Boolean.TRUE.equals(owner)) {

            return "redirect:/accounts";
        }


        // CREATE BANK TRANSACTION

        BankBalance bank =
                new BankBalance();


        bank.setCdName(
                cdName
        );


        bank.setOrderNumber(
                orderNumber
        );


        bank.setIncome(
                income != null
                        ? income
                        : BigDecimal.ZERO
        );


        bank.setExpense(
                expense != null
                        ? expense
                        : BigDecimal.ZERO
        );


        // TRANSACTION DATE

        bank.setTransactionDate(
                transactionDate != null
                        ? transactionDate
                        : LocalDate.now()
        );


        bankBalanceRepository.save(
                bank
        );


        return "redirect:/accounts";
    }


    // =========================================================
    // ADD PENDING
    // OWNER ONLY
    // =========================================================

    @PostMapping("/accounts/pending/add")
    public String addPending(

            @RequestParam String name,

            @RequestParam(required = false)
            String orderNumber,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal due,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal returnAmount,

            @RequestParam(required = false)
            LocalDate transactionDate,

            HttpSession session) {


        // LOGIN CHECK

        Object userId =
                session.getAttribute(
                        "accountUserId"
                );

        if (userId == null) {

            return "redirect:/accounts/login";
        }


        // OWNER CHECK

        Object owner =
                session.getAttribute(
                        "accountOwner"
                );

        if (!Boolean.TRUE.equals(owner)) {

            return "redirect:/accounts";
        }


        // CREATE PENDING TRANSACTION

        PendingAmount pending =
                new PendingAmount();


        pending.setName(
                name
        );


        pending.setOrderNumber(
                orderNumber
        );


        pending.setDue(
                due != null
                        ? due
                        : BigDecimal.ZERO
        );


        pending.setReturnAmount(
                returnAmount != null
                        ? returnAmount
                        : BigDecimal.ZERO
        );


        // TRANSACTION DATE

        pending.setTransactionDate(
                transactionDate != null
                        ? transactionDate
                        : LocalDate.now()
        );


        pendingAmountRepository.save(
                pending
        );


        return "redirect:/accounts";
    }


    // =========================================================
    // ADD EXTRA
    // OWNER ONLY
    // =========================================================

    @PostMapping("/accounts/extra/add")
    public String addExtra(

            @RequestParam String name,

            @RequestParam(required = false)
            String orderNumber,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal haveAmount,

            @RequestParam(
                    required = false,
                    defaultValue = "0"
            )
            BigDecimal giveAmount,

            @RequestParam(required = false)
            LocalDate transactionDate,

            HttpSession session) {


        // LOGIN CHECK

        Object userId =
                session.getAttribute(
                        "accountUserId"
                );

        if (userId == null) {

            return "redirect:/accounts/login";
        }


        // OWNER CHECK

        Object owner =
                session.getAttribute(
                        "accountOwner"
                );

        if (!Boolean.TRUE.equals(owner)) {

            return "redirect:/accounts";
        }


        // CREATE EXTRA TRANSACTION

        ExtraAmount extra =
                new ExtraAmount();


        extra.setName(
                name
        );


        extra.setOrderNumber(
                orderNumber
        );


        extra.setHaveAmount(
                haveAmount != null
                        ? haveAmount
                        : BigDecimal.ZERO
        );


        extra.setGiveAmount(
                giveAmount != null
                        ? giveAmount
                        : BigDecimal.ZERO
        );


        // TRANSACTION DATE

        extra.setTransactionDate(
                transactionDate != null
                        ? transactionDate
                        : LocalDate.now()
        );


        extraAmountRepository.save(
                extra
        );


        return "redirect:/accounts";
    }


    // =========================================================
    // BANK BALANCE PAGE
    // =========================================================

    @GetMapping("/accounts/bank")
    public String bankBalance(
            HttpSession session,
            Model model) {


        // LOGIN CHECK

        Object userId =
                session.getAttribute(
                        "accountUserId"
                );

        if (userId == null) {

            return "redirect:/accounts/login";
        }


        Object username =
                session.getAttribute(
                        "accountUsername"
                );


        Object owner =
                session.getAttribute(
                        "accountOwner"
                );


        // GET BANK DATA

        List<BankBalance> bankBalances =
                bankBalanceRepository.findAll();


        // TOTAL INCOME

        BigDecimal totalIncome =
                bankBalances.stream()
                .map(BankBalance::getIncome)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        // TOTAL EXPENSE

        BigDecimal totalExpense =
                bankBalances.stream()
                .map(BankBalance::getExpense)
                .filter(value -> value != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );


        // BANK TOTAL

        BigDecimal bankTotal =
                totalIncome.subtract(
                        totalExpense
                );


        model.addAttribute(
                "username",
                username
        );


        model.addAttribute(
                "owner",
                Boolean.TRUE.equals(owner)
        );


        model.addAttribute(
                "bankBalances",
                bankBalances
        );


        model.addAttribute(
                "totalIncome",
                totalIncome
        );


        model.addAttribute(
                "totalExpense",
                totalExpense
        );


        model.addAttribute(
                "bankTotal",
                bankTotal
        );


        return "bank-balance";
    }


    // =========================================================
    // EXCEL EXPORT
    // =========================================================

    @GetMapping("/accounts/export")
    public void exportAccountsExcel(

            HttpSession session,

            HttpServletResponse response)
            throws IOException {


        // LOGIN CHECK

        if (
                session.getAttribute(
                        "accountUserId"
                ) == null
        ) {

            response.sendRedirect(
                    "/accounts/login"
            );

            return;
        }


        // EXCEL CONTENT TYPE

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );


        response.setHeader(
                "Content-Disposition",
                "attachment; filename=AutoPrtz-Accounts.xlsx"
        );


        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {


            // =================================================
            // LOAD ALL DATA
            // =================================================

            List<BankBalance> bankBalances =
                    bankBalanceRepository.findAll();


            List<PendingAmount> pendingAmounts =
                    pendingAmountRepository.findAll();


            List<ExtraAmount> extraAmounts =
                    extraAmountRepository.findAll();


            // =================================================
            // CALCULATE BANK TOTALS
            // =================================================

            BigDecimal totalIncome =
                    bankBalances.stream()
                    .map(BankBalance::getIncome)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal totalExpense =
                    bankBalances.stream()
                    .map(BankBalance::getExpense)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal bankTotal =
                    totalIncome.subtract(
                            totalExpense
                    );


            // =================================================
            // CALCULATE PENDING TOTALS
            // =================================================

            BigDecimal totalDue =
                    pendingAmounts.stream()
                    .map(PendingAmount::getDue)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal totalReturn =
                    pendingAmounts.stream()
                    .map(PendingAmount::getReturnAmount)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal pendingTotal =
                    totalDue.subtract(
                            totalReturn
                    );


            // =================================================
            // CALCULATE EXTRA TOTALS
            // =================================================

            BigDecimal totalHave =
                    extraAmounts.stream()
                    .map(ExtraAmount::getHaveAmount)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal totalGive =
                    extraAmounts.stream()
                    .map(ExtraAmount::getGiveAmount)
                    .filter(v -> v != null)
                    .reduce(
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );


            BigDecimal extraTotal =
                    totalHave.subtract(
                            totalGive
                    );


            // =================================================
            // FINAL AUDIT
            // =================================================

            BigDecimal auditAmount =
                    bankTotal
                    .add(pendingTotal)
                    .subtract(extraTotal);


            // =================================================
            // BANK SHEET
            // =================================================

            Sheet bankSheet =
                    workbook.createSheet(
                            "Bank Balance"
                    );


            Row bankHeader =
                    bankSheet.createRow(0);


            bankHeader.createCell(0)
                    .setCellValue("Transaction Date");


            bankHeader.createCell(1)
                    .setCellValue("C/D Name");


            bankHeader.createCell(2)
                    .setCellValue("Order Number");


            bankHeader.createCell(3)
                    .setCellValue("Income");


            bankHeader.createCell(4)
                    .setCellValue("Expense");


            bankHeader.createCell(5)
                    .setCellValue("Total");


            int bankRow = 1;


            for (
                    BankBalance bank :
                    bankBalances
            ) {


                Row row =
                        bankSheet.createRow(
                                bankRow++
                        );


                row.createCell(0)
                        .setCellValue(
                                bank.getTransactionDate() == null
                                        ? ""
                                        : bank.getTransactionDate().toString()
                        );


                row.createCell(1)
                        .setCellValue(
                                bank.getCdName() == null
                                        ? ""
                                        : bank.getCdName()
                        );


                row.createCell(2)
                        .setCellValue(
                                bank.getOrderNumber() == null
                                        ? ""
                                        : bank.getOrderNumber()
                        );


                row.createCell(3)
                        .setCellValue(
                                bank.getIncome() == null
                                        ? 0
                                        : bank.getIncome().doubleValue()
                        );


                row.createCell(4)
                        .setCellValue(
                                bank.getExpense() == null
                                        ? 0
                                        : bank.getExpense().doubleValue()
                        );


                row.createCell(5)
                        .setCellValue(
                                bank.getTotal() == null
                                        ? 0
                                        : bank.getTotal().doubleValue()
                        );
            }


            // =================================================
            // PENDING SHEET
            // =================================================

            Sheet pendingSheet =
                    workbook.createSheet(
                            "Pending"
                    );


            Row pendingHeader =
                    pendingSheet.createRow(0);


            pendingHeader.createCell(0)
                    .setCellValue("Transaction Date");


            pendingHeader.createCell(1)
                    .setCellValue("Name");


            pendingHeader.createCell(2)
                    .setCellValue("Order Number");


            pendingHeader.createCell(3)
                    .setCellValue("Due");


            pendingHeader.createCell(4)
                    .setCellValue("Return");


            pendingHeader.createCell(5)
                    .setCellValue("Total");


            int pendingRow = 1;


            for (
                    PendingAmount pending :
                    pendingAmounts
            ) {


                Row row =
                        pendingSheet.createRow(
                                pendingRow++
                        );


                row.createCell(0)
                        .setCellValue(
                                pending.getTransactionDate() == null
                                        ? ""
                                        : pending.getTransactionDate().toString()
                        );


                row.createCell(1)
                        .setCellValue(
                                pending.getName() == null
                                        ? ""
                                        : pending.getName()
                        );


                row.createCell(2)
                        .setCellValue(
                                pending.getOrderNumber() == null
                                        ? ""
                                        : pending.getOrderNumber()
                        );


                row.createCell(3)
                        .setCellValue(
                                pending.getDue() == null
                                        ? 0
                                        : pending.getDue().doubleValue()
                        );


                row.createCell(4)
                        .setCellValue(
                                pending.getReturnAmount() == null
                                        ? 0
                                        : pending.getReturnAmount().doubleValue()
                        );


                row.createCell(5)
                        .setCellValue(
                                pending.getTotal() == null
                                        ? 0
                                        : pending.getTotal().doubleValue()
                        );
            }


            // =================================================
            // EXTRA SHEET
            // =================================================

            Sheet extraSheet =
                    workbook.createSheet(
                            "Extra"
                    );


            Row extraHeader =
                    extraSheet.createRow(0);


            extraHeader.createCell(0)
                    .setCellValue("Transaction Date");


            extraHeader.createCell(1)
                    .setCellValue("Name");


            extraHeader.createCell(2)
                    .setCellValue("Order Number");


            extraHeader.createCell(3)
                    .setCellValue("Have");


            extraHeader.createCell(4)
                    .setCellValue("Give");


            extraHeader.createCell(5)
                    .setCellValue("Total");


            int extraRow = 1;


            for (
                    ExtraAmount extra :
                    extraAmounts
            ) {


                Row row =
                        extraSheet.createRow(
                                extraRow++
                        );


                row.createCell(0)
                        .setCellValue(
                                extra.getTransactionDate() == null
                                        ? ""
                                        : extra.getTransactionDate().toString()
                        );


                row.createCell(1)
                        .setCellValue(
                                extra.getName() == null
                                        ? ""
                                        : extra.getName()
                        );


                row.createCell(2)
                        .setCellValue(
                                extra.getOrderNumber() == null
                                        ? ""
                                        : extra.getOrderNumber()
                        );


                row.createCell(3)
                        .setCellValue(
                                extra.getHaveAmount() == null
                                        ? 0
                                        : extra.getHaveAmount().doubleValue()
                        );


                row.createCell(4)
                        .setCellValue(
                                extra.getGiveAmount() == null
                                        ? 0
                                        : extra.getGiveAmount().doubleValue()
                        );


                row.createCell(5)
                        .setCellValue(
                                extra.getTotal() == null
                                        ? 0
                                        : extra.getTotal().doubleValue()
                        );
            }


            // =================================================
            // AUDIT SHEET
            // =================================================

            Sheet auditSheet =
                    workbook.createSheet(
                            "Audit"
                    );


            Row auditTitle =
                    auditSheet.createRow(0);


            auditTitle.createCell(0)
                    .setCellValue(
                            "AutoPrtz Accounts Audit"
                    );


            Row auditDate =
                    auditSheet.createRow(1);


            auditDate.createCell(0)
                    .setCellValue(
                            "Report Date"
                    );


            auditDate.createCell(1)
                    .setCellValue(
                            LocalDate.now().toString()
                    );


            Row auditBank =
                    auditSheet.createRow(3);


            auditBank.createCell(0)
                    .setCellValue(
                            "Bank Total"
                    );


            auditBank.createCell(1)
                    .setCellValue(
                            bankTotal.doubleValue()
                    );


            Row auditPending =
                    auditSheet.createRow(4);


            auditPending.createCell(0)
                    .setCellValue(
                            "Pending Total"
                    );


            auditPending.createCell(1)
                    .setCellValue(
                            pendingTotal.doubleValue()
                    );


            Row auditExtra =
                    auditSheet.createRow(5);


            auditExtra.createCell(0)
                    .setCellValue(
                            "Extra Total"
                    );


            auditExtra.createCell(1)
                    .setCellValue(
                            extraTotal.doubleValue()
                    );


            Row auditIncome =
                    auditSheet.createRow(7);


            auditIncome.createCell(0)
                    .setCellValue(
                            "Total Income"
                    );


            auditIncome.createCell(1)
                    .setCellValue(
                            totalIncome.doubleValue()
                    );


            Row auditExpense =
                    auditSheet.createRow(8);


            auditExpense.createCell(0)
                    .setCellValue(
                            "Total Expense"
                    );


            auditExpense.createCell(1)
                    .setCellValue(
                            totalExpense.doubleValue()
                    );


            Row auditDue =
                    auditSheet.createRow(9);


            auditDue.createCell(0)
                    .setCellValue(
                            "Total Due"
                    );


            auditDue.createCell(1)
                    .setCellValue(
                            totalDue.doubleValue()
                    );


            Row auditReturn =
                    auditSheet.createRow(10);


            auditReturn.createCell(0)
                    .setCellValue(
                            "Total Return"
                    );


            auditReturn.createCell(1)
                    .setCellValue(
                            totalReturn.doubleValue()
                    );


            Row auditHave =
                    auditSheet.createRow(11);


            auditHave.createCell(0)
                    .setCellValue(
                            "Total Have"
                    );


            auditHave.createCell(1)
                    .setCellValue(
                            totalHave.doubleValue()
                    );


            Row auditGive =
                    auditSheet.createRow(12);


            auditGive.createCell(0)
                    .setCellValue(
                            "Total Give"
                    );


            auditGive.createCell(1)
                    .setCellValue(
                            totalGive.doubleValue()
                    );


            Row auditFinal =
                    auditSheet.createRow(14);


            auditFinal.createCell(0)
                    .setCellValue(
                            "FINAL AUDIT AMOUNT"
                    );


            auditFinal.createCell(1)
                    .setCellValue(
                            auditAmount.doubleValue()
                    );


            // =================================================
            // AUTO SIZE COLUMNS
            // =================================================

            for (int i = 0; i < 6; i++) {

                bankSheet.autoSizeColumn(i);

                pendingSheet.autoSizeColumn(i);

                extraSheet.autoSizeColumn(i);
            }


            auditSheet.autoSizeColumn(0);

            auditSheet.autoSizeColumn(1);


            // =================================================
            // WRITE EXCEL
            // =================================================

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();


            workbook.write(
                    output
            );


            response.getOutputStream()
                    .write(
                            output.toByteArray()
                    );


            response.getOutputStream()
                    .flush();
        }
    }
}