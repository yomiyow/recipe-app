package recipe_app.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.school.recipeapp.R;

import java.util.List;

import recipe_app.adapter.MealAdapter;
import recipe_app.api.callbacks.MealsCallback;
import recipe_app.model.FavoriteManager;
import recipe_app.model.Meal;
import recipe_app.pages.auth.LoginActivity;

public class FavoriteActivity extends AppCompatActivity {

    private Context c;
    private BottomNavigationView bottomNavigation;

    private RecyclerView favoriteMealRV;
    private LinearLayout emptyView;
    private MealAdapter mealAdapter;
    private List<Meal> favoriteMeals;

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
    protected void onResume() {
        super.onResume();
        renderFavoriteMeals();
    }

    private void init() {
        c = FavoriteActivity.this;
        bottomNavigation = findViewById(R.id.bottomNav);
        emptyView = findViewById(R.id.emptyView);

        // RecyclerView
        favoriteMealRV = findViewById(R.id.favoriteMealRecyclerView);
        favoriteMealRV.setLayoutManager(new LinearLayoutManager(c));

        handleBottomNavClick();
        renderFavoriteMeals();
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
            } else if (id == R.id.logoutNav) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(c, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.favoriteNav);
    }

    private void renderFavoriteMeals() {
        // Fetch favorite meal from database
        FavoriteManager.getFavorites(new MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                favoriteMeals = meals;

                updateView();

                mealAdapter = new MealAdapter(c, favoriteMeals, true);
                favoriteMealRV.setAdapter(mealAdapter);
            }
            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void updateView() {
        // check what view to render
        if (favoriteMeals.isEmpty()) {
            favoriteMealRV.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            favoriteMealRV.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}