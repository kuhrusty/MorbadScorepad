package com.kuhrusty.morbadscorepad.model;

import java.util.List;

/**
 */
public class Mission implements Expandable {
    private String expansionID;
    String name;
    int pageNumber;
    List<String> prevIDs;
    List<String> nextIDs;

    public String getName() {
        return name;
    }

    @Override
    public String getExpansionID() {
        return expansionID;
    }
    @Override
    public void setExpansionID(String id) {
        expansionID = id;
    }
}
