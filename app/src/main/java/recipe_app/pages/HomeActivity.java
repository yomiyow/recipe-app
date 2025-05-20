package recipe_app.pages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.school.recipeapp.R;

import java.util.ArrayList;
import java.util.List;

import recipe_app.adapter.MealAdapter;
import recipe_app.api.MealApi;
import recipe_app.api.callbacks.CategoryCallback;
import recipe_app.api.callbacks.MealsCallback;
import recipe_app.model.Meal;

public class HomeActivity extends AppCompatActivity {

    private Context c;
    private MealApi mealApi;
    private final String TAG = "HomeActivity";
    private List<Meal> mealList;
    MealAdapter mealAdapter;

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

    private void init() {
        // Xml element
        chipGroup = findViewById(R.id.chipGroup);
        RecyclerView mealRecyclerView = findViewById(R.id.mealRecyclerView);

        // Class variable
        c = HomeActivity.this;
        mealApi = new MealApi(c);
        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(c, mealList);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(c));
        mealRecyclerView.setAdapter(mealAdapter);

        // Functions Call
        renderCategoryChip();
    }

    private void fetchMealsByCategory(String category) {
        mealApi.fetchMealsByCategory(category, new MealsCallback() {
            @Override
            public void onSuccess(List<Meal> meals) {
                mealList.clear();
                mealList.addAll(meals);
                mealAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error fetching meals", e);
            }
        });
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
                        chip.setOnClickListener(v -> fetchMealsByCategory(category));
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

}