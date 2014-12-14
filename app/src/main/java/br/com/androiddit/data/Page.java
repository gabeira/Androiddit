package br.com.androiddit.data;

import java.util.List;

/**
 * Created by gabrielbernardopereira on 12/9/14.
 */
public class Page {

    public List<Reddit> reddits;
    public String after;

    public List<Reddit> getReddits() {
        return reddits;
    }

    public void setReddits(List<Reddit> _reddits) {
        this.reddits = _reddits;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
