package ustc.zgq.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Element;
import utils.XmlUtils;

/**
 * Servlet implementation class SimpleController
 */
//@WebServlet("/SimpleController")
public class SimpleController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public static final String ERROR = "error.html";
	private static final String PRE_EXECUTION = "predo";
    private static final String AFTER_EXECUTION = "afterdo";
    private File controller_xml;
    private List<String> actionNames;
    private boolean hasAction;
    private boolean hasInterceptor;
    private String interceptor_name;
    private String result;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimpleController() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req,resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String path = req.getServletPath();
		System.out.println(path);
		// 获取请求动作名称 截取/xxx.sc
		String action = path.substring(path.lastIndexOf('/')+1,path.lastIndexOf('.'));
		System.out.println(action);
		//打开配置文件
		controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());
		//从配置文件中读出action的名称并存入list列表中
		actionNames = XmlUtils.getActionAttributes(controller_xml, "name");
		hasAction = false; // 判断方法是否匹配
		for(String actionName : actionNames){
			if (actionName.equals(action)){
			// 匹配成功,利用反射执行相应操作
            hasAction=true;
            // 得到action名对应的action元素
            Element action_element = XmlUtils.getElementByAttr(controller_xml,"action","name",actionName);  
            //获取interceptor-ref节点
            List<Element> interceptor_ref_elements = action_element.elements("interceptor-ref");
            // 判断是否存在interceptor-ref节点
            if (interceptor_ref_elements !=null){
                hasInterceptor = true;
                for(Element interceptor_ref_element : interceptor_ref_elements){
                	//get the intercetor_name
                    interceptor_name = interceptor_ref_element.attribute("name").getText();
                    try {
                    	System.out.println("predo");
                    	//执行predo方法
//						doInterceptor(interceptor_name,PRE_EXECUTION);
                    	doInterceptor(interceptor_name,PRE_EXECUTION,action);                   	                  	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
            
            //得到action对应的class名
            String class_name = XmlUtils.getAttrValueByName(action_element,"class");
            //得到method名
            String method_name = XmlUtils.getAttrValueByName(action_element,"method");
            try {
                //利用反射执行指定方法获取方法返回值
//                result = doMethod(class_name, method_name);           	
            	result = doMethod(class_name,method_name,req.toString());       	
                // 根据方法的返回值,查询次action下的result节点的name属性 跳转/重定向
                handleResult(action_element,result,method_name,req,resp);
                if (hasInterceptor){ 
                    for(Element interceptor_ref_element : interceptor_ref_elements){
                        interceptor_name = interceptor_ref_element.attribute("name").getText();
                        // do afterdo() method
                        System.out.println("afterdo");
                        doInterceptor(interceptor_name,AFTER_EXECUTION,result);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
		
    if (!hasAction) { // 没有请求的方法
        resp.sendRedirect(ERROR);
    }
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
   * 根据result结果字符串处理跳转/重定向
   * @param action_element
   * @param result
   */
  private void handleResult(Element action_element, String result,String method,
		  HttpServletRequest request,HttpServletResponse response) {
      String sel_str = MessageFormat.format("result[@name=''{0}'']",result);
      System.out.println(sel_str);
      Element result_element = (Element) action_element.selectSingleNode(sel_str);
      //get the type
      String type = result_element.attribute("type").getText();
      //get the value
      String value = result_element.attribute("value").getText();
      if (value.endsWith("_view.xml")) {
          // 根据xml动态生成客户端html视图
          try {
              response.getWriter().write(XmlUtils.ConvertXml2Html("/success_view_t.xsl",
                      "/success_view.xml").toString());
          } catch (IOException e) {
              e.printStackTrace();
          }
      }else 
      try {
    	  //a new request
          request.setAttribute("type",type+":"+ method);
          // 转发到指定页面
          if ("forward".equals(type)) { 
              request.getRequestDispatcher(value).forward(request, response);
           // 重定向到指定页面
          } else if ("redirect".equals(type)) { 
              response.sendRedirect(value);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }	
	}
  
 /**
  * 拦截器方法的执行
  * @param interceptor_name 拦截器名
  * @param interceptor_order 拦截方法名
  * @param parameter 传入到方法中的参数
  * @throws Exception
  */
  private void doInterceptor(String interceptor_name, String interceptor_order,
		  String parameter) throws Exception {
      Element interceptor = XmlUtils.getElementByAttr(controller_xml,
    		  "interceptor","name",interceptor_name);
      //get the class_name
      String class_name = interceptor.attribute("class").getText();
      //get the method_name
      String method_name = interceptor.attribute(interceptor_order).getText();
      //do the method
      doMethod(class_name,method_name,parameter);
  }
  
///**
//* 拦截器方法的执行 
//* @param interceptor_name  
//* @param interceptor_order the method to be done
//* @throws Exception 
//*/
//private void doInterceptor(String interceptor_name, String interceptor_order) throws Exception {
//   Element interceptor = XmlUtils.getElementByAttr(controller_xml,"interceptor","name",interceptor_name);
//   //get the class_name
//   String class_name = interceptor.attribute("class").getText();
//   //get the method_name
//   String method_name = interceptor.attribute(interceptor_order).getText();
//   //do the method
//   doMethod(class_name,method_name);
//}

}

