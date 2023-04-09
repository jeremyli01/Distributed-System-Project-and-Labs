/**
 * Backend server for Catboy Application, which is a simple web application that displays a random catboy image.
 * Depending on the type of request, the server will either return a random catboy image or return the dashboard.
 *
 * @author Jeremy Li(AndrewID: zihanli2
 * @version 1.0
 * @since 2021-05-01
 */

package com.example.catboyserver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;

import javax.net.ssl.*;

/**
 * This servlet is used to handle the GET request from the client and return the response. For Android user,
 * note to enter type = "catboy" in the request. For dashboard user, note to enter type = "dashboard" in the request.
 * */
@WebServlet(name = "catboyServlet", urlPatterns = {"/getCatboy"})
public class CatboyServlet extends HttpServlet {
    //URL for the catboy API
    static  String CATBOY_URL = "https://api.catboys.com/img";
    //URL for the MongoDB
    static String MONGODB_URL = "mongodb://zihanli2:zcwmdwXZmH0owcjI@ac-ad98x8x-shard-00-02.t7eztmc.mongodb.net:27017,ac-ad98x8x-shard-00-01.t7eztmc.mongodb.net:27017,ac-ad98x8x-shard-00-00.t7eztmc.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1";
    //MongoDB collection
    static MongoCollection<Document> collection;
    static Gson gson;
    // initialize the MongoDB and Gson
    public void init() {
        gson = new Gson();
        //initialize the MongoDB
        ConnectionString connectionString = new ConnectionString(MONGODB_URL);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test");
        collection = database.getCollection("artist");
    }
    // handle the GET request
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String type = req.getParameter("type");
        // if the request is for dashboard
        if (type.equals("dashboard")) {
            List<Document> documents = collection.find().into(new ArrayList<>());
            //average latency
            long avgLatency = 0;
            long validArtists = 0;
            for (Document doc : documents) {
                avgLatency += doc.getLong("latency");
                validArtists += doc.getString("artist").equals("unknown") ? 0 : 1;
            }
            avgLatency = avgLatency / documents.size();
            // count total number of requests
            long totalRequests = documents.size();
            generateDashboard(documents, resp, avgLatency, validArtists, totalRequests);
            // if the request is for get catboy image
        } else if(type.equals("catboy")) {
            long startTime = System.currentTimeMillis();
            String response = fetch(CATBOY_URL, "TLSV1.3");
            long endTime = System.currentTimeMillis();
            resp.getWriter().write(response);
            //parse the JSON file
            Artist artist = gson.fromJson(response, Artist.class);
            artist.setLatency(endTime - startTime);
            System.out.println(artist);
            //insert the data into MongoDB
            Document doc = new Document("artist", artist.getArtist())
                    .append("url", artist.getUrl())
                    .append("artiest_url", artist.getArtist_url())
                    .append("source_url", artist.getSource_url())
                    .append("error", artist.getError())
                    .append("latency", artist.getLatency())
                    .append("header", req.getHeader("User-Agent"))
                    .append("type", req.getParameter("type"))
                    .append("start_time", startTime)
                    .append("end_time", endTime);
            collection.insertOne(doc);
            resp.setStatus(200);
        }
    }
    /**
     * This method is used to generate the dashboard page for the user.
     * */
    private void generateDashboard(List<Document> documents, HttpServletResponse resp, long avgLatency, long validArtists, long totalRequests) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h1>Dashboard</h1>");
        out.println("<h2>Number of requests: " + totalRequests + "</h2>");
        out.println("<h2>Number of valid artists: " + validArtists + "</h2>");
        out.println("<h2>Average latency: " + avgLatency + "ms</h2>");
        out.println("<h2>Requests</h2>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Artist</th>");
        out.println("<th>URL</th>");
        out.println("<th>Artist URL</th>");
        out.println("<th>Source URL</th>");
        out.println("<th>Latency</th>");
        out.println("<th>Header</th>");
        out.println("<th>Type</th>");
        out.println("<th>Start Time</th>");
        out.println("<th>End Time</th>");
        out.println("</tr>");
        for (Document doc : documents) {
            out.println("<tr>");
            out.println("<td>" + doc.getString("artist") + "</td>");
            out.println("<td>" + doc.getString("url") + "</td>");
            out.println("<td>" + doc.getString("artiest_url") + "</td>");
            out.println("<td>" + doc.getString("source_url") + "</td>");
            out.println("<td>" + doc.getLong("latency") + "</td>");
            out.println("<td>" + doc.getString("header") + "</td>");
            out.println("<td>" + doc.getString("type") + "</td>");
            out.println("<td>" + doc.getLong("start_time") + "</td>");
            out.println("<td>" + doc.getLong("end_time") + "</td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("</body></html>");
    }

    public void destroy() {
        super.destroy();
    }

    /**
     * This method is used to create the trust manager for the SSL connection.
     * */
    private static void createTrustManager(String certType) throws KeyManagementException, NoSuchAlgorithmException {
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

    /**
     * This method is used to fetch the data from the given URL.
     * */
    private static String fetch(String searchURL, String certType) {
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
}