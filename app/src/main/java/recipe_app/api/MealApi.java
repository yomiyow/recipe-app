package recipe_app.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import recipe_app.api.callbacks.CategoryCallback;
import recipe_app.api.callbacks.MealCallback;
import recipe_app.api.callbacks.MealsCallback;
import recipe_app.model.Meal;

public class MealApi {
    private Context context;
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    public MealApi(Context context) {
        this.context = context;
    }

    // Fetch all meal categories
    public void fetchCategories(CategoryCallback cb) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BASE_URL + "list.php?c=list";

        JsonObjectRequest req = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject res) {
                    List<String> categoryList = new ArrayList<>();

                    try {
                        JSONArray meals = res.getJSONArray("meals");

                        for (int i = 0; i < meals.length(); i++) {
                            JSONObject obj = meals.getJSONObject(i);
                            String category = obj.getString("strCategory");
                            categoryList.add(category);
                        }

                        cb.onSuccess(categoryList);

                    } catch (Exception e) {
                        cb.onError(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    cb.onError(e);
                }
            });

        queue.add(req);
    }

    // Fetch meals based on the passed category parameter
    public void fetchMealsByCategory(String category, MealsCallback cb) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BASE_URL + "filter.php?c=" + category;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        List<Meal> mealList = new ArrayList<>();

                        try {
                            JSONArray meals = res.getJSONArray("meals");

                            for (int i = 0; i < meals.length(); i++) {
                                JSONObject obj = meals.getJSONObject(i);
                                String id = obj.getString("idMeal");
                                String name = obj.getString("strMeal");
                                String thumb = obj.getString("strMealThumb");

                                mealList.add(new Meal(id, name, thumb));
                            }

                            cb.onSuccess(mealList);

                        } catch (Exception e) {
                            cb.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cb.onError(error);
                    }
                }
        );
        queue.add(req);
    }

    // Fetch single meal base on mealId
    public void fetchMeal(String mealId, MealCallback cb) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = BASE_URL + "lookup.php?i=" + mealId;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject res) {
                        try {
                            JSONObject meal = res.getJSONArray("meals").getJSONObject(0);
                            String id = meal.getString("idMeal");
                            String name = meal.getString("strMeal");
                            String category = meal.optString("strCategory", "");
                            String country = meal.getString("strArea");
                            String thumbUrl = meal.getString("strMealThumb");
                            String tags = meal.optString("strTags", "");
                            String youtube = meal.optString("strYoutube", "");

                            String instructions = meal.getString("strInstructions");
                            List<String> instructionSteps = new ArrayList<>();
                            if (instructions != null && !instructions.isEmpty()) {
                                for (String step : instructions.split("\\.")) {
                                    if (!step.trim().isEmpty()) {
                                        instructionSteps.add(step.trim());
                                    }
                                }
                            }

                            List<String> ingredientList = new ArrayList<>();
                            for (int i = 1; i <= 20; i++) {
                                String ingredient = meal.optString("strIngredient" + i, "");
                                String measure = meal.optString("strMeasure" + i, "");

                                if (!ingredient.isEmpty() && !ingredient.equals("null")) {
                                    String item = (!measure.isEmpty() && !measure.equals("null"))
                                            ? ingredient + " (" + measure.trim() + ")"
                                            : ingredient;

                                    ingredientList.add(item);
                                }
                            }

                            cb.onSuccess(new Meal(
                                id, name, thumbUrl,
                                category, country,
                                instructionSteps,
                                tags, youtube, ingredientList
                            ));

                        } catch (Exception e) {
                            cb.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cb.onError(error);
                    }
                }
        );
        queue.add(req);
    }
}
