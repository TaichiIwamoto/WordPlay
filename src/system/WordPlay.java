package system;

import java.io.*;
import java.util.*;
import generator.*;
import util.SetWord;
import webApi.*;

public class WordPlay {
    public List<ReactionPattern> patternList;
    public List<ReactionPattern> playPatternList;
    public ByPlayWord bpw = new ByPlayWord();
    public ByPattern bp = new ByPattern();
    public ByChainGame bcg = new ByChainGame();
    public GooLab goo = new GooLab();
    public StateManager sm = new StateManager();
    public SetWord sw = new SetWord();
    public ByRepeat repeat = new ByRepeat();

    public void StartWordPlay() {
        String output = "こんにちは！ 言葉遊びチャットBotです!";
        System.out.println("システム:" + output);

        setupReactionPattern(); // リアクションパターンのセットアップ
        sw.setUpWordData(); // しりとり辞書のセットアップ

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "Shift-JIS"));
            System.out.print("ユーザ:");
            String input;
            while ((input = br.readLine()) != null) {
                output = generateResponse(input, output);
                System.out.println("システム:" + output);
                if (sm.finishState == true) {
                    return;
                }
                System.out.print("ユーザ:");
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // pattern.txtからリアクションパターンを初期化
    public void setupReactionPattern() {
        patternList = new ArrayList<ReactionPattern>();
        playPatternList = new ArrayList<ReactionPattern>();

        // 通常会話のパターンリストセットアップ
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream("WordPlay/src/resource/pattern.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t"); // タブ文字を区切りとして分割する
                ReactionPattern ptn = new ReactionPattern(); // 新しいインスタンスを作る
                ptn.keyword = split[0];
                ptn.response = split[1];
                patternList.add(ptn); // パターンリストに追加する
            }
            br.close();
        } catch (IOException ex) { // 例外処理
            ex.printStackTrace();
        }

        // 特殊会話のパターンリストセットアップ
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream("WordPlay/src/resource/playPattern.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t"); // タブ文字を区切りとして分割する
                ReactionPattern ptn = new ReactionPattern(); // 新しいインスタンスを作る
                ptn.keyword = split[0];
                ptn.response = split[1];
                playPatternList.add(ptn); // パターンリストに追加する
            }
            br.close();
        } catch (IOException ex) { // 例外処理
            ex.printStackTrace();
        }
    }

    // システムの返答を生成する
    public String generateResponse(String input, String previousOutput) {
        List<ResponseCandidate> candidateList = new ArrayList<ResponseCandidate>();

        if (sm.playNum == 0) {
            bpw.generateResponseByPlayWords(input, candidateList, playPatternList, sm); // 特殊会話パターンによる返答生成
            bp.generateResponseByPattern(input, candidateList, patternList, sm); // 通常会話パターンによる返答生成
            repeat.generateResponseByRepeat(candidateList);
        } else if (sm.playNum == 1) {
            bcg.ChainGame(input, candidateList, sm, sw); // しりとりによる返答生成
        }

        // generatorによって生成された返答、スコアを比較しretに代入する
        String ret = "";
        double maxScore = -1.0;
        for (ResponseCandidate cdd : candidateList) {
            if (cdd.score > maxScore) {
                ret = cdd.response;
                maxScore = cdd.score;
            }
        }
        return ret;
    }

}
