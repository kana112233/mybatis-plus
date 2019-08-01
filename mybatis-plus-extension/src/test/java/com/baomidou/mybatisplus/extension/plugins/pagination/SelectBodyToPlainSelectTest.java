package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SelectBody强转PlainSelect不支持sql里面最外层带union
 * 用SetOperationList处理sql带union的语句
 */
class SelectBodyToPlainSelectTest {

    @Test
    void testPaginationInterceptorConcatOrderByBefore() {
        Page<?> page = new Page<>();
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem order = new OrderItem();
        order.setAsc(true);
        order.setColumn("column");
        orderItems.add(order);
        page.setOrders(orderItems);
        String result = PaginationInterceptor
            .concatOrderBy("select * from test", page);

        assertThat(result).isEqualTo("SELECT * FROM test ORDER BY column");
    }

    @Test
    void testPaginationInterceptorConcatOrderByFix() {
        Page<?> page = new Page<>();
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem order = new OrderItem();
        order.setAsc(true);
        order.setColumn("column");
        orderItems.add(order);
        page.setOrders(orderItems);

        String result = PaginationInterceptor
            .concatOrderBy("select * from test union select * from test2", page);

        assertThat(result).isEqualTo("SELECT * FROM test UNION SELECT * FROM test2 ORDER BY column");
    }

}
