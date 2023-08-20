package uni.fmi.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class ExpenseIncomeActivity extends AppCompatActivity {

    private Intent intent;
    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;
    private DateTimeFormatter dateTimeFormatter;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private Switch isIncomeSwitch;
    private TextView isIncomeTextView;
    private Switch isRecurringSwitch;
    private TextView isRecurringTextView;
    private TextView intervalTextView;
    private Spinner intervalSpinner;
    private TextView labelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_income);

        intent = getIntent();

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);

        dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        TextView dateTextView = findViewById(R.id.expenseIncomeDateTextView);
        dateTextView.append(" " + getIntent().getStringExtra("DATE"));
        descriptionEditText = findViewById(R.id.expenseIncomeDescriptionEditText);
        amountEditText = findViewById(R.id.expenseIncomeAmountEditText);
        isIncomeSwitch = findViewById(R.id.expenseIncomeIsIncomeSwitch);
        isIncomeTextView = findViewById(R.id.expenseIncomeIsExpenseTextView);
        isRecurringSwitch = findViewById(R.id.expenseIncomeIsRecurringSwitch);
        isRecurringTextView = findViewById(R.id.expenseIncomeIsRecurringTextView);
        intervalTextView = findViewById(R.id.expenseIncomeIntervalTextView);
        intervalSpinner = findViewById(R.id.expenseIncomeIntervalSpinner);
        labelTextView = findViewById(R.id.expenseIncomeLabelTextView);
        String description = intent.getStringExtra("DESCRIPTION");

        intervalTextView.setVisibility(View.GONE);
        intervalSpinner.setVisibility(View.GONE);

        if (description != null) {
            labelTextView.setText("Edit Expense/Income");
            descriptionEditText.setText(description);
            amountEditText.setText(intent.getDoubleExtra("AMOUNT",0) + "");
            boolean isIncome = intent.getBooleanExtra("IS_INCOME", false);
            if (isIncome) {
                isIncomeSwitch.setChecked(true);
                isIncomeTextView.setText("Income");
            }
            boolean isRecurring = intent.getBooleanExtra("IS_RECURRING", false);
            if (isRecurring) {
                isRecurringSwitch.setChecked(true);
                isRecurringTextView.setText("True");
            }
            if (isRecurring) {
                int interval = intent.getIntExtra("INTERVAL", -1);
                intervalSpinner.setVisibility(View.VISIBLE);
                intervalSpinner.setSelection(interval);
                intervalTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void isExpenseSwitchOnClick(View view) {
        isIncomeTextView.setText(isIncomeSwitch.isChecked() ? "Income" : "Expense");
    }

    public void isRecurringSwitchOnClick(View view) {
        isRecurringTextView.setText(isRecurringSwitch.isChecked() ? "True" : "False");

        if (isRecurringSwitch.isChecked()) {
            intervalTextView.setVisibility(View.VISIBLE);
            intervalSpinner.setVisibility(View.VISIBLE);
            return;
        }

        intervalTextView.setVisibility(View.GONE);
        intervalSpinner.setVisibility(View.GONE);
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
        Boolean isExpense = isIncomeSwitch.isChecked();
        Boolean isRecurring = isRecurringSwitch.isChecked();
        String formattedDate = intent.getStringExtra("DATE");
        LocalDate date = null != formattedDate && !formattedDate.isBlank() ? LocalDate.parse(formattedDate, dateTimeFormatter) : null;
        Double amount = null;

        Integer interval = null;
        if (isRecurring) {
            String intervalStr = (String) intervalSpinner.getSelectedItem();
            String[] intervals = getResources().getStringArray(R.array.intervals);
            for (int i = 0; i < intervals.length; i++)
                if (intervalStr.equals(intervals[i]))
                    interval = i;
        }

        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Transaction transaction = new Transaction(description, amount, date, isExpense, isRecurring, interval);
        if (labelTextView.getText().toString().startsWith("Edit")) {
            int id = intent.getIntExtra("ID", -1);
            transaction.setId(id);
            transactionDao.updateTransaction(transaction);
            setResult(RESULT_OK);
            finish();
        } else {
            boolean isSuccessful = transactionDao.insertTransaction(transaction);
            if (isSuccessful) {
                setResult(RESULT_OK);
                finish();
            } else
                Toast.makeText(this, "ERROR: Something went wrong.", Toast.LENGTH_LONG).show();
        }
    }
    public void goBackButtonOnClick(View view) {
        finish();
    }
}