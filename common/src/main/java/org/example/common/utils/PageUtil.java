package org.example.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;


public class PageUtil {

    public static <T> List<T> queryPage(
            Integer pageNo,
            Integer pageSize,
            Function<Page<T>, IPage<T>> queryFunc
    ) {
        // 1. 校验分页参数（默认页码1，默认每页10条）
        pageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;

        // 2. 初始化分页对象（MyBatis-Plus的Page）
        Page<T> page = Page.of(pageNo, pageSize);

        // 3. 执行查询（通过函数式接口传入具体查询逻辑）
        IPage<T> resultPage = queryFunc.apply(page);
        //4. 封装分页结果
        // 当前页数据
        List<T> records = resultPage.getRecords();



        return records;
    }

}
