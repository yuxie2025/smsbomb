package com.yuxie.smsbomb.bean;


import java.io.Serializable;


/**
 * Created by luo on 2018/3/3.
 */
public class SmsApi implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String type;
    private String url;
    private String body;
    private String resultOk;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResultOk() {
        return resultOk;
    }

    public void setResultOk(String resultOk) {
        this.resultOk = resultOk;
    }

    @Override
    public String toString() {
        return "SmsApi{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", body='" + body + '\'' +
                ", resultOk='" + resultOk + '\'' +
                '}';
    }
}
