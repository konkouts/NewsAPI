import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import exception.MissingCountryException;
import exception.NewsAPIException;
import model.NewsAPI;
import model.NewsInfo;
import org.junit.Assert;
import org.junit.Test;
import services.NewsAPIService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAPITest {

    // unit test for retrieving the ip of the user and then finding his/ her country's code
    @Test
    public void ipFinder() throws IOException, GeoIp2Exception {
            try
            {
                URL ip = new URL("https://checkip.amazonaws.com");
                BufferedReader br = new BufferedReader(new InputStreamReader(ip.openStream()));
                String pip = br.readLine();
                System.out.println("Public/External IP Address = " +pip);

                File database = new File("./src/main/resources/GeoLite2-City.mmdb");

                // This reader object should be reused across lookups as creation of it is
                // expensive.
                DatabaseReader reader = new DatabaseReader.Builder(database).build();

                // If you want to use caching at the cost of a small (~2MB) memory overhead:
                // new DatabaseReader.Builder(file).withCache(new CHMCache()).build();

                InetAddress ipAddress = InetAddress.getByName(pip);

                CityResponse response = reader.city(ipAddress);

                Country country = response.getCountry();
                System.out.println(country.getIsoCode().toLowerCase());
            }
            catch(Exception e)
            {
                System.out.println("Exception: " +e);
            }
    }

    // unit test for formatting the user's date input to the proper format used is newsapi
    @Test
    public void formatDate() throws ParseException {
//        String date = "03-11-2008";
        String date = "2022/02/03";
        //check what is the format of the input
        if (date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
            Date dateValue = input.parse(date);
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("" + output.format(dateValue) + " real date " + date);
        }
        else if (date.matches("\\d{2}-\\d{2}-\\d{4}")){
            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
            Date dateValue = input.parse(date);
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("" + output.format(dateValue) + " real date " + date);
        }
        else if (date.matches("\\d{4}/\\d{2}/\\d{2}")){
            SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd");
            Date dateValue = input.parse(date);
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("" + output.format(dateValue) + " real date " + date);
        }

    }


    // unit test for retrieving the top headlines with country and category parameters
    @Test
    public void testPopularAPI() throws NewsAPIException, MissingCountryException {
        final NewsAPIService newsSearchService = NewsAPI.getNewsAPIService();
        final List<NewsInfo> results = newsSearchService.getPopularNews("gr", "");
        Assert.assertFalse(results.isEmpty());
        results.forEach(System.out::println);
    }

    //unit test for retrieving all the news according to the provided parameters
    @Test
    public void testfindbyParam() throws NewsAPIException {
        final NewsAPIService newsSearchService = NewsAPI.getNewsAPIService();

        String source = "bbc news";
        source = source.replace(' ' , '-').toLowerCase();
        System.out.println(source);

        final List<NewsInfo> results = newsSearchService.newsByParameters("covid", source, "2022-02-19", null, null);
        Assert.assertFalse(results.isEmpty());
        results.forEach(System.out::println);
    }
}
