package com.eduguide.eduguide.service;

import com.eduguide.eduguide.model.Transaction;
import com.eduguide.eduguide.model.TransactionRequest;
import com.eduguide.eduguide.repository.TransactionRepository;
import com.eduguide.eduguide.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByUserId(UUID userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Transaction> createTransaction(TransactionRequest request) {
        var userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isPresent()) {
            Transaction transaction = new Transaction();
            transaction.setUser(userOptional.get());
            transaction.setAmount(request.getAmount());
            transaction.setType(request.getType());
            transaction.setDescription(request.getDescription());
            return Optional.of(transactionRepository.save(transaction));
        }
        return Optional.empty();
    }

    public boolean deleteTransaction(UUID id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
