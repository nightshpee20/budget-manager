package uni.fmi.androidproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.database.DataBaseHelper;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton,
                                 expenseIncomeFloatingActionButton,
                                 recurringExpenseIncomeFloatingActionButton;
    private TextView expenseIncomeTextView,
                     recurringExpenseIncomeTextView;
    private Boolean areAllFabsVisible;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        expenseIncomeFloatingActionButton = findViewById(R.id.expenseIncomeFloatingActionButton);
        recurringExpenseIncomeFloatingActionButton = findViewById(R.id.recurringExpenseIncomeFloatingActionButton);
        expenseIncomeTextView = findViewById(R.id.expenseIncomeTextView);
        recurringExpenseIncomeTextView = findViewById(R.id.recurringExpenseIncomeTextView);

        expenseIncomeFloatingActionButton.setVisibility(View.GONE);
        recurringExpenseIncomeFloatingActionButton.setVisibility(View.GONE);
        expenseIncomeTextView.setVisibility(View.GONE);
        recurringExpenseIncomeTextView.setVisibility(View.GONE);

        areAllFabsVisible = false;

        floatingActionButton.setOnClickListener(this::floatingActionButtonOnClick);
        expenseIncomeFloatingActionButton.setOnClickListener(this::expenseIncomeFloatingActionButtonOnClick);
        recurringExpenseIncomeFloatingActionButton.setOnClickListener(this::recurringExpenseIncomeFloatingActionButtonOnClick);
    }

    public void floatingActionButtonOnClick(View view) {
        if (!areAllFabsVisible) {
            expenseIncomeFloatingActionButton.show();
            recurringExpenseIncomeFloatingActionButton.show();
            expenseIncomeTextView.setVisibility(View.VISIBLE);
            recurringExpenseIncomeTextView.setVisibility(View.VISIBLE);

            areAllFabsVisible = true;

            return;
        }

        expenseIncomeFloatingActionButton.hide();
        recurringExpenseIncomeFloatingActionButton.hide();
        expenseIncomeTextView.setVisibility(View.GONE);
        recurringExpenseIncomeTextView.setVisibility(View.GONE);

        areAllFabsVisible = false;
    }

    public void expenseIncomeFloatingActionButtonOnClick(View view) {
        Intent intent = new Intent(this, AddExpenseIncomeActivity.class);
        intent.putExtra("ACTIVITY_1", "ACTIVITY_1");
        startActivity(intent);
    }

    public void recurringExpenseIncomeFloatingActionButtonOnClick(View view) {

    }
}