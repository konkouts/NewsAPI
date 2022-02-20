package model;

import services.NewsAPIService;

public class NewsAPI {
    public static NewsAPIService getNewsAPIService() {
//        return new NewsAPIService("https://newsapi.org/", "e243c59761ba4389af6ec9347f6a2309");
        return new NewsAPIService("https://newsapi.org/", "3f76b2304358416ea160b0b95b138014");
    }
}
