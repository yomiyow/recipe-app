package recipe_app.pages.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.school.recipeapp.R;

import recipe_app.pages.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private Context c;
    private FirebaseAuth auth;
    private TextInputEditText emailET, passwordET;
    private Button loginBtn, signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) handleNavToHome();
    }

    private void init() {
        c = LoginActivity.this;
        auth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailField);
        passwordET = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.signupLink);

        loginBtn.setOnClickListener(v -> handleLogin());
        signupLink.setOnClickListener(v -> handleNavToSignup());
    }

    private void handleLogin() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (email.isEmpty()) {
            emailET.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        // Redirect to HomeActivity
                        handleNavToHome();
                        finish();
                    } else {
                        // Email not verified
                        Toast.makeText(c, "Login failed. User not found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(c, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void handleNavToSignup() {
        startActivity(new Intent(c, SignupActivity.class));
    }

    private void handleNavToHome() {
        startActivity(new Intent(c, HomeActivity.class));
    }
}