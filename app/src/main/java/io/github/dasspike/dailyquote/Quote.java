package io.github.dasspike.dailyquote;

import java.util.Date;

/**
 * Data structure representing a Quote.
 */
class Quote {
    public final String text;
    public final String author;
    public final Date date;
    public final String backgroundUrl;

    /**
     * Creates a new quote object with the given text, author, date and URL to the background image.
     *
     * @param text          The quote text.
     * @param author        The quote author.
     * @param date          The quote date.
     * @param backgroundUrl The URL to the background image.
     */
    Quote(String text, String author, Date date, String backgroundUrl) {
        this.text = text;
        this.author = author;
        this.date = date;
        this.backgroundUrl = backgroundUrl;
    }
}
