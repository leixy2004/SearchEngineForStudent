package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;

/**
 * <pre>
 *     IndexSearcher是一个搜索命中结果的实现类.
 * </pre>
 */
public class IndexSearcher extends AbstractIndexSearcher {



    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
        this.index.optimize();
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
//        System.out.println("search1:!"+queryTerm.toString());
        AbstractPostingList postingList = this.index.search(queryTerm);
        if (postingList == null) {
            return null;
        }
        List<AbstractHit> hits = new ArrayList<AbstractHit>();
        for (int i=0;i<postingList.size();i++){
            AbstractPosting posting = postingList.get(i);
            AbstractHit hit = new Hit(
                    posting.getDocId(),
                    this.index.getDocName(posting.getDocId())
            );
            hit.getTermPostingMapping().put(queryTerm, posting);
            hit.setScore(sorter.score(hit));
            hits.add(hit);
        }

        sorter.sort(hits);
//        for (AbstractHit hit : hits) {
//            System.out.println(hit);
//        }
        return hits.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
//        System.out.println("search:!"+queryTerm1.toString() + " " + queryTerm2.toString()+
//                " " + combine.toString());
        AbstractPostingList postingList1 = this.index.search(queryTerm1);
        AbstractPostingList postingList2 = this.index.search(queryTerm2);


        List<AbstractHit> hits = new ArrayList<>();
        if (combine == LogicalCombination.AND) {
            if (postingList1 == null || postingList2 == null) {
                return null;
            }
            int i = 0, j = 0;
            while (i < postingList1.size() && j < postingList2.size()) {
                AbstractPosting posting1 = postingList1.get(i);
                AbstractPosting posting2 = postingList2.get(j);
                if (posting1.getDocId() == posting2.getDocId()) {
                    AbstractHit hit = new Hit(
                            posting1.getDocId(),
                            this.index.getDocName(posting1.getDocId())
                    );
                    hit.getTermPostingMapping().put(queryTerm1, posting1);
                    hit.getTermPostingMapping().put(queryTerm2, posting2);
                    hit.setScore(sorter.score(hit));
                    hits.add(hit);
                    i++;
                    j++;
                } else if (posting1.getDocId() < posting2.getDocId()) {
                    i++;
                } else {
                    j++;
                }
            }
        } else { // OR
            int i = 0, j = 0;
            if (postingList1 == null) {
                return search(queryTerm2, sorter);
            }
            if (postingList2 == null) {
                return search(queryTerm1, sorter);
            }
            while (i < postingList1.size() || j < postingList2.size()) {
//                System.out.println("i=" + i + " j=" + j);
                if (i < postingList1.size() && j >= postingList2.size()) {
                    AbstractPosting posting1 = postingList1.get(i);
                    AbstractHit hit = new Hit(
                            posting1.getDocId(),
                            this.index.getDocName(posting1.getDocId())
                    );
                    hit.getTermPostingMapping().put(queryTerm1, posting1);
                    hit.setScore(sorter.score(hit));
                    hits.add(hit);
                    i++;
                } else if (i >= postingList1.size() && j < postingList2.size()) {
                    AbstractPosting posting2 = postingList2.get(j);
                    AbstractHit hit = new Hit(
                            posting2.getDocId(),
                            this.index.getDocName(posting2.getDocId())
                    );
                    hit.getTermPostingMapping().put(queryTerm2, posting2);
                    hit.setScore(sorter.score(hit));
                    hits.add(hit);
                    j++;
                } else {
                    AbstractPosting posting1 = postingList1.get(i);
                    AbstractPosting posting2 = postingList2.get(j);
                    if (posting1.getDocId() == posting2.getDocId()) {
                        AbstractHit hit = new Hit(
                                posting1.getDocId(),
                                this.index.getDocName(posting1.getDocId())
                        );
                        hit.getTermPostingMapping().put(queryTerm1, posting1);
                        hit.getTermPostingMapping().put(queryTerm2, posting2);
                        hit.setScore(sorter.score(hit));
                        hits.add(hit);
//                        System.out.println("HELLO!");
                        i++;
                        j++;
                    } else if (posting1.getDocId() < posting2.getDocId()) {
                        AbstractHit hit = new Hit(
                                posting1.getDocId(),
                                this.index.getDocName(posting1.getDocId())
                        );
                        hit.getTermPostingMapping().put(queryTerm1, posting1);
                        hit.setScore(sorter.score(hit));
                        hits.add(hit);
                        i++;
                    } else {
                        AbstractHit hit = new Hit(
                                posting2.getDocId(),
                                this.index.getDocName(posting2.getDocId())
                        );
                        hit.getTermPostingMapping().put(queryTerm2, posting2);
                        hit.setScore(sorter.score(hit));
                        hits.add(hit);
                        j++;
                    }
                }
            }
        }
        sorter.sort(hits);
//        for (AbstractHit hit : hits) {
//            System.out.println(hit);
//        }
//        System.out.println("have "+hits.size());
        return hits.toArray(new AbstractHit[0]);
    }
}
