package com.junnar.tourist_guide.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String name;
    private String description;
    private String date;
    private String month;
    private String tag;       // Cultural, Festival, Adventure, Nature, Trekking
    private String icon;

    public Event() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}