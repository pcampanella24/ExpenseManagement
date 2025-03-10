package com.deltaservices.expensemanagement.model;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequestDto {

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Category is required")
    private String category;
}