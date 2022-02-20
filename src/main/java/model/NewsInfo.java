package model;

import model.thenewsdb.Article;

public class NewsInfo {

    private final String title;
    private final Object author;
    private final String description;
    private final String url;
    private final String publishedAt;

    public NewsInfo(String title, Object author, String description, String url, String publishedAt) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public NewsInfo(Article theArticle) {
        this.title = theArticle.getTitle();
        this.author = theArticle.getAuthor();
        this.description = theArticle.getDescription();
        this.url = theArticle.getUrl();
        this.publishedAt = theArticle.getPublishedAt();
    }

    public String getTitle() {
        return title;
    }

    public Object getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    @Override
    public String toString() {
        return "NewsInfo{" +
                "title='" + title + '\'' +
                ", author=" + author +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                '}';
    }
}
