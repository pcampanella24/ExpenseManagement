package com.deltaservices.expensemanagement.service;

import com.deltaservices.expensemanagement.exception.ResourceNotFoundException;
import com.deltaservices.expensemanagement.model.Expense;
import com.deltaservices.expensemanagement.model.ExpenseRequestDto;
import com.deltaservices.expensemanagement.model.ExpenseResponseDto;
import com.deltaservices.expensemanagement.repository.ExpenseRepository;
import com.deltaservices.expensemanagement.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDto> getAllExpenses() {
        logger.info("Fetching all expenses");
        return expenseRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
        validateExpenseRequest(expenseRequestDto);

        try {
            logger.info("Creating new expense: {}", expenseRequestDto);
            Expense expense = convertToEntity(expenseRequestDto);
            Expense savedExpense = expenseRepository.save(expense);
            logger.info("Successfully created expense with id: {}", savedExpense.getId());

            return convertToResponseDto(savedExpense);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to create expense due to data integrity violation", e);
            throw new BadRequestException("Could not create expense due to data constraint violation");
        } catch (Exception e) {
            logger.error("Unexpected error while creating expense, check the amount", e);
            throw e;
        }
    }

    @Transactional
    public void deleteExpense(Long id) {
        validateId(id);

        logger.info("Deleting expense with id: {}", id);

        if (!expenseRepository.existsById(id)) {
            logger.warn("Failed to delete expense with id: {}. Expense not found", id);
            throw new ResourceNotFoundException("Expense", "id", id);
        }

        try {
            expenseRepository.deleteById(id);
            logger.info("Successfully deleted expense with id: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete expense with id: {}", id, e);
            throw e;
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            logger.warn("Expense ID is null");
            throw new BadRequestException("Expense ID cannot be null");
        }

        if (id <= 0) {
            logger.warn("Invalid expense ID: {}", id);
            throw new BadRequestException("Expense ID must be a positive number");
        }
    }

    private void validateExpenseRequest(ExpenseRequestDto request) {
        if (request == null) {
            logger.warn("Expense data is null");
            throw new BadRequestException("Expense data cannot be null");
        }

        if (request.getDescription() == null || !StringUtils.hasText(request.getDescription())) {
            logger.warn("Expense description is empty or null");
            throw new BadRequestException("Expense description cannot be empty");
        }

        if (request.getAmount() == null) {
            logger.warn("Expense amount is null");
            throw new BadRequestException("Expense amount cannot be null");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Invalid expense amount: {}", request.getAmount());
            throw new BadRequestException("Expense amount must be greater than zero");
        }

        if (request.getDate() == null) {
            logger.warn("Expense date is null");
            throw new BadRequestException("Expense date cannot be null");
        }

        if (request.getCategory() != null && request.getCategory().trim().isEmpty()) {
            logger.warn("Expense category is empty string");
            throw new BadRequestException("Expense category cannot be empty string");
        }
    }

    private ExpenseResponseDto convertToResponseDto(Expense expense) {
        ExpenseResponseDto responseDto = new ExpenseResponseDto();
        responseDto.setId(expense.getId());
        responseDto.setDescription(expense.getDescription());
        responseDto.setAmount(expense.getAmount());
        responseDto.setDate(expense.getDate());
        responseDto.setCategory(expense.getCategory());
        return responseDto;
    }

    private Expense convertToEntity(ExpenseRequestDto requestDto) {
        Expense expense = new Expense();
        updateExpenseFields(expense, requestDto);
        return expense;
    }

    private void updateExpenseFields(Expense expense, ExpenseRequestDto requestDto) {
        expense.setDescription(requestDto.getDescription());
        expense.setAmount(requestDto.getAmount());
        expense.setDate(requestDto.getDate());
        expense.setCategory(requestDto.getCategory());
    }
}