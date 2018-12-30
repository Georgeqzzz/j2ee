package ustc.zgq.interceptor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Element;
import utils.XmlUtils;

public class InterceptorConfigManager {
	private static Map<String,Interceptor> map;  //保存获取的interceptor
	
	public static Map<String,Interceptor> getInterceptorConfig(String path){
		if(map == null) {
			map = new HashMap<>();
		//解析controller.xml,获取所有的interceptor节点
		InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        List<Element> interceptor_list = XmlUtils.getElementsByName(is, "interceptor");
        if (interceptor_list !=null){
            for(Element interceptor_ele : interceptor_list ){
                Interceptor interceptor = new Interceptor();
                String name = interceptor_ele.attributeValue("name");
                interceptor.setName(name);
                interceptor.setClassName(interceptor_ele.attributeValue("class"));
                interceptor.setPreDo(interceptor_ele.attributeValue("predo"));
                interceptor.setAfterDo(interceptor_ele.attributeValue("afterdo"));
                map.put(name,interceptor);
            }
        }
    }
    return map;
	}

}
