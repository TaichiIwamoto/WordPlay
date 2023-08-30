package webApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

// GooラボAPIのテスト
public class GooLab {

    public static void main(String[] args) {
        GooLab goo = new GooLab();
        // goo.gooProper("岩本と三匹の横浜の千葉と内閣");
        List<String> response = goo.gooMorpho("1つ");
        for (String str : response) {
            System.out.println(str);
        }
    }

    public List<String> gooMorpho(String word) {
        JSONObject json = new JSONObject();
        json.put("app_id", "87f7eabfcde6b8190b81f24e3b0407f98e3e8e13a6dcb835cb486ef25e41394c");
        json.put("sentence", word);

        JsonNode root = null;

        try {
            URI uri = new URI("https://labs.goo.ne.jp/api/morph");
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) (url.openConnection());
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.connect();

            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.print(json);
            ps.close();

            if (con.getResponseCode() != 200) {
                System.out.println("error");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String tmp = "";
            ObjectMapper mapper = new ObjectMapper();
            while ((tmp = br.readLine()) != null) {
                root = mapper.readTree(tmp);
            }
            br.close();
            con.disconnect();
        } catch (IOException ex) {
            System.out.println("happen error");
            ex.printStackTrace();
        } catch (URISyntaxException urex) {
            urex.printStackTrace();
        }

        String jsonString = root.toString();
        jsonString = jsonString.replace("\"", "").replace("[", "").replace("]",
                "").replace("word_list:", "")
                .replace("}", "");
        String[] jsonArray = jsonString.split((","));

        List<String> jsonList = new ArrayList<String>(Arrays.asList(jsonArray));
        jsonList.remove(0);
        int j = 3;
        for (int i = 0; i < jsonList.size() / 3; i++) {
            jsonList.set(i * j, "基本形:" + jsonList.get(i * j));
            jsonList.set(i * j + 1, "品詞:" + jsonList.get(i * j + 1));
            jsonList.set(i * j + 2, "読み:" + jsonList.get(i * j + 2));
        }
        return jsonList;
    }

    public String gooProper(String word) {
        String response = "";

        JSONObject json = new JSONObject();
        json.put("app_id", "87f7eabfcde6b8190b81f24e3b0407f98e3e8e13a6dcb835cb486ef25e41394c");
        json.put("sentence", word);

        JsonNode root = null;

        try {
            URI uri = new URI("https://labs.goo.ne.jp/api/entity");
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) (url.openConnection());
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.connect();

            PrintStream ps = new PrintStream(con.getOutputStream());
            ps.print(json);
            ps.close();

            if (con.getResponseCode() != 200) {
                System.out.println("error");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String tmp = "";
            ObjectMapper mapper = new ObjectMapper();
            while ((tmp = br.readLine()) != null) {
                root = mapper.readTree(tmp);
            }
            br.close();
            con.disconnect();
            response = root.toString();
        } catch (IOException ex) {
            System.out.println("happen error");
            ex.printStackTrace();
        } catch (URISyntaxException urex) {
            urex.printStackTrace();
        }
        return response;
    }
}