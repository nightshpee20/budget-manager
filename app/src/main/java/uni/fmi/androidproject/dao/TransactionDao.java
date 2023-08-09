package uni.fmi.androidproject.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

import static uni.fmi.androidproject.model.Transaction.*;

public class TransactionDao {
    private final DataBaseHelper dataBaseHelper;

    public TransactionDao(DataBaseHelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
    }

    public List<Transaction> getTransactionByFilter(Transaction filter) {
        List<Transaction> result = new ArrayList<>();

        String query = String.format(
                """
                SELECT * FROM %s
                """,
                T_TRANSACTION);

        return result;
    }

    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DESCRIPTION, transaction.getDescription());
        cv.put(AMOUNT, transaction.getAmount());
        cv.put(DATE, transaction.getDate());
        cv.put(IS_EXPENSE, transaction.getIsExpense());
        cv.put(IS_RECURRING, transaction.getIsRecurring());

        long insert = db.insert(T_TRANSACTION,null,cv);
        return insert != -1;
    }
}
