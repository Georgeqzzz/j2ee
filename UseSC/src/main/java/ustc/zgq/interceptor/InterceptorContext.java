package ustc.zgq.interceptor;

import java.util.HashMap;
import java.util.Map;

public class InterceptorContext {

    private Map<String,Interceptor> map_config;
    private Map<String,Object> context = new HashMap<>();

    public InterceptorContext(String path){
    	//打开interceptor的config文件
        map_config = InterceptorConfigManager.getInterceptorConfig(path);
        for (Map.Entry<String,Interceptor> entry : map_config.entrySet()){
            String interceptor_name = entry.getKey();
            Interceptor interceptor = entry.getValue();
            Object existInterceptor = context.get(interceptor_name);
            //若无拦截器实例
            if (existInterceptor == null){
                try {
                    Class clazz = Class.forName(interceptor.getClassName());
                    existInterceptor = clazz.newInstance();
                    context.put(interceptor_name,existInterceptor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取拦截器的实例,拦截器均是单例
     * @param name
     * @return
     */
    public Object getInterceptor(String name){
        return context.get(name);
    }
}
