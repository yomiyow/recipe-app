package recipe_app.api.callbacks;

import recipe_app.model.Meal;

public interface MealCallback {
    void onSuccess(Meal meal);
    void onError(Exception e);
}
