package util;

// import util.WordList;
import java.io.*;
import java.util.*;

public class SetWord {
    public Map<String, String> wordData = new HashMap<String, String>();
    public String key;

    public static void main(String[] args) {
        SetWord sw = new SetWord();
        sw.setUpWordData();
        String str = sw.responseWord("アタ");
        String str2 = sw.responseWord("タタ");
        System.out.println(str);
        System.out.println(str2);
    }

    public void setUpWordData() {
        try {
            FileReader fr = new FileReader("WordPlay/src/resource/chainGameSaveData.txt");
            BufferedReader br = new BufferedReader(fr);

            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                String[] split = tmp.split("=");
                String word = split[0].replace(":", "");
                String read = split[1].replace(":", "");
                wordData.put(word, read);
            }
            br.close();

            // for (Map.Entry<String, String> entry : wordData.entrySet()) {
            // System.out.println(entry);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String responseWord(String input) {
        String response = "";
        input = input.substring(input.length() - 1, input.length());
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(wordData.entrySet());
        Collections.shuffle(list);
        for (Map.Entry<String, String> entry : list) {
            if ((entry.getValue().substring(0, 1)).equals(input)) {
                String str = entry.getValue();
                if (str.substring(str.length() - 1, str.length()).equals("ン")) {
                    continue;
                }
                key = entry.getKey();
                response = entry.getKey() + "(" + entry.getValue() + ")";
            }
        }
        if (response.equals("")) {
            return "false";
        } else {
            wordData.remove(key);
        }
        return response;

    }

    public void saveWordMap(Map<String, String> saveWord) {
        try {
            // セーブデータ書き込み
            FileWriter filewriter = new FileWriter("WordPlay/src/resource/chainGameSaveData.txt", true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(filewriter));

            for (Map.Entry<String, String> entry : saveWord.entrySet()) {
                pw.println(entry);
            }

            File file = new File("WordPlay/src/resource/chainGameSaveData.txt");
            if (!file.exists()) {
                System.out.println("fill can't find");
            }
            pw.close();

            // セーブデータ読み込み後にて、重複単語削除
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String tmp = "";
            List<String> savedate = new ArrayList<String>();
            while ((tmp = br.readLine()) != null) {
                savedate.add(tmp);
            }
            List<String> hashSaveDate = new ArrayList<String>(new HashSet<>(savedate));

            // 整形した単語リストをセーブデータに保存
            FileWriter filewriter2 = new FileWriter("WordPlay/src/resource/chainGameSaveData.txt");
            PrintWriter pw2 = new PrintWriter(new BufferedWriter(filewriter2));

            for (String str : hashSaveDate) {
                pw2.println(str);
            }

            pw2.close();
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
