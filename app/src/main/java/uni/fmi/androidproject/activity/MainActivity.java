package uni.fmi.androidproject.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startForResultLauncher;

    private FloatingActionButton floatingActionButton,
                                 expenseIncomeFloatingActionButton,
                                 backupFloatingActionButton,
                                 downloadFloatingActionButton;
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
        downloadFloatingActionButton = findViewById(R.id.downloadFloatingActionButton);

        expenseIncomeFloatingActionButton.setVisibility(View.GONE);
        backupFloatingActionButton.setVisibility(View.GONE);
        downloadFloatingActionButton.setVisibility(View.GONE);

        areAllFabsVisible = false;

        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        floatingActionButton.setOnClickListener(this::floatingActionButtonOnClick);
        expenseIncomeFloatingActionButton.setOnClickListener(this::expenseIncomeFloatingActionButtonOnClick);
        backupFloatingActionButton.setOnClickListener(this::backupFloatingActionButtonOnClick);
        downloadFloatingActionButton.setOnClickListener(this::downloadFloatingActionButtonOnClick);
        calendarView.setOnDateChangeListener(this::calendarViewOnDateChange);
        balanceTextView = findViewById(R.id.balanceTextView);

        dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        selectedDate = LocalDate.now().format(dateTimeFormatter);

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);
        Transaction filter = new Transaction();
        filter.setDate(LocalDate.now());
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);
        List<Transaction> filteredTransactionList = filterTransactionList(LocalDate.now(), LocalDate.now().toEpochDay(), transactionList);
        recyclerViewAdapter = new RecyclerViewAdapter(this, filteredTransactionList, transactionDao);
        recyclerViewAdapter.setBalanceTextView(balanceTextView);
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
        LocalDate filterDate = LocalDate.parse(selectedDate, dateTimeFormatter);
        Long filterDateEpochDay = filterDate.toEpochDay();
        filter.setDate(filterDate);
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);
        List<Transaction> filteredTransactionList = filterTransactionList(filterDate, filterDateEpochDay, transactionList);
        recyclerViewAdapter.setTransactionList(filteredTransactionList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private List<Transaction> filterTransactionList(LocalDate filterDate, Long filterDateEpochDay, List<Transaction> transactionList) {
        List<Transaction> filteredTransactionList = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getDate().equals(filterDate)) {
                filteredTransactionList.add(transaction);
                continue;
            }

            if (!transaction.getIsRecurring() && !transaction.getDate().equals(filterDate))
                continue;
            Long difference = filterDateEpochDay - transaction.getDate().toEpochDay();
            switch (transaction.getInterval()) {
                case 0:
                    filteredTransactionList.add(transaction);
                    break;
                case 1:
                    if (difference % 7 == 0)
                        filteredTransactionList.add(transaction);
                    break;
                case 2:
                    if (difference % 14 == 0)
                        filteredTransactionList.add(transaction);
                    break;
                case 3:
                    if (difference % 21 == 0)
                        filteredTransactionList.add(transaction);
                    break;
                case 4:
                    if (difference % 28 == 0)
                        filteredTransactionList.add(transaction);
                    break;
                case 5:
                    long monthsBetween = ChronoUnit.MONTHS.between(filterDate, transaction.getDate());
                    if (Math.abs(monthsBetween) == 1)
                        filteredTransactionList.add(transaction);
                    break;
                case 6:
                    long monthsBetween1 = ChronoUnit.MONTHS.between(filterDate, transaction.getDate());
                    if (Math.abs(monthsBetween1) == 2)
                        filteredTransactionList.add(transaction);
                    break;
                case 7:
                    long monthsBetween2 = ChronoUnit.MONTHS.between(filterDate, transaction.getDate());
                    if (Math.abs(monthsBetween2) == 3)
                        filteredTransactionList.add(transaction);
                    break;
                case 8:
                    long monthsBetween3 = ChronoUnit.MONTHS.between(filterDate, transaction.getDate());
                    if (Math.abs(monthsBetween3) == 6)
                        filteredTransactionList.add(transaction);
                    break;
                case 9:
                    long monthsBetween4 = ChronoUnit.MONTHS.between(filterDate, transaction.getDate());
                    if (Math.abs(monthsBetween4) == 12)
                        filteredTransactionList.add(transaction);
                    break;
            }
        }

        return filteredTransactionList;
    }

    public void floatingActionButtonOnClick(View view) {
        if (!areAllFabsVisible) {
            expenseIncomeFloatingActionButton.show();
            backupFloatingActionButton.show();
            downloadFloatingActionButton.show();

            areAllFabsVisible = true;

            return;
        }

        expenseIncomeFloatingActionButton.hide();
        backupFloatingActionButton.hide();
        downloadFloatingActionButton.show();

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
        intent.putExtra("ENDPOINT", "BACKUP");
        startForResultLauncher.launch(intent);
    }

    public void downloadFloatingActionButtonOnClick(View view) {
        Intent intent = new Intent(this, RegisterLoginActivity.class);
        intent.putExtra("ENDPOINT", "DOWNLOAD");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            updateRecyclerView();
            LocalDate selectedDateLocalDate = (LocalDate) dateTimeFormatter.parse(selectedDate);
            updateBalanceTextView(selectedDateLocalDate.getYear(), selectedDateLocalDate.getMonthValue(), selectedDateLocalDate.getDayOfMonth());
        }
    }
}