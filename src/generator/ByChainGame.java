package generator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import system.ResponseCandidate;
import system.StateManager;
import webApi.*;
import util.SetWord;

//しりとりにより返答を生成
public class ByChainGame {
    private WikiPedia wp = new WikiPedia();
    private GooLab gl = new GooLab();
    private boolean correctWord = true;

    /**
     * 
     * @param input         :ユーザが入力した文
     * @param candidateList :返答リスト
     * @param sm            :ステータスマネージャー
     * @param sw            :しりとり辞書入出力
     */
    public void ChainGame(String input, List<ResponseCandidate> candidateList,
            StateManager sm, SetWord sw) {

        List<String> gooMorphoResponse = gl.gooMorpho(input); // ユーザが入力した文を形態素解析する
        String gooProperResponse = gl.gooProper(input); // ユーザが入力した文を固有名詞抽出解析する
        String wikiResponse = wp.getWiki(input); // ユーザが入力した文を検索する
        ResponseCandidate cdd = new ResponseCandidate(); // 一時返答格納用のReponseCandidateインスタンスを生成
        String inputRead = gooMorphoResponse.get(2).replace("読み:", ""); // ユーザが入力した文の"読み"のみをgooMorphoResponseから抽出
        String inputEnd = inputRead.substring(inputRead.length() - 1, inputRead.length()); // inputReadの末尾
        String inputStart = inputRead.substring(0, 1); // inputReadの先頭

        // ユーザが入力した単語が存在しない場合終了
        if (wikiResponse.contains("missing") && correctWord == true) {
            cdd.response = "「" + input + "」" + "という言葉は存在しません！あなたの負けです！";
            correctWord = false;
        }
        // ユーザが入力した単語がシステムが返答した文字の最後になってない場合終了
        if (!(sm.previousRead.equals("")) && correctWord == true) {
            if (!(inputStart.equals(sm.previousRead))) {
                cdd.response = "あなたは\"" + sm.previousRead + "\"から始まる言葉を答えるんですよ。負けです！";
                correctWord = false;
            }
        }

        // ユーザが入力した単語が、独立詞又は単語で無かった場合終了
        if (correctWord == true) {
            for (String str : gooMorphoResponse) {
                if ((str.equals("品詞:独立詞")) || str.contains("Kana")) {
                    cdd.response = "\"" + input + "\"" + "は単語ではありません！あなたの負けです！";
                    correctWord = false;
                }
            }
        }
        // ユーザが入力した単語が、"ん"で終わっている場合終了
        if (inputEnd.equals("ン") && correctWord == true) {
            cdd.response = "\"ん\"が付きましたね！あなたの負けです！";
            correctWord = false;
        }
        // ユーザが入力した単語が既に使われていた場合
        if (correctWord == true) {
            for (String str : sm.wordCache) {
                if (str.contains(input)) {
                    cdd.response = "その単語は既に使われてますよ！あなたの負けです！";
                    correctWord = false;
                }
            }
        }

        // ユーザが失敗した場合の処理
        if (correctWord == false) {
            cdd.score = 15.0;
            candidateList.add(cdd);
            sm.playNum = 0;
            correctWord = true;
            sm.previousRead = "";
            sw.setUpWordData();
            sm.wordCache.clear();
            sm.wordCount = 0;
            return;
        }
        // ユーザが入力した単語が、固有名詞であった場合続行
        if (gooProperResponse.contains("PSN") || gooProperResponse.contains("LOC")) {
            cdd.response = "すいません。ルール上、固有名詞(人名、地域)は認められないんですよ... しりとりは続行できます";
            cdd.score = 15.0;
            candidateList.add(cdd);
            return;
        }

        Map<String, String> inputWord = new HashMap<>();
        String sendInputRead = ":" + inputRead;
        String sendInputWord = ":" + input;
        String inputEndRmBar = "";

        // ユーザが入力した文の末尾が"ー","ャ","ュ","ョ"だった場合の処理
        if (inputEnd.equals("ー")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1);
            inputEndRmBar = sendInputRead.substring(sendInputRead.length() - 1, sendInputRead.length());
        }
        if (inputEnd.equals("ャ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ヤ";
            // System.out.println(sendInputRead);
        } else if (inputEnd.equals("ュ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ユ";
            // System.out.println(sendInputRead);
        } else if (inputEnd.equals("ョ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ヨ";
            // System.out.println(sendInputRead);
        }

        if (inputEndRmBar.equals("ャ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ヤ";
            // System.out.println(sendInputRead);
        } else if (inputEndRmBar.equals("ュ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ユ";
            // System.out.println(sendInputRead);
        } else if (inputEndRmBar.equals("ョ")) {
            sendInputRead = sendInputRead.substring(0, sendInputRead.length() - 1) + "ヨ";
            // System.out.println(sendInputRead);
        }
        String nextword = sw.responseWord(sendInputRead);// gooMorphoResponse.get(2)).replace("読み:", "")
        inputWord.put(sendInputWord, sendInputRead); // ユーザが入力した文をマップに追加
        sw.saveWordMap(inputWord);
        sm.wordCache.add(input);

        // システムが既に使用した単語を使用した場合
        for (String str : sm.wordCache) {
            if (str.equals(sw.key)) {
                cdd.response = sw.key + "は既に使われていました。私の負けです...";
                cdd.score = 15.0;
                candidateList.add(cdd);
                sm.playNum = 0;
                sm.previousRead = "";
                sm.wordCache.clear();
                sm.wordCount = 0;
                return;
            }
        }

        // 返答できる単語がマップに存在しない場合終了(システム敗北)
        // システムが返せる単語を見つけれなかった場合
        if (nextword.equals("false")) {
            cdd.response = "うーん、単語が思いつきません... 私の負けです...";
            cdd.score = 15.0;
            candidateList.add(cdd);
            sm.playNum = 0;

            // 敗北した際、ユーザが入力した単語を検索し、単語を蓄積する
            List<String> wikiSearchRes = gl.gooMorpho(wikiResponse); // 検索結果を形態素解析
            Map<String, String> saveWord = new HashMap<>(); // 保存する単語をマップにまとめる

            for (int i = 0; i < wikiSearchRes.size(); i++) {
                if (wikiSearchRes.get(i).equals("品詞:名詞")) {
                    Pattern pattern = Pattern.compile("[a-zA-Z]|[0-9]"); // 正規表現、ローマ字大文字小文字、数字
                    Matcher matcher = pattern.matcher(wikiSearchRes.get(i - 1));
                    String wikigooProper = gl.gooProper(wikiSearchRes.get(i - 1));// 追加する単語を固有名詞解析
                    String wikiSearchWord = wikiSearchRes.get(i - 1).replace("基本形", ""); // 追加する単語
                    String wikiSearchRead = wikiSearchRes.get(i + 1).replace("読み", ""); // 追加する単語の読み
                    String wikiSearchResEnd = wikiSearchRead.substring(wikiSearchRead.length() - 1,
                            wikiSearchRead.length());
                    String wikiSearchReadRmBar = "";

                    if ((wikigooProper.contains("PSN")) || (wikigooProper.contains("LOC"))) { // 名詞が固有名詞だった場合マップに追加しない
                        continue;

                    } else if (matcher.find()) { // 名詞が正規表現に一致していればマップに追加しない
                        continue;
                    } else if (wikiSearchResEnd.equals("ー")) {
                        wikiSearchReadRmBar = wikiSearchRead.substring(2, wikiSearchRead.length() - 1);
                    }
                    if (wikiSearchResEnd.equals("ャ")) {
                        wikiSearchRead = wikiSearchRead.replace("ャ", "ヤ");
                    } else if (wikiSearchResEnd.equals("ュ")) {
                        wikiSearchRead = wikiSearchRead.replace("ュ", "ユ");
                    } else if (wikiSearchResEnd.equals("ョ")) {
                        wikiSearchRead = wikiSearchRead.replace("ョ", "ヨ");
                    }

                    if (wikiSearchReadRmBar.equals("ャ")) {
                        wikiSearchRead = wikiSearchReadRmBar.substring(0, sendInputRead.length() - 1) + "ヤ";
                    } else if (wikiSearchReadRmBar.equals("ュ")) {
                        wikiSearchRead = wikiSearchReadRmBar.substring(0, sendInputRead.length() - 1) + "ユ";
                    } else if (wikiSearchReadRmBar.equals("ョ")) {
                        wikiSearchRead = wikiSearchReadRmBar.substring(0, sendInputRead.length() - 1) + "ヨ";
                    }

                    saveWord.put(wikiSearchWord, wikiSearchRead);
                }
            }
            sm.previousRead = "";
            sw.saveWordMap(saveWord);
            sw.setUpWordData();
            sm.wordCache.clear();
            sm.wordCount = 0;
            return;
        } else if (nextword.substring(nextword.length() - 2, nextword.length() - 1).equals("ン")) {
            cdd.response = nextword + " " + "\"ン\"が付いてしまいました...私の負けです";
            cdd.score = 15.0;
            candidateList.add(cdd);
            sm.playNum = 0;
            sm.previousRead = "";
            sm.wordCount = 0;
            sm.wordCache.clear();
            return;
        } else {
            // 単語が見つかった場合返答
            sm.wordCount += 1;
            // 難易度による終了判定
            if (sm.wordCount > sm.wordLimit[sm.difficulty]) {
                cdd.response = "これ以上単語が思いつきません...私の負けです";
                cdd.score = 15.0;
                candidateList.add(cdd);
                sm.previousRead = "";
                sm.wordCount = 0;
                sm.wordCache.clear();
                sm.playNum = 0;
                return;
            }
            sm.wordCache.add(sw.key);
            sm.previousRead = nextword.substring(nextword.length() - 2, nextword.length() - 1);
            cdd.response = nextword;
            cdd.score = 15.0;
            candidateList.add(cdd);
            // for (String str : sm.wordCache) {
            // System.out.println(str);
            // }
        }
    }
}