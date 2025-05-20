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

public class MealApi {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    public interface CategoryCallback {
        void onSuccess(List<String> categories);
        void onError(Exception e);
    }

    // Fetch list of all categories
    public void fetchCategories(Context context, CategoryCallback cb) {
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
}
