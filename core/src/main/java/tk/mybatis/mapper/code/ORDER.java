package tk.mybatis.mapper.code;

/**
 * SQL 실행시기
 *
 * @author liuzh
 */
public enum ORDER {
    AFTER, //insert 실행 후 SQL
    BEFORE,//insert 사전 실행 SQL
    DEFAULT//전역 구성 사용
}
