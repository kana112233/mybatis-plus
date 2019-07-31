package com.baomidou.mybatisplus.extension.plugins.pagination;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SelectBody强转PlainSelect不支持sql里面带union
 * 临时解决方法
 * @date 2019/7/31
 */
class SelectBodyToPlainSelectTest {

    @Test
    void testSelectBodyToPlainSelectSuccess() {
        String originalSql = "select * from test";

        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;

        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
        assert plainSelect != null;
    }

    @Test
    void testSelectBodyToPlainSelectThrowException() {
        String originalSql = "select * from test union select * from test";

        Select selectStatement = null;
        try {
            selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
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
            assert plainSelect != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
