package uni.fmi.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class ExpenseIncomeActivity extends AppCompatActivity {

    private Intent intent;
    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;
    private SimpleDateFormat simpleDateFormat;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Switch isExpenseSwitch;
    private TextView isExpenseTextView;
    private Switch isRecurringSwitch;
    private TextView isRecurringTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_income);

        intent = getIntent();

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        TextView dateTextView = findViewById(R.id.expenseIncomeDateTextView);
        dateTextView.append(" " + getIntent().getStringExtra("DATE"));
        descriptionEditText = findViewById(R.id.expenseIncomeDescriptionEditText);
        amountEditText = findViewById(R.id.expenseIncomeAmountEditText);
        isExpenseSwitch = findViewById(R.id.expenseIncomeIsExpenseSwitch);
        isExpenseTextView = findViewById(R.id.expenseIncomeIsExpenseTextView);
        isRecurringSwitch = findViewById(R.id.expenseIncomeIsRecurringSwitch);
        isRecurringTextView = findViewById(R.id.expenseIncomeIsRecurringTextView);
    }

    public void isExpenseSwitchOnClick(View view) {
        isExpenseTextView.setText(isExpenseSwitch.isChecked() ? "Expense" : "Income");
    }

    public void isRecurringSwitchOnClick(View view) {
        isExpenseTextView.setText(isRecurringSwitch.isChecked() ? "True" : "False");
    }

    public String validate() {
        String description = descriptionEditText.getText().toString();
        if (description.isBlank())
            return "Description cannot be blank!";

        String amountStr = amountEditText.getText().toString();
        if (amountStr.isBlank())
            return "Amount cannot be blank!";
        try {
            Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return "Amount must be a positive decimal number.";
        }

        return null;
    }

    public void addButtonOnClick(View view) {
        String validateResult = validate();
        if (null != validateResult) {
            Toast.makeText(this, "ERROR: " + validateResult, Toast.LENGTH_LONG).show();
            return;
        }

        String description = descriptionEditText.getText().toString();
        Boolean isExpense = isExpenseSwitch.isChecked();
        Boolean isRecurring = isRecurringSwitch.isChecked();
        String formattedDate = intent.getStringExtra("DATE");
        Double amount = null;
        Date date = null;
        try {
            date = null != formattedDate && !formattedDate.isBlank() ? simpleDateFormat.parse(formattedDate) : null;
            amount = Double.parseDouble(amountEditText.getText().toString());
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Transaction transaction = new Transaction(description, amount, date, isExpense, isRecurring);
        boolean isSuccessful = transactionDao.insertTransaction(transaction);
        if (isSuccessful)
            finish();
        else
            Toast.makeText(this, "ERROR: Something went wrong.", Toast.LENGTH_LONG).show();
    }
    public void goBackButtonOnClick(View view) {
        finish();
    }
}