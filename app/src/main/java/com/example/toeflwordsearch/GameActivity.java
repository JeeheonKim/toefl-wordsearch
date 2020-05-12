package com.example.toeflwordsearch;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;

import static java.lang.String.format;

public class GameActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        try {
            Log.d("general", "hello in onCreate");
            //TODO: Use Shared Preferences for populating leveled data (for later)
            Map<String, String> map = readWordAndDef(R.raw.word0);
            Log.d("general", "map to String: \n"+map.toString());
            //TODO: change the param of readWordAndDef if the test works
            List<String> words = new ArrayList<String>();
            words.addAll((map.keySet()));
            Log.d("general", "Words Array: \n"+words.toString());
            printResult(createWordSearch(words));

            Character[] tmp = new Character[100];
            for (int rr=0; rr < nRows; rr++) {
                for (int cc = 0; cc < nCols; cc++) {
                    tmp[rr*10 + cc] = Grid.cells[rr][cc];
                }
            }
            GridView gridView = findViewById(R.id.gridview);
            ArrayAdapter adapter = new ArrayAdapter<Character>(GameActivity.this, R.layout.linearlayout_cell,R.id.cell_text,tmp);
            gridView.setAdapter(adapter);

            final ListView list = findViewById(R.id.list);
//            CustomAdapter customAdapter = new CustomAdapter(this, words);
//            list.setAdapter(customAdapter);
            //AlphabetTextAdapter alphabetTextAdapter = new AlphabetTextAdapter(this, tmp);
            //gridView.setAdapter(alphabetTextAdapter);
            Log.d("general", "end of onCreate");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Logic
    static class Grid {
        int numAttempts;
        static char[][] cells = new char[nRows][nCols];
        List<String> solutions = new ArrayList<>();
        static Map<String, String> wordAndDef = new HashMap<>();
    }

    final static int[][] directions = {
            {1, 0}, {0, 1}, {1, 1}, {1, -1},
            {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}
    };

    final static int nRows = 10;
    final static int nCols = 10;
    final static int gridSize = nRows * nCols;
    final static int minWords = 6; //Minimum word that should in the list

    final static Random rand = new Random();

    Map<String, String> readWordAndDef(int resId) throws IOException {
        int maxLen = Math.max(nRows, nCols);
        Map<String, String> wordAndDef = new HashMap<String, String>();
        String string = "";
        InputStream is = getResources().openRawResource(resId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (true){
            try {
                if ((string = reader.readLine()) == null) break;
            } catch (IOException e){
                e.printStackTrace();
            }
            string = string.trim().toLowerCase();
            String[] tokens = string.split(" ", 2);

            if (tokens[0].matches("^[a-z]{3," + maxLen + "}$")) {
                wordAndDef.put(tokens[0], tokens[1]);
            }
        }
        is.close();
        return wordAndDef;
    }

    static Grid createWordSearch(List<String> words) {
        Grid grid = null;
        int numAttempts = 0;

        outer:
        while (++numAttempts < 100) {
            Collections.shuffle(words);

            grid = new Grid();
            int cellsFilled = 0;
            for (String word : words) {
                cellsFilled += tryPlaceWord(grid, word);
                if (cellsFilled == gridSize) {
                    if (grid.solutions.size() >= minWords) {
                        grid.numAttempts = numAttempts;
                        break outer;
                    } else break; // grid is full but we didn't pack enough words, start over
                }
            }
        }
        return grid;
    }

    static int tryPlaceWord(Grid grid, String word) {
        int randDir = rand.nextInt(directions.length);
        int randPos = rand.nextInt(gridSize);

        for (int dir = 0; dir < directions.length; dir++) {
            dir = (dir + randDir) % directions.length;

            for (int pos = 0; pos < gridSize; pos++) {
                pos = (pos + randPos) % gridSize;

                int lettersPlaced = tryLocation(grid, word, dir, pos);
                if (lettersPlaced > 0)
                    return lettersPlaced;
            }
        }
        return 0;
    }

    static int tryLocation(Grid grid, String word, int dir, int pos) {
        int r = pos / nCols;
        int c = pos % nCols;
        int len = word.length();

        //  check bounds
        if ((directions[dir][0] == 1 && (len + c) > nCols)
                || (directions[dir][0] == -1 && (len - 1) > c)
                || (directions[dir][1] == 1 && (len + r) > nRows)
                || (directions[dir][1] == -1 && (len - 1) > r))
            return 0;

        int rr, cc, i, overlaps = 0;

        // check cells
        for (i = 0, rr = r, cc = c; i < len; i++) {
            if (Grid.cells[rr][cc] != 0 && Grid.cells[rr][cc] != word.charAt(i))
                return 0;
            cc += directions[dir][0];
            rr += directions[dir][1];
        }

        // place
        for (i = 0, rr = r, cc = c; i < len; i++) {
            if (Grid.cells[rr][cc] == word.charAt(i))
                overlaps++;
            else
                Grid.cells[rr][cc] = word.charAt(i);

            if (i < len - 1) {
                cc += directions[dir][0];
                rr += directions[dir][1];
            }
        }

        int lettersPlaced = len - overlaps;
        if (lettersPlaced > 0) {
            //TODO: Should use this to check answers when the user finishes a drag event
            grid.solutions.add(format("%-10s (%d,%d)(%d,%d)", word, c, r, cc, rr));
        }
        return lettersPlaced;
    }

    //TODO: May change this function to display on the xml or make another function
    static void printResult(Grid grid) {
        Log.d("general", "printResult begins");
        if (grid == null || grid.numAttempts == 0) {
            System.out.println("No grid to display");
            return;
        }
        int size = grid.solutions.size();
        String ret = "";
        ret += String.format("Number of words: " + size);

        ret += ("\n     0  1  2  3  4  5  6  7  8  9");
        for (int r = 0; r < nRows; r++) {
            ret += String.format("%n%d   ", r);
            for (int c = 0; c < nCols; c++)
                ret += String.format(" %c ", Grid.cells[r][c]);
        }

        ret += String.format("\n");

        for (int i = 0; i < size - 1; i += 2) {
            ret +=  String.format("%s   %s%n", grid.solutions.get(i),
                    grid.solutions.get(i + 1));
        }
        if (size % 2 == 1)
            ret += String.format(grid.solutions.get(size - 1));

        Log.d("general", ret);
    }

}
