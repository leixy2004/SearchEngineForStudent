package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * <pre>
 * PatternTermTupleFilter是正则表达式过滤器的实现类
 *      该过滤器用于过滤掉不符合正则表达式的三元组
 * </pre>
 */
public class PatternTermTupleFilter extends AbstractTermTupleFilter {
//    private final Pattern pattern;


    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
//        pattern = Pattern.compile(Config.TERM_FILTER_PATTERN);
    }

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        while (true) {
            AbstractTermTuple tuple = input.next();
            if (tuple == null) {
                return null;
            }
            if (Pattern.matches(Config.TERM_FILTER_PATTERN, tuple.term.toString())) {
                return tuple;
            }
        }
    }
}
