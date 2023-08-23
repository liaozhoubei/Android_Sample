package com.example.example.retrofit;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "data",strict = false)
public class NewsDataXml {
    @ElementList(required = true,inline = true,entry = "info")

    public List<NewsXml> list;

    public List<NewsXml> getList() {
        return list;
    }

    public void setList(List<NewsXml> list) {
        this.list = list;
    }
}
