package com.demo.instaoff;

public class RecentPost{
    String imageURL,caption,time,likeCount,tags,commentCount;
    public RecentPost(String imageURL, String caption,String time,String likeCount, String commentCount){
        this.imageURL = imageURL;
        this.caption = caption;
        this.time = time;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
    public String debug(){
        return "Debugging: "+imageURL+" "+caption+" "+time+" "+likeCount+" "+commentCount;
    }
}