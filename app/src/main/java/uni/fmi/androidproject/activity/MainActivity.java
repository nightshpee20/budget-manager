package uni.fmi.androidproject.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startForResultLauncher;

    private FloatingActionButton floatingActionButton,
                                 expenseIncomeFloatingActionButton,
                                 backupFloatingActionButton;
    private Boolean areAllFabsVisible;
    private CalendarView calendarView;
    private TextView balanceTextView;
    private RecyclerView transactionsRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;
    private DateTimeFormatter dateTimeFormatter;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        expenseIncomeFloatingActionButton = findViewById(R.id.expenseIncomeFloatingActionButton);
        backupFloatingActionButton = findViewById(R.id.backupFloatingActionButton);

        expenseIncomeFloatingActionButton.setVisibility(View.GONE);
        backupFloatingActionButton.setVisibility(View.GONE);

        areAllFabsVisible = false;

        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        floatingActionButton.setOnClickListener(this::floatingActionButtonOnClick);
        expenseIncomeFloatingActionButton.setOnClickListener(this::expenseIncomeFloatingActionButtonOnClick);
        backupFloatingActionButton.setOnClickListener(this::backupFloatingActionButtonOnClick);
        calendarView.setOnDateChangeListener(this::calendarViewOnDateChange);
        balanceTextView = findViewById(R.id.balanceTextView);

        dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        selectedDate = LocalDate.now().format(dateTimeFormatter);


        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);
        Transaction filter = new Transaction();
        filter.setDate(LocalDate.now());
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);

        recyclerViewAdapter = new RecyclerViewAdapter(this, transactionList);
        transactionsRecyclerView.setAdapter(recyclerViewAdapter);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        startForResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    updateRecyclerView();
                    LocalDate date = LocalDate.parse(selectedDate, dateTimeFormatter);
                    updateBalanceTextView(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                }
            }
        );

        LocalDate date = LocalDate.parse(selectedDate, dateTimeFormatter);
        updateBalanceTextView(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public void updateRecyclerView() {
        Transaction filter = new Transaction();
        filter.setDate(LocalDate.parse(selectedDate, dateTimeFormatter));
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);
        recyclerViewAdapter.setTransactionList(transactionList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void floatingActionButtonOnClick(View view) {
        if (!areAllFabsVisible) {
            expenseIncomeFloatingActionButton.show();
            backupFloatingActionButton.show();

            areAllFabsVisible = true;

            return;
        }

        expenseIncomeFloatingActionButton.hide();
        backupFloatingActionButton.hide();

        areAllFabsVisible = false;
    }

    public void expenseIncomeFloatingActionButtonOnClick(View view) {
        Intent intent = new Intent(this, ExpenseIncomeActivity.class);
        intent.putExtra("DATE", selectedDate);
//        startActivity(intent);
        startForResultLauncher.launch(intent);
    }

    public void backupFloatingActionButtonOnClick(View view) {
        Intent intent = new Intent(this, RegisterLoginActivity.class);
        startForResultLauncher.launch(intent);
    }

    public void calendarViewOnDateChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
        selectedDate = dayOfMonth+ "/" + ++month + "/" + year;
        updateBalanceTextView(year, month, dayOfMonth);
        updateRecyclerView();
    }

    private void updateBalanceTextView(int year, int month, int dayOfMonth) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        Double balance = transactionDao.getBalanceByEpochDay(date.toEpochDay());
        String text = String.format("Balance: %.2f", balance);
        balanceTextView.setText(text);
    }
}