package uni.fmi.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.database.DataBaseHelper;
import uni.fmi.androidproject.model.Transaction;

public class RegisterLoginActivity extends AppCompatActivity {
    private static final String PATH = "http://192.168.165.60:8080";
    private EditText usernameEditText,
                     passwordEditText;

    private Button loginButton,
                   registerButton;

    private TextView pleaseWaitTextView;

    private FloatingActionButton goBackFloatingActionButton;

    private OkHttpClient httpClient;
    private Gson gson;
    private Type transactionType = new TypeToken<List<Transaction>>() {}.getType();
    private DataBaseHelper dataBaseHelper;
    private TransactionDao transactionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        dataBaseHelper = new DataBaseHelper(this);
        transactionDao = new TransactionDao(dataBaseHelper);
        httpClient = new OkHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Transaction.class, new Transaction.TransactionSerializer())
                .registerTypeAdapter(LocalDate.class, new Transaction.TransactionLocalDateDeserializer())
                .setExclusionStrategies(new Transaction.ExcludeTransactionStrategy())
                .create();

        pleaseWaitTextView = findViewById(R.id.registerLoginPleaseWaitTextView);

        usernameEditText = findViewById(R.id.registerLoginUsernameEditText);
        passwordEditText = findViewById(R.id.registerLoginPasswordEditText);

        loginButton = findViewById(R.id.registerLoginLoginButton);
        registerButton = findViewById(R.id.registerLoginRegisterButton);

        goBackFloatingActionButton = findViewById(R.id.registerLoginGoBackFloatingActionButton);
    }

    public void loginButtonOnClick(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        goBackFloatingActionButton.setEnabled(false);
        pleaseWaitTextView.setVisibility(View.VISIBLE);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.length() < 6 || 32 < username.length()) {
            Toast.makeText(this, "ERROR: Invalid username length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6 || 32 < password.length()) {
            Toast.makeText(this, "ERROR: Invalid password length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        String url = PATH + "/login";
        String json = "{\"user\": \"" + username + "\", \"pass\": \"" + hashedPassword + "\"}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                RegisterLoginActivity.this.runOnUiThread(() -> {
                    Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong during login!", Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    goBackFloatingActionButton.setEnabled(true);
                    pleaseWaitTextView.setVisibility(View.GONE);
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200) {
                    Integer id = Integer.parseInt(response.header("userId"));
                    String endpoint = getIntent().getStringExtra("ENDPOINT");
                    if (endpoint.equals("BACKUP")) {
                        List<Transaction> transactionList = transactionDao.getTransactionByFilter(new Transaction());
                        for (Transaction transaction : transactionList)
                            transaction.setUserId(id);
                        String url = PATH + "/transaction/backup";
                        String json = gson.toJson(transactionList);

                        String setCookie = response.header("Set-Cookie");
                        String session = setCookie.substring(11);
                        MediaType mediaType = MediaType.parse("application/json");
                        RequestBody requestBody = RequestBody.create(json, mediaType);
                        Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Cookie", "JSESSIONID=" + session)
                                .post(requestBody)
                                .build();

                        httpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                RegisterLoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong backing up your data!", Toast.LENGTH_LONG).show();
                                        goBack2ButtonOnClick(null);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                RegisterLoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterLoginActivity.this, "SUCCESS: Data backed up successfully!", Toast.LENGTH_LONG).show();
                                        goBack2ButtonOnClick(null);
                                    }
                                });
                            }
                        });
                    } else if (endpoint.equals("DOWNLOAD")) {
                        List<Transaction> transactionList;
                        String url = PATH + "/transaction/download";

                        String setCookie = response.header("Set-Cookie");
                        String session = setCookie.substring(11);
                        Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Cookie", "JSESSIONID=" + session)
                                .addHeader("userId", id + "")
                                .get()
                                .build();

                        httpClient.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                RegisterLoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong downloading your data. Try again later!", Toast.LENGTH_LONG).show();
                                        goBack2ButtonOnClick(null);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String json = response.body().string();
                                List<Transaction> transactionsList = gson.fromJson(json, transactionType);
                                List<Transaction> oldTransactions = transactionDao.getTransactionByFilter(new Transaction());
                                try {
                                    transactionDao.deleteAllRecords();
                                    for (Transaction transaction : transactionsList)
                                        transactionDao.insertTransaction(transaction);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    transactionDao.deleteAllRecords();
                                    for (Transaction transaction : oldTransactions)
                                        transactionDao.insertTransaction(transaction);
                                }

                                RegisterLoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterLoginActivity.this, "SUCCESS: Data downloaded successfully!", Toast.LENGTH_LONG).show();
                                        goBack2ButtonOnClick(null);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    public void registerButtonOnClick(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        goBackFloatingActionButton.setEnabled(false);
        pleaseWaitTextView.setVisibility(View.VISIBLE);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.length() < 6 || 32 < username.length()) {
            Toast.makeText(this, "ERROR: Invalid username length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6 || 32 < password.length()) {
            Toast.makeText(this, "ERROR: Invalid password length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        String url = PATH + "/register";
        String json = "{\"user\": \"" + username + "\", \"pass\": \"" + hashedPassword + "\"}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                RegisterLoginActivity.this.runOnUiThread(() -> {
                    Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong during register!", Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    goBackFloatingActionButton.setEnabled(true);
                    pleaseWaitTextView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                RegisterLoginActivity.this.runOnUiThread(() -> {
                    switch (response.code()) {
                        case 200 ->
                                Toast.makeText(RegisterLoginActivity.this, "!!SUCCESSFUL REGISTRATION!! Now login to continue!", Toast.LENGTH_LONG).show();
                        case 409 ->
                                Toast.makeText(RegisterLoginActivity.this, "!!USERNAME TAKEN!! Try another one!", Toast.LENGTH_LONG).show();
                    }
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    goBackFloatingActionButton.setEnabled(true);
                    pleaseWaitTextView.setVisibility(View.GONE);
                });
            }
        });
    }

    public void goBack2ButtonOnClick(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public String hashPassword(String password) {
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null!");

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashedPassword = new StringBuilder();
        for (int i: hash)
            hashedPassword.append(String.format("%02x", 0XFF & i));

        return hashedPassword.toString();
    }
}