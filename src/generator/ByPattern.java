package generator;

import java.util.List;
import system.*;

//通常会話パターンにより返答を生成する
public class ByPattern {
    /**
     * @param input         :ユーザが入力した文
     * @param candidateList :返答リスト
     * @param patternList   :通常会話パターンリスト
     * @param sm            :ステータスマネージャー
     */
    public void generateResponseByPattern(String input, List<ResponseCandidate> candidateList,
            List<ReactionPattern> patternList, StateManager sm) {
        double score = 3.0;
        for (ReactionPattern ptn : patternList) {
            if (input.contains(ptn.keyword)) {
                if (ptn.response.contains("またいつでも遊びに来てくださいね！")) {
                    sm.finishState = true;
                }
                ResponseCandidate cdd = new ResponseCandidate();
                cdd.response = ptn.response;
                cdd.score = score;
                candidateList.add(cdd);
            }
        }
    }
}
