package recipe_app.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import recipe_app.api.callbacks.FavoriteCallback;

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

}
