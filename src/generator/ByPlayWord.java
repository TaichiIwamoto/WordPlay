package generator;

import java.util.List;
import system.*;
import util.OneShotResponse;;

//特殊会話パターンにより返答を生成する
public class ByPlayWord {
    public OneShotResponse osr = new OneShotResponse();

    /**
     * 
     * @param input           :ユーザが入力した文
     * @param candidateList   :返答リスト
     * @param playPatternList :特殊会話リスト
     * @param sm              :ステータスマネージャー
     */
    public void generateResponseByPlayWords(String input, List<ResponseCandidate> candidateList,
            List<ReactionPattern> playPatternList, StateManager sm) {
        double score = 10.0;

        for (ReactionPattern ptn : playPatternList) {
            if (input.contains(ptn.keyword)) {
                ResponseCandidate cdd = new ResponseCandidate();
                cdd.response = ptn.response;
                cdd.score = score;
                candidateList.add(cdd);
                if (ptn.response.contains("しりとり")) {
                    sm.playNum = 1;
                } else if (ptn.response.contains("難易度")) {
                    if (input.contains("変")) {
                        int dif = osr.ChangeDifficulty();
                        sm.difficulty = dif;
                        cdd.response = "難易度を" + sm.wordLimitString[sm.difficulty] + "に変更しました";
                        return;
                    }
                    cdd.response += sm.wordLimitString[sm.difficulty] + "です";
                }

            }
        }
    }
}
