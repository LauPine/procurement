/**
 * Copyright © 2014 税友软件集团股份有限公司 All rights reserved.
 */
package cn.com.liucm.dto;

import java.io.Serializable;

/**
 * <p>
 * Title: 实体基础类
 * </p>
 * <p>
 * <p>
 * Description:
 * </p>
 * Author: maomj </p>
 * <p>
 * Copyright: 税友软件集团股份有限公司
 * </p>
 * <p>
 * Create Time: 2014-5-28 上午9:27:42
 * 
 * @version 1.0
 */
public abstract class BaseDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1001827477085627541L;

    /**
     * 对象放入set集合的需要重写
     * 
     * @param obj
     *            对象
     * @return boolean
     */
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * 对象放入集合的需要重写
     * 
     * @return int
     */
    public int hashCode() {
        return super.hashCode();
    }

}
