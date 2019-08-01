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
 * 临时解决方法
 * 提交pr解决
 */
class SelectBodyToPlainSelectTest {

    private String originalSql = "select * from test";
    private String actualSql = "SELECT * FROM test";

    private String originalUnionSql = "select * from test union select * from test2";
    private String actualUnionSql = "SELECT * FROM test UNION SELECT * FROM test2";

    @Test
    void testSelectBodyToPlainSelectSuccess() {
        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;

        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
        assertThat(plainSelect.toString())
            .isEqualTo(actualSql);
    }

    @Test
    void testSelectBodyToPlainSelectThrowException() {
        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalUnionSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;
        try {
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            System.out.println("don't hit this" + plainSelect);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("net.sf.jsqlparser.statement.select.SetOperationList cannot be cast to net.sf.jsqlparser.statement.select.PlainSelect");
        }
    }

    /**
     * 临时解决方法
     */
    @Test
    void testResoleSelectBodyToPlainSelectThrowException() {
        String originalSql = "select * from (select * from test union select * from test) as temp";

        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;

        try {
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            assertThat(plainSelect.toString())
                .isEqualTo("SELECT * FROM (SELECT * FROM test UNION SELECT * FROM test) AS temp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 最终解决方法
     */
    @Test
    void testSetOperationList() {
        parseSql(originalSql, actualSql);

        parseSql(originalUnionSql,
            actualUnionSql);


    }

    @Test
    void testWithItemNotSupportWith() {
        String originalSql = "with cc as (select * from test) select * from cc";
        parseSql(originalSql, "SELECT * FROM cc");
    }


    private void parseSql(String originalSql, String actualStr) {
        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;

        if (selectStatement.getSelectBody() instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            assertThat(plainSelect.toString())
                .isEqualTo(actualStr);
        } else if (selectStatement.getSelectBody() instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectStatement.getSelectBody();
            assertThat(setOperationList.toString())
                .isEqualTo(actualStr);
        } else if (selectStatement.getSelectBody() instanceof WithItem) {
            System.out.println("WithItem");
            assert false;
        } else {
            System.out.println("else");
            assert false;
        }
    }

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
            .concatOrderBy(originalSql, page);

        assertThat(result).isEqualTo(actualSql);
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
            .concatOrderBy(originalUnionSql, page);

        assertThat(result).isEqualTo(actualUnionSql);
    }


}
