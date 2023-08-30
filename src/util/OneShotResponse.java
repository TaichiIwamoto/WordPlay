package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OneShotResponse {
    public int ChangeDifficulty() {
        try {
            String output;
            output = "どの難易度にしますか？ Easy Normal Hard";
            System.out.println("システム:" + output);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "Shift-JIS"));
            String input;
            System.out.print("ユーザ:");
            while ((input = br.readLine()) != null) {
                if (input.equals("Easy")) {
                    return 0;
                } else if (input.equals("Normal")) {
                    return 1;
                } else if (input.equals("Hard")) {
                    return 2;
                } else {
                    System.out.println("システム:難易度を選択してください Easy Nomarl Hard");
                    System.out.print("ユーザ:");
                    continue;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
