package uni.fmi.androidproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import uni.fmi.androidproject.R;

public class RegisterLoginActivity extends AppCompatActivity {
    private EditText loginUsernameEditText,
                     loginPasswordEditTExt,
                     registerUsernameEditText,
                     registerPasswordEditText;

    private OkHttpClient httpClient;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        httpClient = new OkHttpClient();
        gson = new Gson();

        loginUsernameEditText = findViewById(R.id.registerLoginLoginUsernameEditText);
        loginPasswordEditTExt = findViewById(R.id.registerLoginLoginPasswordEditText);
        registerUsernameEditText = findViewById(R.id.registerLoginRegisterUsernameEditText);
        registerPasswordEditText = findViewById(R.id.registerLoginRegisterPasswordEditText);
    }

    public void loginButtonOnClick(View view) {
        String username = loginUsernameEditText.getText().toString();
        String password = loginPasswordEditTExt.getText().toString();

        if (username.length() < 6 || 32 < username.length()) {
            Toast.makeText(this, "ERROR: Invalid username length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6 || 32 < password.length()) {
            Toast.makeText(this, "ERROR: Invalid password length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        String url = "http://192.168.88.60:8080/login";
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
                RegisterLoginActivity.this.runOnUiThread(() -> Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong during login!", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200) {
                    RegisterLoginActivity.this.runOnUiThread(() -> {
                        Toast.makeText(RegisterLoginActivity.this, "!!SUCCESSFUL LOGIN!!", Toast.LENGTH_LONG).show();
                        goBack2ButtonOnClick(null);
                    });
                }
            }
        });
    }

    public void registerButtonOnClick(View view) {
        String username = registerUsernameEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();

        if (username.length() < 6 || 32 < username.length()) {
            Toast.makeText(this, "ERROR: Invalid username length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6 || 32 < password.length()) {
            Toast.makeText(this, "ERROR: Invalid password length! Length must be 6 - 32 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        String url = "http://192.168.88.60:8080/register";
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
                RegisterLoginActivity.this.runOnUiThread(() -> Toast.makeText(RegisterLoginActivity.this, "ERROR: Something went wrong during register!", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200) {
                    RegisterLoginActivity.this.runOnUiThread(() -> {
                        Toast.makeText(RegisterLoginActivity.this, "!!SUCCESSFUL REGISTRATION!!", Toast.LENGTH_LONG).show();
                        goBack2ButtonOnClick(null);
                    });
                }
            }
        });
    }

    public void goBack2ButtonOnClick(View view) {
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