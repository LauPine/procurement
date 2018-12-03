package cn.com.liucm.dto;

import java.util.List;

/**
 *<p>Project: 内容管理系统 </p>
 *<p>Function: 分页实体</p>
 *<p>Description: 分页实体
 *</p>
 *<p>Copyright:Copyright(c) 2014-2030 税友集团 </p>
 *<p>
 *Company: 税友软件集团有限公司
 *</p>
 *@author pyl
 *@version 1.0
 */
public class PageDto<T> extends BaseDTO{
    /**
     * 当前页号
     */
    private int pageIndex = 0;

    /**
     * 每页显示条数
     */
    private int pageSize = 15;

    /**
     * 总条数
     */
    private long total;
    /**
     * 返回结果集
     */
    private List<T> list;

    /**
     * 空构造方法
     */
    public PageDto(){
    }
    /**
     * 默认构造方法
     * 
     * @param pageIndex
     *            当前页码
     * @param totalCount
     *            数据库中总记录条数
     * @param pageSize
     *            本页容量
     * @param list
     *            本页包含的数据
     */
    public PageDto(List<T> list, long totalCount, int pageIndex, int pageSize) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.total = totalCount;
        this.list = list;
    }
    /**
     * 构造方法
     * @param totalCount totalCount
     * @param list list
     */
    public PageDto(List<T> list, long totalCount) {
        this.list = list;
        this.total = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex + 1;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
