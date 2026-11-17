package com.github.jinlee3511.finance_tracker.controller;

import com.github.jinlee3511.finance_tracker.dto.TransactionRequest;
import com.github.jinlee3511.finance_tracker.dto.TransactionResponse;
import com.github.jinlee3511.finance_tracker.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Get By Id
    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable @Positive Long id) {
        return TransactionResponse.from(transactionService.getTransactionById(id)); // 200
    }

    // Get Transactions By Date
    @GetMapping // ?start=2026-11-12&end=2026-11-13
    public List<TransactionResponse> getTransactions(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start, // yyyy-MM-dd

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end // yyyy-MM-dd
    ) {
        return transactionService.getTransactionsByDate(start, end)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    // Create
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody @Valid TransactionRequest transactionRequest) {
        Long transactionId = transactionService.createTransaction(transactionRequest.categoryId(),
                transactionRequest.transactionType(), transactionRequest.amount(),
                transactionRequest.transactionDate(), transactionRequest.description());

        TransactionResponse transactionResponse = TransactionResponse
                .from(transactionService.getTransactionById(transactionId));

        // 201 + Transaction body
        return ResponseEntity.created(URI.create("/api/transactions/" + transactionId)).body(transactionResponse);
    }

    // Update Transaction
    @PutMapping("/{id}")
    public TransactionResponse modifyTransaction(@PathVariable @Positive Long id,
                                                 @RequestBody @Valid TransactionRequest transactionRequest) {
        transactionService.updateTransaction(id, transactionRequest.categoryId(),
                transactionRequest.amount(), transactionRequest.transactionType(),
                transactionRequest.transactionDate(), transactionRequest.description());
        return TransactionResponse.from(transactionService.getTransactionById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @Positive Long id) {
        transactionService.deleteTransactionById(id);
        return ResponseEntity.noContent().build(); // 204 delete, no body
    }
}
