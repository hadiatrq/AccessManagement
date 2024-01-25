package uni.bager.accessmanagement.utils;

import java.util.HashMap;
import java.util.Map;

public class PageManager {
    private final static PageManager PAGE_MANAGER = new PageManager();
    private final Map<String,Boolean> openPages = new HashMap<>();
    private PageManager(){}

    public static PageManager getInstance() {
        return PAGE_MANAGER;
    }

    public boolean isPageOpen(String pageKey){
        return openPages.getOrDefault(pageKey,false);
    }

    public void setPageOpen(String pageKey, boolean isOpen){
        openPages.put(pageKey,isOpen);
    }

}
