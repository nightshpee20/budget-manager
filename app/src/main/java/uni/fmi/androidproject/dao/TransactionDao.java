package uni.fmi.androidproject.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

import static uni.fmi.androidproject.model.Transaction.*;

public class TransactionDao {
    private final DataBaseHelper dataBaseHelper;
    private final DateTimeFormatter dateTimeFormatter;

    public TransactionDao(DataBaseHelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
        dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    }

    public List<Transaction> getTransactionByFilter(Transaction filter) {
        List<Transaction> result = new ArrayList<>();

        Integer idField = filter.getId();
        String description = filter.getDescription();
        Double amount = filter.getAmount();
        LocalDate date = filter.getDate();
        Boolean isIncome = filter.getIsIncome();
        Boolean isRecurring = filter.getIsRecurring();
        Integer interval = filter.getInterval();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM ")
                     .append(T_TRANSACTION)
                     .append(" WHERE 1=1 ");
        if (null != idField && idField > 0)
            stringBuilder.append(" AND id = ").append(idField);
        if (null != description && !description.isBlank())
            stringBuilder.append(" AND description = '").append(description).append('\'');
        if (null != amount && amount >= 0)
            stringBuilder.append(" AND amount = ").append(amount);
        if (null != date)
            stringBuilder.append(" AND date <= ").append(date.toEpochDay());
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
            String formattedDate = null;
            do {
                id = cursor.getInt(0);
                description = cursor.getString(1);
                amount = cursor.getDouble(2);
                formattedDate = cursor.getString(3);
                date = null != formattedDate && !formattedDate.isBlank() ? LocalDate.ofEpochDay(Long.parseLong(formattedDate)) : null;
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
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(DESCRIPTION, transaction.getDescription());
            contentValues.put(AMOUNT, transaction.getAmount());
            contentValues.put(DATE, transaction.getDate().toEpochDay());
            contentValues.put(IS_INCOME, transaction.getIsIncome() ? 1 : 0);
            contentValues.put(IS_RECURRING, transaction.getIsRecurring() ? 1 : 0);
            contentValues.put(INTERVAL, transaction.getInterval());

            long insert = sqLiteDatabase.insert(T_TRANSACTION,null,contentValues);
            return insert != -1;
        }
    }

    public double getBalanceByEpochDay(Long selectedDay) {
        double budget = 0d;
        List<Transaction> transactionList = new ArrayList<>();
        String query = "SELECT amount, date, is_income, is_recurring, interval FROM t_transaction WHERE date <= " + selectedDay;
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            if (!cursor.moveToFirst())
                return budget;

            do {
                double amount = cursor.getDouble(0);
                LocalDate date = LocalDate.ofEpochDay(cursor.getLong(1));
                boolean isIncome = cursor.getInt(2) == 1;
                boolean isRecurring = cursor.getInt(3) == 1;
                int interval = cursor.getInt(4);
                Transaction transaction = new Transaction(null, null, amount, date, isIncome, isRecurring, interval);
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        for (Transaction transaction : transactionList) {
            double multiplier = 1;
            if (!transaction.getIsIncome())
                multiplier = -1;

            double amount = transaction.getAmount();
            if (!transaction.getIsRecurring()) {
                budget += multiplier * amount;
                continue;
            }

            LocalDate transactionDate = transaction.getDate();
            long dayDifference = selectedDay - transactionDate.toEpochDay();
            int interval = switch (transaction.getInterval()) {
                case 0 -> 1;
                case 1 -> 7;
                case 2 -> 14;
                case 3 -> 21;
                case 4 -> 28;
                default -> 0;
            };

            int passedDays = 0;
            if (interval != 0) {
                do {
                    budget += multiplier * amount;
                    passedDays += interval;
                } while (passedDays <= dayDifference);
                continue;
            }

            interval = switch (transaction.getInterval()) {
                case 5 -> 1;
                case 6 -> 2;
                case 7 -> 3;
                case 8 -> 6;
                case 9 -> 12;
                default -> -1;
            };

            int year = transactionDate.getYear();
            int month = transactionDate.getMonthValue();
            passedDays = 0;
            do {
                budget += multiplier * amount;

                for (int i = 0; i < interval; i++) {
                    passedDays += YearMonth.of(year, month++).lengthOfMonth();
                    if (month > 12) {
                        year++;
                        month = 1;
                    }
                }

                dayDifference -= passedDays;
                passedDays = 0;
            } while (dayDifference >= 0);

        }
        return budget;
    }

    public void deleteAllRecords() {
        try (SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase()) {
            sqLiteDatabase.delete(T_TRANSACTION, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(Transaction transaction) {
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(T_TRANSACTION, ID + " = ?", new String[] {transaction.getId() + ""});
        sqLiteDatabase.close();
    }
}
