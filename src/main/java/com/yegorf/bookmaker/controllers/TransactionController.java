package com.yegorf.bookmaker.controllers;

import com.yegorf.bookmaker.entities.Transaction;
import com.yegorf.bookmaker.repos.TransactionRepo;
import com.yegorf.bookmaker.repos.UserRepo;
import com.yegorf.bookmaker.transactions.TransactionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;

    public TransactionController(TransactionRepo transactionRepo, UserRepo userRepo) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/getByUser")
    private HashSet<Transaction> getByUser(@RequestParam String user) {
        return transactionRepo.findAllByUser(userRepo.findByName(user));
    }

    @PostMapping("/addTransaction")
    private void addTransaction(@RequestParam String user, @RequestParam float sum) {
        TransactionHandler transactionHandler = new TransactionHandler(transactionRepo, userRepo);
        transactionHandler.addTransaction(user, sum);
    }
}
