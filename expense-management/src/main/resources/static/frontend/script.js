const API_BASE_URL = 'http://localhost:8090/api/expenses';

const expenseForm = document.getElementById('expenseForm');
const expensesList = document.getElementById('expensesList');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const resetBtn = document.getElementById('resetBtn');
const messageContainer = document.getElementById('messageContainer');
const descriptionField = document.getElementById('description');
const amountField = document.getElementById('amount');
const dateField = document.getElementById('date');
const categoryField = document.getElementById('category');

document.addEventListener('DOMContentLoaded', () => {
    resetForm();
    loadExpenses();
    
    expenseForm.addEventListener('submit', handleFormSubmit);
    resetBtn.addEventListener('click', resetForm);
});

async function loadExpenses() {
    try {
        const response = await fetch(API_BASE_URL);
        
        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }

        const expenses = await response.json();
        renderExpensesList(expenses);
    } catch (error) {
        showMessage(`Error loading expenses: ${error.message}`, true);
    }
}

function renderExpensesList(expenses) {
    expensesList.innerHTML = '';
    
    if (expenses.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="5" style="text-align: center;">No expenses found</td>';
        expensesList.appendChild(row);
        return;
    }
    
    let totalAmount = 0;
    
    expenses.forEach(expense => {
        const row = document.createElement('tr');  
        totalAmount += expense.amount;
        const date = new Date(expense.date);
        const formattedDate = date.toLocaleDateString('it-IT');
        const categoryMap = {
            'FOOD': 'Food',
            'TRANSPORTATION': 'Transportation',
            'ENTERTAINMENT': 'Entertainment',
            'UTILITIES': 'Utilities',
            'OTHER': 'Other'};
        
        row.innerHTML = `
            <td>${expense.description}</td>
            <td>€${expense.amount.toFixed(2)}</td>
            <td>${formattedDate}</td>
            <td>${categoryMap[expense.category] || expense.category}</td>
            <td class="actions">
                <button class="delete-btn" data-id="${expense.id}">Delete</button>
            </td>`;
        
        row.querySelector('.delete-btn').addEventListener('click', () => deleteExpense(expense.id));
        expensesList.appendChild(row);
    });
    
    const totalRow = document.createElement('tr');
    totalRow.className = 'total-row';
    totalRow.innerHTML = `
        <td><strong>Total</strong></td>
        <td><strong>€${totalAmount.toFixed(2)}</strong></td>
        <td></td>
        <td></td>
        <td></td>`;
    expensesList.appendChild(totalRow);
}

async function handleFormSubmit(event) {
    event.preventDefault();
    
    const expenseData = {
        description: descriptionField.value,
        amount: parseFloat(amountField.value),
        date: dateField.value,
        category: categoryField.value
    };  

    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(expenseData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP Error: ${response.status}`);
        }
        
        showMessage('Expense saved successfully!');
        resetForm();
        loadExpenses();       
    } catch (error) {
        showMessage(`Error: ${error.message}`, true);
    }
}

async function deleteExpense(id) {
    if (!confirm('Are you sure you want to delete this expense?')) {
        return;
    }   
    
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }
        
        showMessage('Expense deleted successfully!');
        loadExpenses();    
    } catch (error) {
        showMessage(`Error deleting expense: ${error.message}`, true);
    }
}

function resetForm() {
    expenseForm.reset();
    formTitle.textContent = 'Add New Expense';
    submitBtn.textContent = 'Save Expense';
    
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    dateField.value = `${year}-${month}-${day}`;
    hideMessage();
}

function showMessage(message, isError = false) {
    messageContainer.textContent = message;
    messageContainer.className = `message ${isError ? 'error' : 'success'}`; 
    setTimeout(hideMessage, 5000);
}

function hideMessage() {
    messageContainer.className = 'message hidden';
}