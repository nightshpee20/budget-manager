package uni.fmi.androidproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import uni.fmi.androidproject.model.Transaction;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, "budget_managed.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = """
            CREATE TABLE T_TRANSACTION (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                DESCRIPTION TEXT,
                AMOUNT REAL,
                DATE TEXT,
                IS_EXPENSE BOOL,
                IS_RECURRING BOOL
            )
        """;
        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
