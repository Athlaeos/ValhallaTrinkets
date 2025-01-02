package me.athlaeos.valhallatrinkets;

public class TrinketType {

    private final int id;
    private final String loreTag;

    public TrinketType(int id, String displayName){
        this.id = id;
        this.loreTag = displayName;
    }

    public int getID() {
        return id;
    }

    public String getLoreTag() {
        return loreTag;
    }
}
