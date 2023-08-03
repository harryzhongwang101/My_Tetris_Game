package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameStats {
    private int round, averageScore, score, blockBound;
    private HashMap<String, Integer> blockStats = new HashMap<String, Integer>();

    public GameStats(){
        round = 0;
        averageScore = 0;
        score = 0;

        blockStats.put("I", 0);
        blockStats.put("J", 0);
        blockStats.put("K", 0);
        blockStats.put("L", 0);
        blockStats.put("O", 0);
        blockStats.put("S", 0);
        blockStats.put("T", 0);
        blockStats.put("Z", 0);
        blockStats.put("Plus", 0);
        blockStats.put("P", 0);
        blockStats.put("Q", 0);
    }

    // add the count of the current block, called in Tetris createRandomTetrisBlock
    public void addCount(String blockName){
        blockStats.put(blockName, blockStats.get(blockName)+1);
    }

    // both updates are called in Tetris gameOver
    public void updateScores(int score) {
        this.score = score;
        averageScore = (averageScore*round + score)/(round+1);
        setRound(round+1);

    }
    public void updateFile(String difficulty, int blockBound){
        this.blockBound = blockBound;
        // not checking if file exist cuz it still exists after you close the whole game?
        if(round == 1){
            createInitialFile(difficulty);
        }else {
            Path path = Paths.get("Statistics.txt");
            List<String> lines = new ArrayList<>();
            try {
                // read all lines
                lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // updates the average score, change line 2 of the text
            lines.set(1, "Average score per round: " + averageScore);
            try {
                // rewrite to the txt file with the updated average score
                Files.write(path, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // append the new round to the txt file
        try(FileWriter fw = new FileWriter("Statistics.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("------------------------------------------");
            out.println("Round #" + round);
            out.println("Score: " + score);

            int i = 1;
            for(String blocks: blockStats.keySet()){
                // in easy mode only need to print 7 blocks' count
                if(i>blockBound){
                    break;
                }
                i++;

                // instead of "Plus" the txt file should have "+" to indicate the block
                if(blocks.equals("Plus")){
                    out.println( "+: " + blockStats.get(blocks));
                    continue;
                }
                out.println(blocks + ": " + blockStats.get(blocks));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.resetCount();
    }
    private void createInitialFile(String difficulty){
        try (PrintWriter pw = new PrintWriter(new FileWriter("Statistics.txt"))) {
            pw.println("Difficulty: "+difficulty);
            pw.println("Average score per round: " + averageScore);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRound(int round) {
        this.round = round;
    }


    private void resetCount() {
        for(String blocks: blockStats.keySet()){
            blockStats.put(blocks, 0);
        }
    }
}
