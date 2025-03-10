package com.deltaservices.expensemanagement.repository;

import com.deltaservices.expensemanagement.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> { }
