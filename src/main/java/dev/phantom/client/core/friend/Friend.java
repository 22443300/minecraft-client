package dev.phantom.client.core.friend;

import com.google.gson.JsonObject;

public class Friend {

    private String name;
    private String uuid;
    private FriendRelation relation;
    private String alias;

    public Friend(String name, String uuid, FriendRelation relation, String alias) {
        this.name = name;
        this.uuid = uuid;
        this.relation = relation;
        this.alias = alias;
    }

    public Friend(String name, FriendRelation relation) {
        this(name, "", relation, "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public FriendRelation getRelation() {
        return relation;
    }

    public void setRelation(FriendRelation relation) {
        this.relation = relation;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("uuid", uuid);
        obj.addProperty("relation", relation.name());
        obj.addProperty("alias", alias);
        return obj;
    }

    public static Friend deserialize(JsonObject obj) {
        String name = obj.has("name") ? obj.get("name").getAsString() : "";
        String uuid = obj.has("uuid") ? obj.get("uuid").getAsString() : "";
        FriendRelation relation = FriendRelation.FRIEND;
        if (obj.has("relation")) {
            try {
                relation = FriendRelation.valueOf(obj.get("relation").getAsString());
            } catch (IllegalArgumentException ignored) {}
        }
        String alias = obj.has("alias") ? obj.get("alias").getAsString() : "";
        return new Friend(name, uuid, relation, alias);
    }
}
