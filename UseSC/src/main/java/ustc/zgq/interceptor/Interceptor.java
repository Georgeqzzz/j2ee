package ustc.zgq.interceptor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Interceptor {
	private String name;
	private String className;
	private String preDo;
	private String afterDo;
}
