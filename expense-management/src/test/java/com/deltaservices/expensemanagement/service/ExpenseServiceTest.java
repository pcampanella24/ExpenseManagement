package com.deltaservices.expensemanagement.service;

import com.deltaservices.expensemanagement.exception.BadRequestException;
import com.deltaservices.expensemanagement.model.Expense;
import com.deltaservices.expensemanagement.model.ExpenseRequestDto;
import com.deltaservices.expensemanagement.model.ExpenseResponseDto;
import com.deltaservices.expensemanagement.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense1;
    private Expense expense2;
    private ExpenseRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        expense1 = new Expense();
        expense1.setId(1L);
        expense1.setDescription("Test Expense 1");
        expense1.setAmount(new BigDecimal("100.50"));
        expense1.setDate(LocalDate.now());
        expense1.setCategory("Food");

        expense2 = new Expense();
        expense2.setId(2L);
        expense2.setDescription("Test Expense 2");
        expense2.setAmount(new BigDecimal("50.75"));
        expense2.setDate(LocalDate.now().minusDays(1));
        expense2.setCategory("Transport");

        validRequestDto = new ExpenseRequestDto();
        validRequestDto.setDescription("New Expense");
        validRequestDto.setAmount(new BigDecimal("75.25"));
        validRequestDto.setDate(LocalDate.now());
        validRequestDto.setCategory("Utilities");
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense1, expense2));

        List<ExpenseResponseDto> result = expenseService.getAllExpenses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Expense 1", result.get(0).getDescription());
        assertEquals("Test Expense 2", result.get(1).getDescription());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void getAllExpenses_WhenNoExpenses_ShouldReturnEmptyList() {
        when(expenseRepository.findAll()).thenReturn(Collections.emptyList());

        List<ExpenseResponseDto> result = expenseService.getAllExpenses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    void createExpense_WithValidData_ShouldReturnSavedExpense() {
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense savedExpense = invocation.getArgument(0);
            savedExpense.setId(3L);
            return savedExpense;
        });

        ExpenseResponseDto result = expenseService.createExpense(validRequestDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Expense", result.getDescription());
        assertEquals(new BigDecimal("75.25"), result.getAmount());
        assertEquals("Utilities", result.getCategory());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void createExpense_WithNullRequest_ShouldThrowBadRequestException() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(null)
        );
        assertEquals("Expense data cannot be null", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithEmptyDescription_ShouldThrowBadRequestException() {
        ExpenseRequestDto invalidRequest = new ExpenseRequestDto();
        invalidRequest.setDescription("");
        invalidRequest.setAmount(new BigDecimal("75.25"));
        invalidRequest.setDate(LocalDate.now());
        invalidRequest.setCategory("Utilities");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(invalidRequest)
        );
        assertEquals("Expense description cannot be empty", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithNullAmount_ShouldThrowBadRequestException() {
        ExpenseRequestDto invalidRequest = new ExpenseRequestDto();
        invalidRequest.setDescription("Test");
        invalidRequest.setAmount(null);
        invalidRequest.setDate(LocalDate.now());
        invalidRequest.setCategory("Utilities");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(invalidRequest)
        );
        assertEquals("Expense amount cannot be null", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithZeroAmount_ShouldThrowBadRequestException() {
        ExpenseRequestDto invalidRequest = new ExpenseRequestDto();
        invalidRequest.setDescription("Test");
        invalidRequest.setAmount(BigDecimal.ZERO);
        invalidRequest.setDate(LocalDate.now());
        invalidRequest.setCategory("Utilities");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(invalidRequest)
        );
        assertEquals("Expense amount must be greater than zero", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithNullDate_ShouldThrowBadRequestException() {
        ExpenseRequestDto invalidRequest = new ExpenseRequestDto();
        invalidRequest.setDescription("Test");
        invalidRequest.setAmount(new BigDecimal("75.25"));
        invalidRequest.setDate(null);
        invalidRequest.setCategory("Utilities");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(invalidRequest)
        );
        assertEquals("Expense date cannot be null", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void createExpense_WithDataIntegrityViolation_ShouldThrowBadRequestException() {
        when(expenseRepository.save(any(Expense.class))).thenThrow(new DataIntegrityViolationException("Database error"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> expenseService.createExpense(validRequestDto)
        );
        assertEquals("Could not create expense due to data constraint violation", exception.getMessage());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void deleteExpense_WithValidId_ShouldDeleteExpense() {
        Long id = 1L;
        when(expenseRepository.existsById(id)).thenReturn(true);
        doNothing().when(expenseRepository).deleteById(id);

        expenseService.deleteExpense(id);

        verify(expenseRepository, times(1)).existsById(id);
        verify(expenseRepository, times(1)).deleteById(id);
    }
}