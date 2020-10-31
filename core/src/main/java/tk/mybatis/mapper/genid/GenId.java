package tk.mybatis.mapper.genid;

/**
 * 구체적인 구현은 제공되지 않으며 여기에 아이디어가 있습니다.<br/>
 *
 * Spring 통합 환경에서는 정적으로 구성하여 Spring의 컨텍스트 객체를 얻을 수 있습니다.<br/>
 *
 * 사용하는 경우 vesta(https://gitee.com/robertleepeak/vesta-id-generator) ID를 생성하려면 vesta의 idService가 제공되었다고 가정합니다.<br/>
 *
 * 그런 다음 구현에서 클래스를 가져온 다음 반환 할 Id를 생성 할 수 있습니다. 샘플 코드는 다음과 같습니다.：
 *
 * <pre>
 * public class VestaGenId implement GenId<Long> {
 *    public Long genId(String table, String column){
 *        //ApplicationUtil.getBean 직접 구현해야합니다.
 *        IdService idService = ApplicationUtil.getBean(IdService.class);
 *        return idService.genId();
 *    }
 * }
 * </pre>
 *
 * @author liuzh
 */
public interface GenId<T> {

    T genId(String table, String column);

    class NULL implements GenId {
        @Override
        public Object genId(String table, String column) {
            throw new UnsupportedOperationException();
        }
    }


}
