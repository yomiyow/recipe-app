package recipe_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import recipe_app.model.Meal;
import recipe_app.pages.MealDetailActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.school.recipeapp.R;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final Context context;
    private final List<Meal> meals;

    public MealAdapter(Context context, List<Meal> meals) {
        this.context = context;
        this.meals = meals;
    }

    @NonNull
    @Override
    public MealAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealAdapter.MealViewHolder mealCard, int position) {
        Meal meal = meals.get(position);
        Glide.with(context).load(meal.getThumbUrl()).into(mealCard.mealImage);
        mealCard.mealName.setText(meal.getName());

        mealCard.viewBtn.setOnClickListener(v -> {
           Intent intent = new Intent(context, MealDetailActivity.class);
           intent.putExtra("meal", meal);
           context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {

        ImageView mealImage;
        TextView mealName;
        MaterialButton favoriteBtn;
        MaterialButton viewBtn;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
            viewBtn = itemView.findViewById(R.id.viewBtn);
        }
    }
}
