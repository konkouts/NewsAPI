package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.MissingCountryException;
import exception.NewsAPIException;
import model.NewsInfo;
import model.thenewsdb.Article;
import model.thenewsdb.ErrorResponse;
import model.thenewsdb.NewsResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NewsAPIService {
    private final String API_KEY;
    private final String API_URL;

    public NewsAPIService(String aPI_URL, String aPI_KEY) {
        API_URL = aPI_URL;
        API_KEY = aPI_KEY;
    }

    //method to create base url regarding the top headlines request and perform the request
    private NewsResult getAPIDataTop(String country, String category, String API_URL, String API_KEY ) throws MissingCountryException, NewsAPIException {
        try {
            final URIBuilder uriBuilder = new URIBuilder(API_URL).setPathSegments("v2", "top-headlines")
                    .addParameter("apiKey", API_KEY).addParameter("country", country);

            if (category != null && !category.isBlank()) {
                uriBuilder.addParameter("category", category);
            }
            System.out.println(uriBuilder);
            final URI uri  = uriBuilder.build();
            final HttpGet getRequest = new HttpGet(uri);
            final CloseableHttpClient httpclient = HttpClients.createDefault();
            //perform request
            try (CloseableHttpResponse response = httpclient.execute(getRequest)) {
                //Check if country input is valid and throw exception with message if not
                if (country.length() != 2 && country.length() != 0 && !country.isEmpty()) {
                    throw new MissingCountryException("Λανθασμένη εισαγωγή χώρας. Παρακαλώ συμπληρώστε τον κωδικό της χώρας (gr)");
                }
                // Message to fill the country field
                else if (country.isEmpty() || country.isBlank() || country == null){
                    throw new MissingCountryException("Παρακαλώ συμπληρώστε χώρα");
                }
                final HttpEntity entity = response.getEntity();
                final ObjectMapper mapper = new ObjectMapper();

                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    ErrorResponse errorResponse = mapper.readValue(entity.getContent(), ErrorResponse.class);
                    if (errorResponse.getMessage() != null)
                        throw new NewsAPIException("Error occurred on API call: " + errorResponse.getMessage());
                }
                return mapper.readValue(entity.getContent(), NewsResult.class);
            } catch (IOException e) {
                throw new NewsAPIException("Error requesting data from the TheNewsDB API.", e);
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            throw new NewsAPIException("Unable to create request URI.", e);
        }
    }

    //method to create base url regarding the "everything"" request and perform the request
    private NewsResult getAPIDataNews(String query, String source, String from, String to, String language, String API_URL, String API_KEY ) throws NewsAPIException {
        //add parameters
        try {
            final URIBuilder uriBuilder = new URIBuilder(API_URL).setPathSegments("v2", "everything")
                    .addParameter("apiKey", API_KEY).addParameter("q", query);

            if (source != null && !source.isBlank()) {
                uriBuilder.addParameter("sources", source);
            }
            if (from != null && !from.isBlank()) {
                uriBuilder.addParameter("from", from);
            }
            if (to != null && !to.isBlank()){
                uriBuilder.addParameter("to", to);
            }
            if (language != null && !language.isBlank()) {
                uriBuilder.addParameter("language", language);
            }
            System.out.println(uriBuilder);

            final URI uri  = uriBuilder.build();
            final HttpGet getRequest = new HttpGet(uri);
            final CloseableHttpClient httpclient = HttpClients.createDefault();
            try (CloseableHttpResponse response = httpclient.execute(getRequest)) {
                final HttpEntity entity = response.getEntity();
                final ObjectMapper mapper = new ObjectMapper();
                //Throw exception is case user has not provider a query
                if (query.isBlank() || query.isEmpty()) {
                    throw new NewsAPIException("Παρακαλώ συμπληρώστε την αναζήτηση για να προχωρήσετε");
                }

                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    ErrorResponse errorResponse = mapper.readValue(entity.getContent(), ErrorResponse.class);
                    if (errorResponse.getMessage() != null)
                        throw new NewsAPIException("Error occurred on API call: " + errorResponse.getMessage());
                }
                return mapper.readValue(entity.getContent(), NewsResult.class);
            } catch (IOException e) {
                throw new NewsAPIException("Error requesting data from the TheNewsDB API.", e);
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            throw new NewsAPIException("Unable to create request URI.", e);
        }
    }

    //get popular news
    public List<NewsInfo> getPopularNews(String country, String category) throws MissingCountryException, NewsAPIException {
        NewsResult result = getAPIDataTop(country, category, API_URL, API_KEY);
        List<NewsInfo> newsList = new ArrayList<>(result.getArticles().size());
        System.out.println(result.getArticles().size());
        for (Article theArticle : result.getArticles()) {
            newsList.add(new NewsInfo(theArticle));
        }
        return newsList;
    }

    //get headlines with different parameters
    public List<NewsInfo> newsByParameters(String query, String source, String from, String to, String language) throws NewsAPIException {
        NewsResult result = getAPIDataNews(query, source, from, to, language, API_URL, API_KEY);
        List<NewsInfo> newsList = new ArrayList<>(result.getArticles().size());
        System.out.println(result.getArticles().size());
        for (Article theArticle : result.getArticles()) {
            newsList.add(new NewsInfo(theArticle));
        }
        return newsList;
    }
}
