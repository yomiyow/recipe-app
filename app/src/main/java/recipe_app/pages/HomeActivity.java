package recipe_app.pages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.school.recipeapp.R;

import java.util.List;

import recipe_app.api.MealApi;

public class HomeActivity extends AppCompatActivity {
    private Context c;
    private final String TAG = "HomeActivity";
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
        c = HomeActivity.this;
        chipGroup = findViewById(R.id.chipGroup);

        renderCategoryChip();
    }

    private void renderCategoryChip() {
        new MealApi().fetchCategories(c, new MealApi.CategoryCallback() {
            @Override
            public void onSuccess(List<String> categories) {
                runOnUiThread(() -> {
                    chipGroup.removeAllViews();
                    for (String category : categories) {
                        Chip chip = new Chip(c);
                        chip.setText(category);
                        chip.setCheckable(true);
                        chipGroup.addView(chip);
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