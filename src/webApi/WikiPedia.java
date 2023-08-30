package webApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WikiPedia {
    public JsonNode root;

    public String getWiki(String word) {
        String response = "";

        try {
            URI uri = new URI("https://ja.wikipedia.org/w/api.php?action=query&titles=" +
                    URLEncoder.encode(word, "UTF-8") +
                    "&prop=extracts&exintro&explaintext&redirects=1&format=json");

            // HTTP接続を確立し，処理要求を送る
            URL url = uri.toURL();
            HttpURLConnection conn = ((HttpURLConnection) (url).openConnection());
            conn.setRequestMethod("GET"); // GETメソッド

            // Webサーバからの応答を受け取る
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "Shift-JIS"));
            ObjectMapper mapper = new ObjectMapper();
            String tmp = "";

            while ((tmp = br.readLine()) != null) {
                // gson.toJson(tmp);
                root = mapper.readTree(tmp);
            }
            response = root.toString();

            String markerBegin = "\"extract\":\"";
            int beginIndex = response.indexOf(markerBegin) + markerBegin.length();
            response = response.substring(beginIndex);

            String markerEnd = "\"}}}}";
            int endIndex = response.indexOf(markerEnd);
            response = response.substring(0, endIndex);

            response = response.replace("\\n", ""); // \n

            br.close();
            conn.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args) {
        String word = "りんご";
        WikiPedia wp = new WikiPedia();
        System.out.println(wp.getWiki(word));
    }
}
