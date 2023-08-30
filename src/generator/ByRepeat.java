package generator;

import java.util.List;
import system.*;

public class ByRepeat {
    public void generateResponseByRepeat(List<ResponseCandidate> candidateList) {
        ResponseCandidate cdd = new ResponseCandidate();
        cdd.response = ("何しますか？");
        cdd.score = 1.0;
        candidateList.add(cdd);
    }

}
