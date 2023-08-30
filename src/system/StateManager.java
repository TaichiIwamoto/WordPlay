package system;

import java.util.ArrayList;
import java.util.List;

public class StateManager {
    public boolean finishState; // 会話が終わっているかどうか
    public int playNum; // どの機能を使用しているか(0=会話,1=しりとり)
    public String previousRead = ""; // しりとりにてシステムの一つ前の返答
    public List<String> wordCache = new ArrayList<String>();
    public int difficulty;// 0:easy 1:normal 2:hard
    public int[] wordLimit = new int[] { 5, 20, 50 };
    public String[] wordLimitString = new String[] { "Easy", "Normal", "Hard" };
    public int wordCount;

    public StateManager() {
        playNum = 0;
        difficulty = 1;
        wordCount = 0;
        wordCache.clear();
        previousRead = "";
    }
}
