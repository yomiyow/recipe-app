package recipe_app.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.school.recipeapp.R;

public class FavoriteActivity extends AppCompatActivity {

    private Context c;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

    }

    private void init() {
        c = FavoriteActivity.this;
        bottomNavigation = findViewById(R.id.bottomNav);

        handleBottomNavClick();
    }

    private void handleBottomNavClick() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNav) {
                startActivity(new Intent(c, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.favoriteNav) {
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.favoriteNav);
    }
}