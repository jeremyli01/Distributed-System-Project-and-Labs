package ds.project1task2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;

@WebServlet(name = "helloServlet", urlPatterns = {"/hello-servlet"})
public class HelloServlet extends HttpServlet {
    /** Initialize the state of country and emoji lists */
    public void init() {
//        initEmojis();
        initEmojis2();
        initCountries();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        String country = request.getParameter("countries");
        request.setAttribute("country", country); //retrieve country and set to the result page
        String nickname = getNickName(country);
        request.setAttribute("nickname", nickname); //retrieve nickname and set to the result page
        String capital = getCapital(country);
        request.setAttribute("capital", capital); //retrieve capital and set to the result page
        String[] topscorer = getTopScorer(country);
        if (topscorer != null) {
            request.setAttribute("topscorer", topscorer[0]);
            request.setAttribute("topscore", topscorer[1]);
        } else {
            request.setAttribute("topscorer", "NA");
            request.setAttribute("topscore", "NA");
        } //retrieve top scorer and his/her goals to the result page
        String flag = getFlag(country);
        request.setAttribute("flag", flag); //retrieve flag picture and set to the result page
        String emoji = getEmoji(country);
        request.setAttribute("emoji", emoji);
        RequestDispatcher view = request.getRequestDispatcher("result.jsp"); //retrieve flag emoji and set to the result page
        view.forward(request, response);
    }

    public void destroy() {

    }
    /** Uses indexOf method to trace the leftmost and rightmost index of the targeted nickname string
     * from the HTML content based on its location */
    private String getNickName(String country) {
        String url = "https://www.topendsports.com/sport/soccer/team-nicknames-women.htm";
        String response = fetch(url, "TLSV1.3");
        int cutLeft = response.indexOf(country);
        if (cutLeft == -1) {
            return "Not found";
        }
        cutLeft = response.indexOf("<td>", cutLeft);
        String s = "<td>"; // where nickname locates, the td tag
        cutLeft += s.length();
        int cutRight = response.indexOf("</td>", cutLeft);
        String nickname = response.substring(cutLeft, cutRight);
        return nickname;
    }

    /** Uses Gson to parse the JSON file and find the capital of the specific country, also take care of
     * outlier like England. */
    private String getCapital(String country) {
        String countryForCapital = country;
        String url = "https://restcountries.com/v3.1/name/";
        if ("england".equalsIgnoreCase(countryForCapital)) {
            countryForCapital = "united kingdom";
        }
        url += countryForCapital.replace(" ", "%20");
        String response = fetch(url, "TLSV1.3");
        JsonArray convertedArray = new Gson().fromJson(response, JsonArray.class);
        for (int i =0; i < convertedArray.size(); i++) {
            JsonObject jsonObject = convertedArray.get(i).getAsJsonObject();
            if (jsonObject.get("name").getAsJsonObject().get("common").getAsString().equalsIgnoreCase(countryForCapital)) {
                JsonArray capitalArray = jsonObject.get("capital").getAsJsonArray();
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < capitalArray.size(); j++) {
                    sb.append(capitalArray.get(j).getAsString());
                    if (j < capitalArray.size() - 1) {
                        sb.append(", ");
                    }
                }
                return sb.toString();
            }
        }
        return "Not found";
    }
    /** Parses the top scorer and his/her scores with Jsoup */
    private String[] getTopScorer(String country) {
        String url = "https://www.espn.com/soccer/stats/_/league/FIFA.WWC/season/2019/view/scoring";
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            Elements topScorerElements = doc.getElementsByAttributeValue("class", "ResponsiveTable top-score-table").get(0).getElementsContainingOwnText(country);
            if (topScorerElements.size() == 0) {
                return null;
            } else {
                Element topScorerElement = topScorerElements.get(0).parent().parent().parent();
                return new String[] {topScorerElement.getElementsByAttribute("data-player-uid").get(0).text(), topScorerElement.getElementsByTag("span").last().text()};
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Parses the country flag image url with Jsoup, and take care of corner case like England */
    private String getFlag(String country) {
        String countryForFlag = country;
        if ("england".equalsIgnoreCase(countryForFlag)) {
            countryForFlag = "united kingdom";
        }
        String url = "https://www.cia.gov/the-world-factbook/countries/" + countryForFlag.toLowerCase().replace(" ", "-") + "/flag";
        Document document;
        try {
            document = Jsoup.connect(url).get();
            Elements flagElements= document.getElementsByTag("picture");
            String flagURL =  flagElements.get(0).getElementsByTag("img").get(0).attr("src");
            return "https://www.cia.gov" + flagURL;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /** hashMap which maps all countries to their emojis*/
    static Map<String, String> emojiMap = new HashMap<>();

    /** haseSet which stores all countries in the menu*/
    static Set<String> countries = new HashSet<>();

    static CountryEmoji[] emojis;

    /** Initiates the countries with the given country list text file*/
    void initCountries() {
        File file;
        try {
            file = new File("/Users/jeremyli/Documents/95-702 Distributed System/Project1_zihanli2/Project1Task2/src/main/webapp/countries.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                countries.add(scanner.nextLine());
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Initiates the emojis by matching countries to the JSON file and parse into the key-value pair, a faster approach*/
    void initEmojis() {
        String url = "https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/index.json";
        String response = fetch(url, "TLSV1.3");
        initCountries();
        JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            if (countries.contains(jsonObject.get("name").getAsString())) {
                emojiMap.put(jsonObject.get("name").getAsString(), jsonObject.get("emoji").getAsString());
            }
        }
    }
    /** Initiates the emojis by putting in the array of CountryEmoji class*/
    void initEmojis2() {
        String url = "https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/index.json";
        String response = fetch(url, "TLSV1.3");
        initCountries();
        emojis = new CountryEmoji[countries.size()];
        int count = 0;
        JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            if (countries.contains(jsonObject.get("name").getAsString())) {
                CountryEmoji countryEmoji = new CountryEmoji(jsonObject.get("name").getAsString(), jsonObject.get("emoji").getAsString());
                emojis[count++] = countryEmoji;
            }
        }
    }


    /** Gets the emoji for assigned country in O(N) time to meet submission requirement*/
    private String getEmoji(String country) {
//        return emojiMap.containsKey(country) ? emojiMap.get(country) : "NA";
        for (CountryEmoji countryEmoji : emojis) {
            if (countryEmoji.getCountry().equalsIgnoreCase(country)) {
                return countryEmoji.getEmoji();
            }
        }
        return "NA";
    }

    private String fetch(String searchURL, String certType) {
        try {
            // Create trust manager, which lets you ignore SSLHandshakeExceptions
            createTrustManager(certType);
        } catch (KeyManagementException ex) {
            System.out.println("Shouldn't come here: ");
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Shouldn't come here: ");
            ex.printStackTrace();
        }

        String response = "";
        try {
            URL url = new URL(searchURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Something wrong with URL");
            return null;
        }
        return response;
    }

    private void createTrustManager(String certType) throws KeyManagementException, NoSuchAlgorithmException {
        /**
         * Annoying SSLHandShakeException. After trying several methods, finally this
         * seemed to work.
         * Taken from: http://www.nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/
         */
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance(certType);
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}