/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tk.mybatis.mapper.hsqldb;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.*;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author liuzh
 */
public class HsqldbProvider extends MapperTemplate {
    public HsqldbProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 페이징 조회
     * @param ms
     * @return
     */
    public SqlNode selectPage(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //반환 값 유형을 엔티티 유형으로 수정
        setResultType(ms, entityClass);

        List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
        //정적 SQL 부분:select column ... from table
        sqlNodes.add(new StaticTextSqlNode("SELECT "
                + EntityHelper.getSelectColumns(entityClass)
                + " FROM "
                + tableName(entityClass)));
        //모든 열 가져 오기
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        List<SqlNode> ifNodes = new ArrayList<SqlNode>();
        boolean first = true;
        //모든 열을 반복하여 생성<if test="property!=null">[AND] column = #{property}</if>
        for (EntityColumn column : columnList) {
            StaticTextSqlNode columnNode
                    = new StaticTextSqlNode((first ? "" : " AND ") + column.getColumn() + " = #{entity." + column.getProperty() + "} ");
            if (column.getJavaType().equals(String.class)) {
                ifNodes.add(new IfSqlNode(columnNode, "entity."+column.getProperty() + " != null and " + "entity."+column.getProperty() + " != '' "));
            } else {
                ifNodes.add(new IfSqlNode(columnNode, "entity."+column.getProperty() + " != null "));
            }
            first = false;
        }
        //엔터티 판단 향상
        IfSqlNode ifSqlNode = new IfSqlNode(new MixedSqlNode(ifNodes),"entity!=null");
        //추가<where>
        sqlNodes.add(new WhereSqlNode(ms.getConfiguration(), ifSqlNode));
        //페이징 처리
        sqlNodes.add(new IfSqlNode(new StaticTextSqlNode(" LIMIT #{limit}"),"offset==0"));
        sqlNodes.add(new IfSqlNode(new StaticTextSqlNode(" LIMIT #{limit} OFFSET #{offset} "),"offset>0"));
        return new MixedSqlNode(sqlNodes);
    }
}
