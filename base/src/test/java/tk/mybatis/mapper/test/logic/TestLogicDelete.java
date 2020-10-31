package tk.mybatis.mapper.test.logic;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.mapper.TbUserLogicDeleteMapper;
import tk.mybatis.mapper.mapper.TbUserMapper;
import tk.mybatis.mapper.model.TbUser;
import tk.mybatis.mapper.model.TbUserLogicDelete;

import java.util.List;

public class TestLogicDelete {

    @Test
    public void testLogicDeleteByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();

        try {
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            logicDeleteMapper.deleteByPrimaryKey(3);
            Assert.assertFalse(logicDeleteMapper.existsWithPrimaryKey(3));

            Assert.assertTrue(mapper.existsWithPrimaryKey(3));

            // 삭제 표시가 된 데이터를 삭제합니다. 영향을받는 행 수는 0입니다.
            Assert.assertEquals(0, logicDeleteMapper.deleteByPrimaryKey(9));

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }

    }

    @Test
    // 항목을 삭제하면 삭제되지 않은 조회 조건이 적용되고 항목 클래스에서 설정된 값이 논리적 삭제 필드에 무시됩니다.
    public void testLogicDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();

        try {
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            // 사용자 이름이 test 인 데이터가 2 개 있으며 그 중 1 개가 논리적 삭제로 표시되었습니다.
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test");
            Assert.assertTrue(logicDeleteMapper.existsWithPrimaryKey(8));

            // 삭제 표시는 1 만 삭제합니다.
            Assert.assertEquals(1, logicDeleteMapper.delete(tbUserLogicDelete));
            Assert.assertFalse(logicDeleteMapper.existsWithPrimaryKey(8));

            // 총 4 개 삭제 취소됨
            Assert.assertEquals(4, logicDeleteMapper.selectAll().size());

            TbUser tbUser = new TbUser();
            tbUser.setUsername("test");
            Assert.assertEquals(2,  mapper.select(tbUser).size());

            // 논리적으로 삭제 된 데이터 2 개를 물리적으로 삭제
            Assert.assertEquals(2, mapper.delete(tbUser));

            // 삭제되지 않은 총 개수는 여전히 4 개입니다.
            Assert.assertEquals(4, logicDeleteMapper.selectAll().size());

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testLogicDeleteByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();

        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);

            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("id", 1);

            logicDeleteMapper.deleteByExample(example);
            Assert.assertFalse(logicDeleteMapper.existsWithPrimaryKey(1));

            Assert.assertTrue(mapper.existsWithPrimaryKey(1));

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    // 기본 키 조회에 따르면 삭제 표시 주석 조회는 삭제되지 않은 조회 조건을 사용합니다.
    public void testSelectByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertNull(logicDeleteMapper.selectByPrimaryKey(9));

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertEquals(0, (int) mapper.selectByPrimaryKey(9).getIsValid());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testExistsWithPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertFalse(logicDeleteMapper.existsWithPrimaryKey(9));

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertTrue(mapper.existsWithPrimaryKey(9));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    // 모두 조회, 삭제되지 않은 조회 조건이 삭제 표시 조회에 사용됩니다.
    public void testSelectAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertEquals(5, logicDeleteMapper.selectAll().size());

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertEquals(9, mapper.selectAll().size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    // 조회의 수는 삭제되지 않은 조회 조건을 가져오고 엔티티 클래스에서 설정된 값을 논리적 삭제 필드로 무시합니다.
    public void selectCount() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            // 실제로 삭제되지 않은 항목은 5 개, 삭제 된 항목은 4 개, 설정 값 0 무시, 삭제되지 않은 항목 5 개 조회
            tbUserLogicDelete.setIsValid(0);
            Assert.assertEquals(5, logicDeleteMapper.selectCount(tbUserLogicDelete));

            // 삭제 표시 주석이없는 경우 지정된 조건에 따라 조회
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            Assert.assertEquals(9, mapper.selectCount(tbUser));
            tbUser.setIsValid(0);
            Assert.assertEquals(4, mapper.selectCount(tbUser));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    // 엔티티 질의에 따라 삭제되지 않은 질의 조건을 가져 오며, 논리적 삭제 필드에 엔티티 클래스가 설정 한 값은 무시됩니다.
    public void testSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            // 실제로 삭제되지 않은 항목은 5 개, 삭제 된 항목은 4 개, 설정 값 0 무시, 삭제되지 않은 항목 5 개 조회
            tbUserLogicDelete.setIsValid(0);
            Assert.assertEquals(5, logicDeleteMapper.select(tbUserLogicDelete).size());

            tbUserLogicDelete.setUsername("test");
            Assert.assertEquals(1, logicDeleteMapper.select(tbUserLogicDelete).size());
            Assert.assertEquals(8, (long) logicDeleteMapper.select(tbUserLogicDelete).get(0).getId());

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            // 삭제 표시 주석 없음, 지정된 조건에 따라 조회
            tbUser.setIsValid(0);
            Assert.assertEquals(4, mapper.select(tbUser).size());

            tbUser.setUsername("test");
            Assert.assertEquals(1, mapper.select(tbUser).size());
            Assert.assertEquals(9, (long) mapper.select(tbUser).get(0).getId());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test111");
            logicDeleteMapper.insert(tbUserLogicDelete);

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            tbUser.setUsername("test222");
            mapper.insert(tbUser);

            Assert.assertEquals(1, mapper.selectCount(tbUser));

            TbUserLogicDelete tbUserLogicDelete1 = new TbUserLogicDelete();
            tbUserLogicDelete1.setUsername("test222");
            Assert.assertEquals(0, logicDeleteMapper.selectCount(tbUserLogicDelete1));

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsertSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test333");
            logicDeleteMapper.insertSelective(tbUserLogicDelete);

            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete));

            TbUserLogicDelete tbUserLogicDelete1 = new TbUserLogicDelete();
            tbUserLogicDelete1.setUsername("test333");
            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete1));

            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            tbUser.setUsername("test333");
            mapper.insertSelective(tbUser);

            TbUser tbUser2 = new TbUser();
            tbUser2.setUsername("test333");
            Assert.assertEquals(2, mapper.selectCount(tbUser2));

            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete1));

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdate() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = logicDeleteMapper.selectByPrimaryKey(1);

            tbUserLogicDelete.setPassword(null);
            logicDeleteMapper.updateByPrimaryKey(tbUserLogicDelete);

            Assert.assertNull(logicDeleteMapper.select(tbUserLogicDelete).get(0).getPassword());

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            TbUserLogicDelete tbUserLogicDelete = logicDeleteMapper.selectByPrimaryKey(1);

            tbUserLogicDelete.setPassword(null);
            logicDeleteMapper.updateByPrimaryKeySelective(tbUserLogicDelete);

            Assert.assertEquals("12345678", logicDeleteMapper.select(tbUserLogicDelete).get(0).getPassword());

        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("id", 9);
            Assert.assertEquals(0, logicDeleteMapper.selectByExample(example).size());

            example.or().andEqualTo("username", "test");
            Assert.assertEquals(1, logicDeleteMapper.selectByExample(example).size());


        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByExample2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            // 사용자 이름이 테스트 인 두 개가 있으며, 하나는 삭제 된 것으로 표시됩니다.
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");
            Assert.assertEquals(1, logicDeleteMapper.selectByExample(example).size());

            // 비밀번호 dddd로 삭제되고 사용자 이름 test2로 삭제되지 않음
            example.or().andEqualTo("password", "dddd").orEqualTo("username", "test2");

            Assert.assertEquals(2, logicDeleteMapper.selectByExample(example).size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            // 사용자 이름이 테스트 인 두 개가 있으며, 하나는 삭제 된 것으로 표시됩니다.
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");
            logicDeleteMapper.updateByExample(tbUserLogicDelete, example);

            example.clear();
            example.createCriteria().andEqualTo("username", "123");
            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(1, list.size());

            Assert.assertNull(list.get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExampleSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");
            logicDeleteMapper.updateByExampleSelective(tbUserLogicDelete, example);

            example.clear();
            example.createCriteria().andEqualTo("username", "123");
            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(1, list.size());

            Assert.assertEquals("gggg", list.get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    // 예에서 조건이없는 비정상 조건, 조건은 논리적 삭제 주석의 삭제되지 않은 조건이어야합니다.
    public void testExampleWithNoCriteria() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);

            Example example = new Example(TbUserLogicDelete.class);

            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");

            Assert.assertEquals(5, logicDeleteMapper.updateByExample(tbUserLogicDelete, example));

            Assert.assertEquals(5, logicDeleteMapper.updateByExampleSelective(tbUserLogicDelete, example));

            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(5, list.size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }
}
