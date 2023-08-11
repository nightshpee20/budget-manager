package uni.fmi.androidproject.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

import static uni.fmi.androidproject.model.Transaction.*;

public class TransactionDao {
    private final DataBaseHelper dataBaseHelper;
    private final SimpleDateFormat simpleDateFormat;

    public TransactionDao(DataBaseHelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public List<Transaction> getTransactionByFilter(Transaction filter) {
        List<Transaction> result = new ArrayList<>();

        String description = filter.getDescription();
        Double amount = filter.getAmount();
        String formattedDate = filter.getFormattedDate();
        Boolean isIncome = filter.getIsIncome();
        Boolean isRecurring = filter.getIsRecurring();
        Integer interval = filter.getInterval();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ")
                     .append(T_TRANSACTION)
                     .append(" WHERE 1=1 ");

        if (null != description && !description.isBlank())
            stringBuilder.append(" AND description = '").append(description).append('\'');
        if (null != amount && amount >= 0)
            stringBuilder.append(" AND amount = ").append(amount);
        if (null != formattedDate && !formattedDate.isBlank())
            stringBuilder.append(" AND date = '").append(formattedDate).append('\'');
        if (null != isIncome)
            stringBuilder.append(" AND is_income = ").append(isIncome);
        if (null != isRecurring)
            stringBuilder.append(" AND is_recurring = ").append(isRecurring);
        if (null != interval)
            stringBuilder.append(" AND interval = ").append(interval);

        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(stringBuilder.toString(), null)) {
            if (!cursor.moveToFirst())
                return result;

            Integer id;
            Date date = null;
            do {
                id = cursor.getInt(0);
                description = cursor.getString(1);
                amount = cursor.getDouble(2);
                try {
                    formattedDate = cursor.getString(3);
                    date = null != formattedDate && !formattedDate.isBlank() ? simpleDateFormat.parse(formattedDate) : null;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isIncome = cursor.getInt(4) == 1;
                isRecurring = cursor.getInt(5) == 1;
                interval = cursor.getInt(6);
                Transaction transaction = new Transaction(id, description, amount, date, isIncome, isRecurring, interval);
                result.add(transaction);
            } while (cursor.moveToNext());
        }

        return result;
    }

    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DESCRIPTION, transaction.getDescription());
        contentValues.put(AMOUNT, transaction.getAmount());
        contentValues.put(DATE, transaction.getFormattedDate());
        contentValues.put(IS_INCOME, transaction.getIsIncome() ? 1 : 0);
        contentValues.put(IS_RECURRING, transaction.getIsRecurring() ? 1 : 0);
        contentValues.put(INTERVAL, transaction.getInterval());

        long insert = sqLiteDatabase.insert(T_TRANSACTION,null,contentValues);
        return insert != -1;
    }
}
