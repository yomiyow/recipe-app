package recipe_app.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.school.recipeapp.R;

import java.util.ArrayList;
import java.util.List;

import recipe_app.adapter.MealAdapter;
import recipe_app.api.MealApi;
import recipe_app.api.callbacks.CategoryCallback;
import recipe_app.api.callbacks.MealsCallback;
import recipe_app.model.Meal;
import recipe_app.pages.auth.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    private Context c;
    private MealApi mealApi;
    private final String TAG = "HomeActivity";
    private List<Meal> mealList;
    private MealAdapter mealAdapter;
    private BottomNavigationView bottomNavigation;
    private TextInputEditText searchET;
    private List<Meal> searchedMeals;

    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

    }

    @Override
    protected void onResume() {
        // refresh the state of meal card
        super.onResume();
        mealAdapter.notifyDataSetChanged();
    }

    private void init() {
        // UI element
        chipGroup = findViewById(R.id.chipGroup);
        RecyclerView mealRecyclerView = findViewById(R.id.mealRecyclerView);
        bottomNavigation = findViewById(R.id.homeBottomNav);
        searchET = findViewById(R.id.searchField);

        // Class variable
        c = HomeActivity.this;
        mealApi = new MealApi(c);
        mealList = new ArrayList<>();
        searchedMeals = new ArrayList<>();
        mealAdapter = new MealAdapter(c, mealList);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(c));
        mealRecyclerView.setAdapter(mealAdapter);

        // Functions Call
        renderCategoryChip();
        handleBottomNavClick();
        handleSearch();

    }

    private void renderCategoryChip() {
        mealApi.fetchCategories(new CategoryCallback() {
            @Override
            public void onSuccess(List<String> categories) {
                runOnUiThread(() -> {
                    chipGroup.removeAllViews();
                    for (int i = 0; i < categories.size(); i++) {
                        String category = categories.get(i);
                        Chip chip = new Chip(c);
                        chip.setText(category);
                        chip.setCheckable(true);
                        chip.setOnClickListener(v -> {
                            fetchMealsByCategory(category);
                        });
                        chipGroup.addView(chip);

                        // select the first chip by default
                        if (i == 0) {
                            chip.setChecked(true);
                            fetchMealsByCategory(category);
                        }
                    }
                });
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching categories", e);
            }
        });
    }

    private void fetchMealsByCategory(String category) {
        mealApi.fetchMealsByCategory(category, new MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                searchedMeals.clear();
                searchedMeals.addAll(meals);
                String keyword = searchET.getText().toString().toLowerCase().trim();

                mealList.clear();
                if (keyword.isEmpty()) {
                    mealList.addAll(meals);
                } else {
                    for (Meal meal : meals) {
                        if (meal.getName().toLowerCase().contains(keyword)) {
                            mealList.add(meal);
                        }
                    }
                }
                mealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching meals", e);
            }
        });
    }

    private void handleSearch() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMeals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    };

    private void handleBottomNavClick() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNav) {
                return true;
            } else if (id == R.id.favoriteNav) {
                startActivity(new Intent(c, FavoriteActivity.class));
                finish();
                return true;
            } else if (id == R.id.logoutNav) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(c, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.homeNav);
    }

    private void filterMeals(String query) {
        mealList.clear();
        if (query.isEmpty()) {
            mealList.addAll(searchedMeals);
        } else {
            for (Meal meal : searchedMeals) {
                if (meal.getName().toLowerCase().contains(query.toLowerCase())) {
                    mealList.add(meal);
                }
            }
        }
        mealAdapter.notifyDataSetChanged();
    }

}