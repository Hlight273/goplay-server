package com.github.goplay.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "神中神物品实体类")
public class TestItem {
    private String itemname;
    private String itemdesc;

    public TestItem(String itemname, String itemdesc) {
        this.itemname = itemname;
        this.itemdesc = itemdesc;
    }

    public TestItem() {
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemdesc() {
        return itemdesc;
    }

    public void setItemdesc(String itemdesc) {
        this.itemdesc = itemdesc;
    }

    @Override
    public String toString() {
        return "TestItem{" +
                "itemname='" + itemname + '\'' +
                ", itemdesc='" + itemdesc + '\'' +
                '}';
    }
}
