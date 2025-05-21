package recipe_app.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Meal implements Parcelable {
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

    protected Meal(Parcel in) {
        id = in.readString();
        name = in.readString();
        thumbUrl = in.readString();
        category = in.readString();
        country = in.readString();
        instructionStep = in.createStringArrayList();
        tags = in.readString();
        youtube = in.readString();
        ingredientList = in.createStringArrayList();
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbUrl);
        dest.writeString(category);
        dest.writeString(country);
        dest.writeStringList(instructionStep);
        dest.writeString(tags);
        dest.writeString(youtube);
        dest.writeStringList(ingredientList);
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

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public List<String> getInstructionStep() {
        return instructionStep;
    }

    public String getTags() {
        return tags;
    }

    public String getYoutube() {
        return youtube;
    }

    public List<String> getIngredientList() {
        return ingredientList;
    }

}
