package hust.cs.javacourse.search.parse.impl;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;
import java.util.List;

public class TermTupleScanner extends AbstractTermTupleScanner {
    private int curPos = 0;
    StringSplitter splitter;
    List<String> saved;

    public TermTupleScanner(BufferedReader input) {
        super(input);
        splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        saved = new LinkedList<>();
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        String result = null;
        while (saved.isEmpty()) {
            String line = null;
            try {
                line = input.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (line == null) {
                return null;
            }
            saved.addAll(splitter.splitByRegex(line));
        }
        result = saved.get(0);
        saved.remove(0);
        return new TermTuple(new Term(result.toLowerCase()), curPos++);
    }

}
