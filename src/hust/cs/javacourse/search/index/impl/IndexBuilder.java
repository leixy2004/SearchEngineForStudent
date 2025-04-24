package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * <pre>
 * IndexBuilder是索引构造器的实现类.
 *      该类实现了AbstractIndexBuilder的buildIndex方法，完成索引构造的工作
 * </pre>
 */
public class IndexBuilder extends AbstractIndexBuilder {
    /**
     * <pre>
     * 构造函数
     * @param docBuilder ：AbstractDocumentBuilder子类对象
     * </pre>
     */
    public IndexBuilder(AbstractDocumentBuilder docBuilder) {
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        var list = FileUtil.list(rootDirectory);
        AbstractIndex index = new Index();
        for (var file_path : list) {
            File file = new File(file_path);
            AbstractDocument document = docBuilder.build(docId, file_path, file);
            index.addDocument(document);
            docId++;
        }
        return index;
    }
}
