package com.deltaservices.expensemanagement.controller;

import com.deltaservices.expensemanagement.model.ExpenseRequestDto;
import com.deltaservices.expensemanagement.model.ExpenseResponseDto;
import com.deltaservices.expensemanagement.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private ExpenseResponseDto response1;
    private ExpenseResponseDto response2;
    private ExpenseRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        response1 = new ExpenseResponseDto();
        response1.setId(1L);
        response1.setDescription("Test Expense 1");
        response1.setAmount(new BigDecimal("100.50"));
        response1.setDate(LocalDate.now());
        response1.setCategory("Food");

        response2 = new ExpenseResponseDto();
        response2.setId(2L);
        response2.setDescription("Test Expense 2");
        response2.setAmount(new BigDecimal("50.75"));
        response2.setDate(LocalDate.now().minusDays(1));
        response2.setCategory("Transport");

        validRequestDto = new ExpenseRequestDto();
        validRequestDto.setDescription("New Expense");
        validRequestDto.setAmount(new BigDecimal("75.25"));
        validRequestDto.setDate(LocalDate.now());
        validRequestDto.setCategory("Utilities");
    }

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() {
        List<ExpenseResponseDto> expenses = Arrays.asList(response1, response2);
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        ResponseEntity<List<ExpenseResponseDto>> response = expenseController.getAllExpenses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Expense 1", response.getBody().get(0).getDescription());
        assertEquals("Test Expense 2", response.getBody().get(1).getDescription());
        verify(expenseService, times(1)).getAllExpenses();
    }

    @Test
    void getAllExpenses_WhenNoExpenses_ShouldReturnEmptyList() {
        when(expenseService.getAllExpenses()).thenReturn(new ArrayList<>());

        ResponseEntity<List<ExpenseResponseDto>> response = expenseController.getAllExpenses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(expenseService, times(1)).getAllExpenses();
    }

    @Test
    void createExpense_WithValidRequest_ShouldReturnCreatedExpense() {
        ExpenseResponseDto createdExpense = new ExpenseResponseDto();
        createdExpense.setId(3L);
        createdExpense.setDescription("New Expense");
        createdExpense.setAmount(new BigDecimal("75.25"));
        createdExpense.setDate(LocalDate.now());
        createdExpense.setCategory("Utilities");

        when(expenseService.createExpense(any(ExpenseRequestDto.class))).thenReturn(createdExpense);

        ResponseEntity<ExpenseResponseDto> response = expenseController.createExpense(validRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("New Expense", response.getBody().getDescription());
        assertEquals(new BigDecimal("75.25"), response.getBody().getAmount());
        assertEquals("Utilities", response.getBody().getCategory());
        verify(expenseService, times(1)).createExpense(any(ExpenseRequestDto.class));
    }

    @Test
    void deleteExpense_WithValidId_ShouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(expenseService).deleteExpense(id);

        ResponseEntity<Void> response = expenseController.deleteExpense(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(expenseService, times(1)).deleteExpense(id);
    }
}