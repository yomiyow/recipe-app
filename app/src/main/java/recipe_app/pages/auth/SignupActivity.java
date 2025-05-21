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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.school.recipeapp.R;

public class SignupActivity extends AppCompatActivity {

    private Context c;
    private FirebaseAuth auth;
    private TextInputEditText fullNameET, emailET, passwordET;
    private Button signupBtn, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        c = SignupActivity.this;
        auth = FirebaseAuth.getInstance();

        fullNameET = findViewById(R.id.fullNameField);
        emailET = findViewById(R.id.emailField);
        passwordET = findViewById(R.id.passwordField);
        signupBtn = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(v -> handleNavToLogin());
        signupBtn.setOnClickListener(v -> handleSignup());
    }

    private void handleSignup() {
        String fullName = fullNameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameET.setError("Full name is required");
            return;
        }
        if (email.isEmpty()) {
            emailET.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                            user.sendEmailVerification();
                            new MaterialAlertDialogBuilder(c)
                                .setTitle("Signup Successful")
                                .setMessage("Signup successful! Check your email for verification. You will need to verify your email before logging in.")
                                .setPositiveButton("OK", (dialog, which) -> handleNavToLogin())
                                .setCancelable(false)
                                .show();
                        });
                    }
                } else {
                    Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void handleNavToLogin() {
        startActivity(new Intent(c, LoginActivity.class));
    }
}