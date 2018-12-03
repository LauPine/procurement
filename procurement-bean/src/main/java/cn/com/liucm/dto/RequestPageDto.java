package cn.com.liucm.dto;

/**
 * <p> Project: tel-sale </p>
 * <p> Function: [功能：] </p>
 * <p> Description: [功能描述：] </p>
 * <p> Copyright: Copyright(c) 2009-2018 税友集团 </p>
 * <p> Company: 税友集团</p>
 * <p> UpdateDate:2017-03-22</p>
 * <p> Updator:pyl</p>
 *
 * @version 5.0
 * @since 5.0
 */
public class RequestPageDto extends BaseDTO{
    /**
     * 当前页号
     */
    private int pageIndex = 0;

    /**
     * 每页显示条数
     */
    private int pageSize = 20;

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
}
