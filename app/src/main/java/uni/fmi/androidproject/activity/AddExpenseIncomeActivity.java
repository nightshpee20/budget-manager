package uni.fmi.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class AddExpenseIncomeActivity extends AppCompatActivity {

    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_income);

        Intent intent = getIntent();

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);
//        EditText editText = findViewById(R.id.editText);
//        editText.setText(intent.getStringExtra("ACTIVITY_1"));
    }

    public void addExpenseIncomeButtonOnClick(View view) {
        Transaction transaction = new Transaction();
        transaction.setId(new Random().nextInt() * 1000);
        transaction.setDescription("TEST TRANSACTION");
        transaction.setAmount(100d);
        transaction.setDate(new SimpleDateFormat("dd/MM/YYYY").format(new Date()));
        transaction.setIsExpense(true);
        transaction.setIsRecurring(true);


        boolean isSuccessful = transactionDao.insertTransaction(transaction);

        Toast.makeText(this, isSuccessful ? "Success!!!" : "FAIL!!!", Toast.LENGTH_SHORT).show();
    }
}