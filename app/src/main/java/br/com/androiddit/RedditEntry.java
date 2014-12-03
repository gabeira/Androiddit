package br.com.androiddit;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabrielbernardopereira on 12/2/14.
 */
public class RedditEntry {

    String title;
    String author;
    Long creationDate;
    String thumbnail;
    Integer numberOfComments;

    public RedditEntry() {}

    public RedditEntry(String title, String author, Long creationDate, String thumbnail, Integer numberOfComments) {
        this.title = title;
        this.author = author;
        this.creationDate = creationDate;
        this.thumbnail = thumbnail;
        this.numberOfComments = numberOfComments;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public Long getHoursFromCreation(){
        return TimeUnit.HOURS.convert(Calendar.getInstance().getTimeInMillis() - (creationDate * 1000l),TimeUnit.MILLISECONDS);
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

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setNumberOfComments(Integer numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

}
