package recipe_app.pages;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.FirebaseDatabase;
import com.school.recipeapp.R;

import recipe_app.api.callbacks.FavoriteCallback;
import recipe_app.model.FavoriteManager;
import recipe_app.model.Meal;
import recipe_app.utils.ToastUtil;

public class MealDetailActivity extends AppCompatActivity {

    private Context c;
    private FirebaseDatabase db;
    private Meal meal;
    private boolean isFavorite = false;
    private MaterialToolbar topToolBar;
    private MenuItem favoriteMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        favoriteMenuItem = menu.findItem(R.id.favorite);
        updateFavoriteIcon();
        return true;
    }

    private void init() {
        c = MealDetailActivity.this;
        db = FirebaseDatabase.getInstance();
        topToolBar = findViewById(R.id.topToolBar);
        setSupportActionBar(topToolBar);

        handleReturnHome();
        renderMealDetails();
        handleFavoriteClick();

        FavoriteManager.isFavorite(meal.getId(), new FavoriteCallback() {
            @Override
            public void onResult(boolean isFav) {
                isFavorite = isFav;
                runOnUiThread(() -> updateFavoriteIcon()); // Ensure icon updates when Firebase returns
            }
        });
    }

    private void handleReturnHome() {
        topToolBar.setNavigationOnClickListener(v -> finish());
    }

    private void renderMealDetails() {
        ImageView mealThumbIV = findViewById(R.id.mealThumbIV);
        TextView countryTV = findViewById(R.id.countryTV);
        TextView categoryTV = findViewById(R.id.categoryTV);
        TextView mealNameTV = findViewById(R.id.mealNameTV);

        meal = getIntent().getParcelableExtra("meal");

        Glide.with(c).load(meal.getThumbUrl()).into(mealThumbIV);
        countryTV.setText(meal.getCountry());
        categoryTV.setText(meal.getCategory());
        mealNameTV.setText(meal.getName());

        // Ingredients & Instructions are created dynamically below
        loadIngredients(meal);
        loadInstructions(meal);

    }

    private void loadIngredients(Meal meal) {
        LinearLayout ingredientsContainer = findViewById(R.id.ingredientsContainer);
        ingredientsContainer.removeAllViews();

        for (int i = 0; i < meal.getIngredientList().size(); i++) {
            String item = meal.getIngredientList().get(i);
            String ingredient = item;
            String measure = "";
            int idx = item.lastIndexOf("(");
            if (idx != -1 && item.endsWith(")")) {
                ingredient = item.substring(0, idx).trim();
                measure = item.substring(idx + 1, item.length() - 1).trim();
            }

            // horizontal row for ingredient & measure
            LinearLayout row = new LinearLayout(c);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 15, 0, 15);

            // Ingredient TextView
            TextView ingredientTV = new TextView(c);
            ingredientTV.setText(ingredient);
            ingredientTV.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            // Measure TextView
            TextView measureTV = new TextView(c);
            measureTV.setText(measure);
            measureTV.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            row.addView(ingredientTV);
            row.addView(measureTV);

            ingredientsContainer.addView(row);

            // Add divider in each row
            if (i < meal.getIngredientList().size() - 1) {
                MaterialDivider divider = new MaterialDivider(c);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                params.setMargins(0, 8, 0, 8);
                divider.setLayoutParams(params);
                ingredientsContainer.addView(divider);
            }
        }
    }

    private void loadInstructions(Meal meal) {
        LinearLayout instructionsContainer = findViewById(R.id.instructionsContainer);
        instructionsContainer.removeAllViews();

        for (int i = 0; i < meal.getInstructionStep().size(); i++) {
            String step = meal.getInstructionStep().get(i);

            // Create a horizontal row for number and text
            LinearLayout row = new LinearLayout(c);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 20, 0, 20);

            // Number TextView
            TextView numberTV = new TextView(c);
            numberTV.setText((i + 1) + ".");
            numberTV.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
            numberTV.setPadding(0, 0, 16, 0);
            numberTV.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Step TextView
            MaterialTextView stepTV = new MaterialTextView(c);
            stepTV.setText(step + ".");
            stepTV.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
            stepTV.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            row.addView(numberTV);
            row.addView(stepTV);

            instructionsContainer.addView(row);

            //  add divider between steps
            if (i < meal.getInstructionStep().size() - 1) {
                MaterialDivider divider = new MaterialDivider(c);
                instructionsContainer.addView(divider);
            }
        }
    }

    private void handleFavoriteClick() {
        topToolBar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.favorite) {
                if (isFavorite) {
                    FavoriteManager.removeFavorite(meal.getId());
                    isFavorite = false;
                    ToastUtil.showToast(c, "Removed from Favorite", Toast.LENGTH_SHORT);
                } else {
                    FavoriteManager.addFavorite(meal);
                    isFavorite = true;
                    ToastUtil.showToast(c, "Added to Favorite", Toast.LENGTH_SHORT);
                }
                updateFavoriteIcon();
                return true;
            }
            return false;
        });
    }

    private void updateFavoriteIcon() {
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setIcon(isFavorite
                    ? R.drawable.ic_favorite
                    : R.drawable.ic_outline_favorite
            );
        }
    }

}