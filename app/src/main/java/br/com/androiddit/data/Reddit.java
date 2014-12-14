package br.com.androiddit.data;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabrielbernardopereira on 12/2/14.
 */
public class Reddit {

    Integer id;
    String title;
    String author;
    Long created;
    String thumbnail;
    String url;
    Integer numberOfComments;

    public Reddit() {}

    public Reddit(String title, String author, Long creationDate, String thumbnail, Integer numberOfComments) {
        this.title = title;
        this.author = author;
        this.created = creationDate;
        this.thumbnail = thumbnail;
        this.numberOfComments = numberOfComments;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Long getCreated() {
        return created;
    }

    public Long getHoursFromCreation(){
        return TimeUnit.HOURS.convert(Calendar.getInstance().getTimeInMillis() - (created * 1000l),TimeUnit.MILLISECONDS);
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setNumberOfComments(Integer numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
