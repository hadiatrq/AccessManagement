package uni.bager.accessmanagement.common;

public enum ResourcePath {
    PREFERENCES("/uni/bager/accessmanagement/view/preferences.fxml"),
    ABOUT_RULES("/uni/bager/accessmanagement/view/about-rules.fxml"),
    ADD_RULES("/uni/bager/accessmanagement/view/add-rules-update.fxml"),
    SEARCH("/uni/bager/accessmanagement/view/search.fxml"),
    PREFERENCES_ABSOLUTE("preferences"),
    ABOUT_RULES_ABSOLUTE("about-rules"),
    ADD_RULES_ABSOLUTE("add-rules"),
    SEARCH_ABSOLUTE("search");

    private final String path;
    private ResourcePath(String path){
        this.path = path;
    }
    public String getPath(){
        return path;
    }

}
