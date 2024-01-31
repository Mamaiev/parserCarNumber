package ua.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ua.model.CarNumber;
import ua.model.Region;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ParserSiteService {

    private Log log = LogFactory.getLog(ParserSiteService.class);

    public List<CarNumber> pullNumbers() throws IOException {
        Connection.Response site = parseSite();
        log.info(site.statusMessage() + site.statusCode()); //TODO if get timeout or 504 error I guess we next code doesn't work. Will be generate exception
        if (site.statusCode() == 504) {
            log.error("Error on server side. Status code 504. " + site.statusMessage());
            return Collections.emptyList();
        }
        return convertResponse(site);
    }

    private Connection.Response parseSite() throws IOException {

        Connection.Response response = Jsoup.connect("https://opendata.hsc.gov.ua/check-leisure-license-plates/")
                .validateTLSCertificates(false)
                .ignoreContentType(true)
//                    .data(data())
                .data("region", String.valueOf(Region.KyC.getCode()))
                .data("type_venichle", "light_car_and_truck")
                .data("tsc", "Весь регіон")
                .data("number", "")
                .timeout(0)
                .method(Connection.Method.POST)
                .execute();

        return response;
    }

    private List<CarNumber> convertResponse(Connection.Response response) throws IOException {
        Document doc = response.parse();
        List<CarNumber> carNumbers = new ArrayList<>();

        try {
            Elements temp;
            for (Element element : doc.select("tbody").eq(1).select("tr")) {
                temp = element.select("td");
                CarNumber number = new CarNumber();
                try {
                    number.setNumber(temp.get(0).text());
                    number.setPrice(Integer.valueOf(temp.get(1).text()));
                    number.setServiceCenter(temp.get(2).text());
                    carNumbers.add(number);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Number " + temp.text() + " doesn't have data.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Pulled " + carNumbers.size() + " numbers.");
        return carNumbers;
    }

    private void checkStatusCode(Connection.Response response) {
        response.statusCode();
    }
    //only for test
    private List<CarNumber> parseSiteTest(String str) {
        Document doc = null;
        try {
            doc = Jsoup.parse(new File("/Users/mac/IdeaProjects/parserCarNumber/src/main/resources/htmlGOV.rtf"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<CarNumber> carNumbers = new ArrayList<CarNumber>();
        Elements temp;
        for (Element element : doc.select("tbody").eq(1).select("tr")) {
            temp = element.select("td");
            CarNumber number = new CarNumber();
            try {
                number.setNumber(temp.get(0).text());
                number.setPrice(Integer.valueOf(temp.get(1).text()));
                number.setServiceCenter(temp.get(2).text());
                carNumbers.add(number);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Number " + temp.text() + " doesn't have data.");
            }
        }
        for (CarNumber car : carNumbers) {
            System.out.println(car);

        }
        return carNumbers;
    }

    private static Map<String, String> data() {
        Map res = new HashMap();
        res.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        res.put("Accept-Encoding", "gzip, deflate, br");
        res.put("Accept-Language", "en-US,en;q=0.9,uk;q=0.8");
        res.put("Cache-Control", "max-age=0");
        res.put("Connection", "keep-alive");
        res.put("Content-Length", "202");
        res.put("Content-Type", "application/x-www-form-urlencoded");
        res.put("Host", "opendata.hsc.gov.ua");
        res.put("Origin", "https://opendata.hsc.gov.ua");
        res.put("Referer", "https://opendata.hsc.gov.ua/check-leisure-license-plates/");
        res.put("Sec-Ch-Ua", "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"");
        res.put("Sec-Ch-Ua-Mobile", "?0");
        res.put("Sec-Ch-Ua-Platform", "\"macOS\"");
        res.put("Sec-Fetch-Dest", "document");
        res.put("Sec-Fetch-Mode", "navigate");
        res.put("Sec-Fetch-Site", "same-origin");
        res.put("Sec-Fetch-User", "?1");
        res.put("Upgrade-Insecure-Requests", "1");
        res.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36");

        return res;
    }
}
