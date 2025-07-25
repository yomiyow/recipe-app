package recipe_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import recipe_app.api.MealApi;
import recipe_app.api.callbacks.MealCallback;
import recipe_app.model.FavoriteManager;
import recipe_app.model.Meal;
import recipe_app.pages.FavoriteActivity;
import recipe_app.pages.MealDetailActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.school.recipeapp.R;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final Context context;
    private final List<Meal> meals;
    private boolean isFavoritesOnly = false;

    public MealAdapter(Context context, List<Meal> meals) {
        this.context = context;
        this.meals = meals;
    }

    public MealAdapter(Context context, List<Meal> meals, boolean isFavoritesOnly) {
        this.context = context;
        this.meals = meals;
        this.isFavoritesOnly = isFavoritesOnly;
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

        // Set favorite icon based on favorite state
        FavoriteManager.isFavorite(meal.getId(), isFav -> {
            mealCard.favoriteBtn.setIconResource(
                    isFav ? R.drawable.ic_favorite : R.drawable.ic_outline_favorite
            );
            mealCard.favoriteBtn.setTag(isFav); // <-- Add this line
        });

        mealCard.favoriteBtn.setOnClickListener(v -> {
            Boolean isFav = (Boolean) mealCard.favoriteBtn.getTag();
            int adapterPosition = mealCard.getAdapterPosition();

            if (isFav != null && isFav) {
                FavoriteManager.removeFavorite(meal.getId());
                if (isFavoritesOnly) {
                    removeMeal(adapterPosition);
                    if (meals.isEmpty() && context instanceof FavoriteActivity) {
                        ((FavoriteActivity) context).updateView();
                    }
                } else {
                    mealCard.favoriteBtn.setIconResource(R.drawable.ic_outline_favorite);
                    mealCard.favoriteBtn.setTag(false);
                }
            } else {
                FavoriteManager.addFavorite(meal);
                mealCard.favoriteBtn.setIconResource(R.drawable.ic_favorite);
                mealCard.favoriteBtn.setTag(true);
            }
        });

        mealCard.viewBtn.setOnClickListener(v -> {
            // Fetch full information of meal
            new MealApi(context).fetchMeal(meal.getId(), new MealCallback() {
                @Override
                public void onSuccess(Meal meal) {
                   Intent intent = new Intent(context, MealDetailActivity.class);
                   intent.putExtra("meal", meal);
                   context.startActivity(intent);
                }
                @Override
                public void onError(Exception e) {
                    Log.e("MealAdapter", "Error fetching meal", e);
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void removeMeal(int position) {
        meals.remove(position);
        notifyItemRemoved(position);
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
