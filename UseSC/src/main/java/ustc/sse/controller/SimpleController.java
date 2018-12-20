package ustc.sse.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SimpleController() {
        super();
        // TODO Auto-generated constructor stub
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
		// 获取请求动作名称 截取 /xxx.sc
		String action = path.substring(path.lastIndexOf('/')+1,path.lastIndexOf('.'));
		System.out.println(action);
		//打开配置文件
		File controller_xml = new File(this.getClass().getResource("/controller.xml").getFile());
		//从配置文件中读出action的名称并存入list列表中
		List<String> actionNames = XmlUtils.getActionAttributes(controller_xml, "name");
		boolean hasAction = false; // 判断方法是否匹配
		for(String actionName : actionNames){
			if (actionName.equals(action)){
			// 匹配成功 name=login,利用反射执行相应操作
            hasAction=true;
            // 得到action名对应的action元素
            Element action_element = XmlUtils.getElementByAttr(controller_xml,"action","name",actionName);
            //得到action对应的class名
            String class_name = XmlUtils.getAttrValueByName(action_element,"class");
            //得到method名
            String method_name = XmlUtils.getAttrValueByName(action_element,"method");
            try {
                // 利用反射执行指定方法获取方法返回值
                String result = doMethod(class_name, method_name);
                // 根据方法的返回值,查询次action下的result节点的name属性 跳转/重定向
                handleResult(action_element,result,method_name,req,resp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    if (!hasAction) { // 没有请求的方法
        resp.sendRedirect(ERROR);
    }
}

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
   * 根据result结果字符串处理跳转/重定向
   * @param action_element
   * @param result
   */
  private void handleResult(Element action_element, String result,String method,HttpServletRequest request,HttpServletResponse response) {
      String sel_str = MessageFormat.format("result[@name=''{0}'']",result);
      System.out.println(sel_str);
      Element result_element = (Element) action_element.selectSingleNode(sel_str);
      //得到type
      String type = result_element.attribute("type").getText();
      //得到value值
      String value = result_element.attribute("value").getText();
      try {
    	  //新的request
          request.setAttribute("type",type+":"+ method);
          if ("forward".equals(type)) { // 转发到指定页面
              request.getRequestDispatcher(value).forward(request, response);
          } else if ("redirect".equals(type)) { // 重定向到指定页面
              response.sendRedirect(value);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }	
		
//        out.println("<html>");  
//        out.println("<head>");  
//        out.println("<title>SimpleController</title>");  
//        out.println("</head>");  
//        out.println("<body>");  
//        out.println("<h1>欢迎使用SimpleController!</h1>");  
//        out.println("</body>");  
//        out.println("</html>");  
	}

}

