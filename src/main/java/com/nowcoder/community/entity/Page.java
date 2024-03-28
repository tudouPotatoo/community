package com.nowcoder.community.entity;

import lombok.Data;

/**
 * 我认为 最后还可以直接用Mybatis的Page插件直接实现
 */

/**
 * 封装分页信息
 *
 * 思考：
 * 分页-已知信息 pageNum当前页码, pageSize 页面大小
 * 分页需要什么信息-总页数、分页查询需要的信息（offset、pageSize）、页面能够从几页到第几页的选项
 *               总页数：总数据条数/页面大小
 *               分页需要的信息：offset = (pageNum - 1) * pageSize、pageSize已知
 *               FromPage：pageNum - 2
 *               toPage：pageNum + 2
 */
@Data
public class Page {
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    /**
     * 页面大小
     */
    private Integer pageSize = 10;
    /**
     * 总数据条数
     */
    private Integer rows;
    /**
     * 分页查询的路径
     */
    private String path;

    /**
     * 获取总页数
     */
    public int getTotal() {
        if (rows % pageSize == 0) {
            return rows / pageSize;
        } else {
            return (rows / pageSize) + 1;
        }
    }

    /**
     * 获取起始页码
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取结束页码
     */
    public int getFrom() {
        int from = pageNum - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取最后一个选项的页码
     */
    public int getTo() {
        int to = pageNum + 2;
        int total = getTotal();
        return to <= total ? to : total;
    }

}
