package tk.mybatis.mapper.base.genid;

import tk.mybatis.mapper.genid.GenId;

/**
 * 특별한 상황에 관계없이 간단한 구현은 프로덕션 환경에서 사용해서는 안됩니다.
 *
 * @author liuzh
 */
public class SimpleGenId implements GenId<Long> {
    private Long    time;
    private Integer seq;

    @Override
    public synchronized Long genId(String table, String column) {
        long current = System.currentTimeMillis();
        if (time == null || time != current) {
            time = current;
            seq = 1;
        } else if (current == time) {
            seq++;
        }
        return (time << 20) | seq;
    }
}
