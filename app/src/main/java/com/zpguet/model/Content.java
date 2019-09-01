package com.zpguet.model;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/7/1
 * Time: 21:19
 */
public class Content {
    public int ID = -1;
    private String content;
    private String contenttime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContenttime() {
        return contenttime;
    }

    public void setContenttime(String contenttime) {
        this.contenttime = contenttime;
    }

    @Override
    public String toString() {
//        return "Content{" +
//                "ID=" + ID +
//                ", content='" + content + '\'' +
//                ", contenttime='" + contenttime + '\'' +
//                '}';
        String result = "ID："+this.ID + "\n";
        result += "语录：" + content + "\n";
        result += contenttime;
        return result;
    }
}
