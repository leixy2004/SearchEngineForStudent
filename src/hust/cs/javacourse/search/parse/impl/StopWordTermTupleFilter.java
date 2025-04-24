package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.util.Arrays;
import java.util.HashSet;
/**
 * <pre>
 * StopWordTermTupleFilter是停用词过滤器的实现类
 *      该过滤器用于过滤掉停用词
 * </pre>
 */
public class StopWordTermTupleFilter extends AbstractTermTupleFilter {

    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public StopWordTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
        this.stopWords = new HashSet<>(Arrays.asList(StopWords.STOP_WORDS));
    }

    private final HashSet<String> stopWords;

    /**
     * 获得下一个三元组
     *
     * @return : 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        while (true) {
            AbstractTermTuple tuple = input.next();
            if (tuple == null) {
                return null;
            }
            if (!stopWords.contains(tuple.term.toString())) {
                return tuple;
            }
        }
    }
}
