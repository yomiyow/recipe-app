package recipe_app.model;

public class Meal {
    private String id;
    private String name;
    private String thumbUrl;

    public Meal(String id, String name, String thumbUrl) {
        this.id = id;
        this.name = name;
        this.thumbUrl = thumbUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }
}
