package org.example.finansijeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.finansijeservice.dto.TransactionDTO;
import org.example.finansijeservice.service.TransactionsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:8080")
public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping
    @Operation(
            summary = "List transactions",
            description = "Returns all transactions. Optionally filter by invoice ID or payment method ID."
    )
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @RequestParam(required = false)
            @Schema(description = "Filter by invoice ID", example = "1")
            Long invoiceId,

            @RequestParam(required = false)
            @Schema(description = "Filter by payment method ID", example = "2")
            Long paymentMethodId) {

        List<TransactionDTO> transactions;
        if (invoiceId != null) {
            transactions = transactionsService.getTransactionsByInvoiceId(invoiceId);
        } else if (paymentMethodId != null) {
            transactions = transactionsService.getTransactionsByPaymentMethodId(paymentMethodId);
        } else {
            transactions = transactionsService.getAllTransactions();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @PathVariable
            @Schema(description = "Transaction ID", example = "1")
            Long id) {
        return ResponseEntity.ok(transactionsService.getTransactionById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create transaction",
            description = "Creates a new transaction"
    )
    @ApiResponse(responseCode = "201", description = "Transaction created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestBody @Valid TransactionDTO request) {

        TransactionDTO response = transactionsService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invoice/{invoiceId}/total")
    @Operation(
            summary = "Calculate total transaction amount for invoice",
            description = "Calculates sum of all transactions for a given invoice"
    )
    @ApiResponse(responseCode = "200", description = "Total amount calculated")
    public ResponseEntity<Double> getTotalAmountForInvoice(
            @PathVariable
            @Schema(description = "Invoice ID", example = "1")
            Long invoiceId) {
        Double total = transactionsService.calculateTotalAmountForInvoice(invoiceId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    @ApiResponse(responseCode = "204", description = "Transaction deleted successfully")
    @ApiResponse(responseCode = "404", description = "Transaction not found")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable
            @Schema(description = "Transaction ID", example = "1")
            Long id) {
        transactionsService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
