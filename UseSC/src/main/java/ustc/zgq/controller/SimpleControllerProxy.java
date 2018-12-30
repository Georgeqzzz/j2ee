package ustc.zgq.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Element;
import ustc.zgq.proxy.ActionProxy;
import utils.XmlUtils;

public class SimpleControllerProxy extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	private static final String ERROR = "error.html";
    private String request_path;
    private String action_name;
    private File controller_xml;
    private List<String> actionNames;
    private boolean hasAction;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initParams(req, resp);
        for(String actionName : actionNames){
        	// 匹配成功 name=login,利用反射执行相应操作
            if (actionName.equals(action_name)){
                hasAction =true;
                //  获取当前action节点
                Element action_element = XmlUtils.getElementByAttr(controller_xml,"action","name",actionName);
                // 判断是否存在interceptor-ref节点
                String class_name = XmlUtils.getAttrValueByName(action_element,"class");
                String method_name = XmlUtils.getAttrValueByName(action_element,"method");
                try {
                    // 创建被代理对象和代理对象,调用代理对象的method
                    Class clazz = Class.forName(class_name);
                    //生成被代理对象的实例
                    Object target = clazz.newInstance();
                    //生成代理对象实例
                    ActionProxy actionProxy = new ActionProxy();
                    //动态绑定
                    Object proxy = actionProxy.getProxy(target);
                    // 调用代理对象加强业务方法
                    Method method = clazz.getDeclaredMethod(method_name,String.class);
                    String result = (String) method.invoke(proxy,action_name);
                    // 根据方法的返回值,查询次action下的result节点的name属性 跳转/重定向
                    handleResult(action_element, result,method_name,req,resp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!hasAction) { // 没有匹配请求的方法
            resp.sendRedirect(ERROR);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    /**
     * 初始化请求的数据 request_path action_name controller_xml actionNames
     * @param req
     * @param resp
     * @throws MalformedURLException 
     */
    private void initParams(HttpServletRequest req, HttpServletResponse resp) throws MalformedURLException {
        resp.setCharacterEncoding("gbk"); // 设置response字节编码
        // 获取请求路径
        request_path = req.getServletPath();
//        System.out.println(path);
        // 获取请求动作名称 截取 /xxx.sc
        action_name = request_path.substring(request_path.lastIndexOf('/')+1, request_path.lastIndexOf('.'));
        System.out.println("action_name: "+action_name);
        // 获取资源文件下的xml配置文件
        controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());
        // 获取所有action的name属性
        actionNames = XmlUtils.getActionAttributes(controller_xml, "name");
        // 判断方法是否匹配
        hasAction = false;
    }

     /**
      * 利用反射机制生成指定的class的实例并执行指定的方法
      * @param class_name 给定的class名
      * @param method_name 给定的方法名
      * @return
      * @throws Exception
      */
      private String doMethod(String class_name, String method_name) throws Exception {
    	  // 根据类名获得Class
          Class clazz = Class.forName(class_name);
          //得到类的一个实例
          Object instance =  clazz.newInstance();
          //得到类中的方法
          Method method = instance.getClass().getDeclaredMethod(method_name);
          return (String)method.invoke(instance);
      }
     
     /**
      * 
      * @param class_name 方法的类名
      * @param method_name 方法名
      * @param parameter 传入方法的参数
      * @return
      * @throws Exception
      */
      private String doMethod(String class_name, String method_name,String parameter) throws Exception {
    	  // 根据类名获得Class
          Class clazz = Class.forName(class_name);
          //得到类的一个实例
          Object instance =  clazz.newInstance();
          //得到类中的含参的方法
          Method method = instance.getClass().getDeclaredMethod(method_name,String.class);
          return (String)method.invoke(instance,parameter);
      }

    /**
     * 根据Action方法的返回值对结果进行处理
     * @param action_element
     * @param result
     * @param method
     * @param request
     * @param response
     */
    private void handleResult(Element action_element, String result,String method,HttpServletRequest request,HttpServletResponse response) {
        String sel_str = MessageFormat.format("result[@name=''{0}'']",result);
//        System.out.println(sel_str);
        // 获取result节点
        Element result_element = (Element) action_element.selectSingleNode(sel_str);
        // 获取result的type value属性
        String type = result_element.attribute("type").getText();
        String value = result_element.attribute("value").getText();

        if (value.endsWith("_view.xml")) {
            // 根据xml动态生成客户端html视图
            try {
                response.getWriter().write(XmlUtils.ConvertXml2Html("/success_view_t.xsl",
                        "/success_view.xml").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {

            try {
                request.setAttribute("type",type+":"+ method);
                if ("forward".equals(type)) { // 转发到指定页面
                    request.getRequestDispatcher(value).forward(request, response);
                } else if ("redirect".equals(type)) { // 重定向到指定页面
                    response.sendRedirect(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}