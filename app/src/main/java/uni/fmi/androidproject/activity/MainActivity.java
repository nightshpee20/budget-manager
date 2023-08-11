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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> startForResultLauncher;

    private FloatingActionButton floatingActionButton,
                                 expenseIncomeFloatingActionButton,
                                 recurringExpenseIncomeFloatingActionButton;
    private Boolean areAllFabsVisible;
    private CalendarView calendarView;
    private RecyclerView transactionsRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;
    private SimpleDateFormat simpleDateFormat;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        expenseIncomeFloatingActionButton = findViewById(R.id.expenseIncomeFloatingActionButton);
        recurringExpenseIncomeFloatingActionButton = findViewById(R.id.recurringExpenseIncomeFloatingActionButton);

        expenseIncomeFloatingActionButton.setVisibility(View.GONE);
        recurringExpenseIncomeFloatingActionButton.setVisibility(View.GONE);

        areAllFabsVisible = false;

        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);

        floatingActionButton.setOnClickListener(this::floatingActionButtonOnClick);
        expenseIncomeFloatingActionButton.setOnClickListener(this::expenseIncomeFloatingActionButtonOnClick);
        recurringExpenseIncomeFloatingActionButton.setOnClickListener(this::recurringExpenseIncomeFloatingActionButtonOnClick);
        calendarView.setOnDateChangeListener(this::calendarViewOnDateChange);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        selectedDate = simpleDateFormat.format(new Date());

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);
        Transaction filter = new Transaction();
        filter.setDate(new Date());
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);

        recyclerViewAdapter = new RecyclerViewAdapter(this, transactionList);
        transactionsRecyclerView.setAdapter(recyclerViewAdapter);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        startForResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK)
                    updateRecyclerView();
            }
        );
    }

    public void updateRecyclerView() {
        Transaction filter = new Transaction();
        try {
            filter.setDate(simpleDateFormat.parse(selectedDate));
        } catch (ParseException e) {
            Toast.makeText(this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        List<Transaction> transactionList = transactionDao.getTransactionByFilter(filter);
        recyclerViewAdapter.setTransactionList(transactionList);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public void floatingActionButtonOnClick(View view) {
        if (!areAllFabsVisible) {
            expenseIncomeFloatingActionButton.show();
            recurringExpenseIncomeFloatingActionButton.show();

            areAllFabsVisible = true;

            return;
        }

        expenseIncomeFloatingActionButton.hide();
        recurringExpenseIncomeFloatingActionButton.hide();

        areAllFabsVisible = false;
    }

    public void expenseIncomeFloatingActionButtonOnClick(View view) {
        Intent intent = new Intent(this, ExpenseIncomeActivity.class);
        intent.putExtra("DATE", selectedDate);
//        startActivity(intent);
        startForResultLauncher.launch(intent);
    }

    public void recurringExpenseIncomeFloatingActionButtonOnClick(View view) {

    }

    public void calendarViewOnDateChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
        selectedDate = dayOfMonth+ "/" + ++month + "/" + year;
        updateRecyclerView();
    }
}