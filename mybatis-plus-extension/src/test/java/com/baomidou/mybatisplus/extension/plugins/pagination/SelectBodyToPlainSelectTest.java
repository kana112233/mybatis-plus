package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SelectBody强转PlainSelect不支持sql里面最外层带union
 * 用SetOperationList处理sql带union的语句
 */
class SelectBodyToPlainSelectTest {

    private Page<?> page = new Page<>();

    @BeforeEach
    void setup() {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem order = new OrderItem();
        order.setAsc(true);
        order.setColumn("column");
        orderItems.add(order);
        page.setOrders(orderItems);
    }

    @Test
    void testPaginationInterceptorConcatOrderByBefore() {
        String actualSql = PaginationInterceptor
            .concatOrderBy("select * from test", page);

        assertThat(actualSql).isEqualTo("SELECT * FROM test ORDER BY column");

        String actualSqlWhere = PaginationInterceptor
            .concatOrderBy("select * from test where 1 = 1", page);

        assertThat(actualSqlWhere).isEqualTo("SELECT * FROM test WHERE 1 = 1 ORDER BY column");
    }

    @Test
    void testPaginationInterceptorConcatOrderByFix() {
        String actualSql = PaginationInterceptor
            .concatOrderBy("select * from test union select * from test2", page);
        assertThat(actualSql).isEqualTo("SELECT * FROM test UNION SELECT * FROM test2 ORDER BY column");

        String actualSqlUnionAll = PaginationInterceptor
            .concatOrderBy("select * from test union all select * from test2", page);
        assertThat(actualSqlUnionAll).isEqualTo("SELECT * FROM test UNION ALL SELECT * FROM test2 ORDER BY column");
    }

    @Test
    void testPaginationInterceptorConcatOrderByFixWithWhere() {
        String actualSqlWhere = PaginationInterceptor
            .concatOrderBy("select * from test where 1 = 1 union select * from test2 where 1 = 1", page);
        assertThat(actualSqlWhere).isEqualTo("SELECT * FROM test WHERE 1 = 1 UNION SELECT * FROM test2 WHERE 1 = 1 ORDER BY column");

        String actualSqlUnionAll = PaginationInterceptor
            .concatOrderBy("select * from test where 1 = 1 union all select * from test2 where 1 = 1 ", page);
        assertThat(actualSqlUnionAll).isEqualTo("SELECT * FROM test WHERE 1 = 1 UNION ALL SELECT * FROM test2 WHERE 1 = 1 ORDER BY column");
    }

}
