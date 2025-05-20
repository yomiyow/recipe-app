package recipe_app.api.callbacks;

import java.util.List;

import recipe_app.model.Meal;

public interface MealCallback {
    void onSuccess(List<Meal> meals);
    void onError(Exception e);

}
