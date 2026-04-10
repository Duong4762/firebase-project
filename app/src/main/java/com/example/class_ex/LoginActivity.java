package com.example.class_ex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email;
    private TextInputEditText password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            goMain();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        MaterialButton login = findViewById(R.id.btnLogin);
        MaterialButton register = findViewById(R.id.btnRegister);

        login.setOnClickListener(v -> doLogin(false));
        register.setOnClickListener(v -> doLogin(true));
    }

    private void doLogin(boolean isRegister) {
        String e = email.getText() != null ? email.getText().toString().trim() : "";
        String p = password.getText() != null ? password.getText().toString() : "";
        if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
            Toast.makeText(this, R.string.err_fill_all, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isRegister) {
            auth.createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener(r -> onAuthSuccess(r.getUser()))
                    .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            auth.signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener(r -> onAuthSuccess(r.getUser()))
                    .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private void onAuthSuccess(FirebaseUser user) {
        if (user == null) {
            return;
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("email", user.getEmail());
        profile.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : "");
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .set(profile, com.google.firebase.firestore.SetOptions.merge())
                .addOnCompleteListener(t -> {
                    com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                            .addOnSuccessListener(token -> {
                                Map<String, Object> m = new HashMap<>();
                                m.put("fcmToken", token);
                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(user.getUid())
                                        .set(m, com.google.firebase.firestore.SetOptions.merge());
                            });
                    goMain();
                });
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
