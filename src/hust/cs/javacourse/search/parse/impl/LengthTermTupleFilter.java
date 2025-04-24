package hust.cs.javacourse.search.parse.impl;


import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;
/**
 * <pre>
 * LengthTermTupleFilter是长度过滤器的实现类
 *      该过滤器用于过滤掉长度小于3的三元组
 * </pre>
 */
public class LengthTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public LengthTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

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
            var length = tuple.term.toString().length();
            if (length >= Config.TERM_FILTER_MINLENGTH && length <= Config.TERM_FILTER_MAXLENGTH) {
                return tuple;
            }
            // 如果长度小于3，继续读取下一个三元组
        }
    }
}
