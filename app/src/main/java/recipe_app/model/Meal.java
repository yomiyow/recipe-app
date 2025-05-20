package recipe_app.model;

import java.util.List;

public class Meal {
    private String id;
    private String name;
    private String thumbUrl;
    private String category;
    private String country;
    private List<String> instructionStep;
    private String tags;
    private String youtube;
    private List<String> ingredientList;

    public Meal(String id, String name, String thumbUrl) {
        this.id = id;
        this.name = name;
        this.thumbUrl = thumbUrl;
    }

    public Meal(
        String id, String name, String thumbUrl,
        String category, String country, List<String> instructionStep,
        String tags, String youtube, List<String> ingredientList
    ) {
        this.id = id;
        this.name = name;
        this.thumbUrl = thumbUrl;
        this.category = category;
        this.country = country;
        this.instructionStep = instructionStep;
        this.tags = tags;
        this.youtube = youtube;
        this.ingredientList = ingredientList;
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
