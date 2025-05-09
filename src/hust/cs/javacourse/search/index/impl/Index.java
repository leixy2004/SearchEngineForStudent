package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        return "Index{" +
                "docIdToDocPathMapping=" + docIdToDocPathMapping +
                ", termToPostingListMapping=" + termToPostingListMapping +
                '}';
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());
        List<AbstractTermTuple> tuples = document.getTuples();
        Map<AbstractTerm,AbstractPosting> termToPosting = new TreeMap<>();
        document.getTuples().forEach(tuple -> {
            AbstractTerm term = tuple.term;
            if (!termToPosting.containsKey(term)) {
                termToPosting.put(term, new Posting(document.getDocId(), 0, new ArrayList<>()));
            }
            AbstractPosting posting = termToPosting.get(term);
            posting.setFreq(posting.getFreq() + tuple.freq);
            posting.getPositions().add(tuple.curPos);
        });
        termToPosting.forEach((term, posting) -> {
//            if (!termToPostingListMapping.containsKey(term)) {
//                termToPostingListMapping.put(term, new PostingList());
//            }
//            termToPostingListMapping.get(term).add(posting);
            termToPostingListMapping.computeIfAbsent(term, k -> new PostingList()).add(posting);
        });
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            this.readObject(ois);
        } catch (IOException e) {
            System.err.println("Error while reading " + file);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                System.err.println("Error while closing " + file);
            }
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            this.writeObject(oos);
        } catch (IOException e) {
            System.err.println("Error while writing " + file);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.err.println("Error while closing " + file);
            }
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return this.termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return this.termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (var entry : termToPostingListMapping.entrySet()) {
            AbstractPostingList postingList = entry.getValue();
            postingList.sort();
            for (int i = 0; i < postingList.size(); i++) {
                AbstractPosting posting = postingList.get(i);
                posting.sort();
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this);

        } catch (IOException e) {
            System.err.println("Error while writing " + out);
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            Index index = (Index) in.readObject();
            this.docIdToDocPathMapping = index.docIdToDocPathMapping;
            this.termToPostingListMapping = index.termToPostingListMapping;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while reading " + in);
        }
    }
}
