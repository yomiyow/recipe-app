package recipe_app.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import recipe_app.api.callbacks.FavoriteCallback;
import recipe_app.api.callbacks.MealsCallback;

public class FavoriteManager {

    public static void addFavorite(Meal meal) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(userId)
                .child(meal.getId());
        dbRef.setValue(meal);
    }

    public static void removeFavorite(String mealId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(userId)
                .child(mealId);
        dbRef.removeValue();
    }

    public static void isFavorite(String mealId, FavoriteCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(userId)
                .child(mealId);

        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                callback.onResult(true);
            } else {
                callback.onResult(false);
            }
        });
    }

    public static void getFavorites(MealsCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(userId);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Meal> favoriteMeals = new ArrayList<>();
                for (DataSnapshot mealSnap : snapshot.getChildren()) {
                    Meal meal = mealSnap.getValue(Meal.class);
                    if (meal != null) {
                        favoriteMeals.add(meal);
                    }
                }
                callback.onSuccess(favoriteMeals);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

}
