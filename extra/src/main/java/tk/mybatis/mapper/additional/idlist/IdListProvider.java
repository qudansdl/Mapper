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

package tk.mybatis.mapper.additional.idlist;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.List;
import java.util.Set;

/**
 * ID 문자열을 통한 다양한 작업
 * <p/>
 * ids 같은 "1,2,3"
 *
 * @author liuzh
 */
public class IdListProvider extends MapperTemplate {

    public IdListProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * idList는 필수.
     *
     * @param list
     * @param errorMsg
     */
    public static void notEmpty(List<?> list, String errorMsg){
        if(list == null || list.size() == 0){
            throw new MapperException(errorMsg);
        }
    }

    /**
     * 기본 키 문자열에 따라 삭제합니다. 클래스에는 @Id로 주석이 달린 필드가 하나만 있습니다.
     *
     * @param ms
     * @return
     */
    public String deleteByIdList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        appendWhereIdList(sql, entityClass, getConfig().isSafeDelete());
        return sql.toString();
    }

    /**
     * 기본 키 문자열을 기반으로하는 조회, 클래스에 @Id로 주석이 달린 필드가 하나만 있습니다.
     *
     * @param ms
     * @return
     */
    public String selectByIdList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //반환 값을 엔티티 유형으로 수정
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        appendWhereIdList(sql, entityClass, false);
        return sql.toString();
    }

    /**
     * 접합 조건
     *
     * @param sql
     * @param entityClass
     */
    private void appendWhereIdList(StringBuilder sql, Class<?> entityClass, boolean notEmpty){
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            if(notEmpty){
                sql.append("<bind name=\"notEmptyListCheck\" value=\"@tk.mybatis.mapper.additional.idlist.IdListProvider@notEmpty(");
                sql.append("idList, 'idList 필수')\"/>");
            }
            sql.append("<where>");
            sql.append("<foreach collection=\"idList\" item=\"id\" separator=\",\" open=\"");
            sql.append(column.getColumn());
            sql.append(" in ");
            sql.append("(\" close=\")\">");
            sql.append("#{id}");
            sql.append("</foreach>");
            sql.append("</where>");
        } else {
            throw new MapperException("继承 ByIdList 方法의엔티티 클래스[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解의들");
        }
    }
}
