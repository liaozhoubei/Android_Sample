package com.example.example.retrofit;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name = "info",strict = false)
public class NewsXml {
    @Attribute
    public String id;
    @Attribute
    public String cName;
    @Text
    public String info;
    public String getcName() {
        return cName;
    }
    public void setcName(String cName) {
        this.cName = cName;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "NewsXml{" +
                "id='" + id + '\'' +
                ", cName='" + cName + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}