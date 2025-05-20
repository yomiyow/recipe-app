package recipe_app.api.callbacks;

import java.util.List;

public interface CategoryCallback {
    void onSuccess(List<String> categories);
    void onError(Exception e);
}
