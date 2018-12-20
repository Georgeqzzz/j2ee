package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
/*
<sc-configuration>
	<controller>
		<action name="login" class="zgq.ustc.action.LoginAction" method="login">
			<result name="success" type="forward" value="login_success.html"></result>
			<result name="failure" type="redirect" value="login_failed.html"></result>
		</action>
		<action name="logout" method="logout">
			<result name="success" type="forward" value="logout.html"></result>
		</action>
		<action name="register" class="zgq.ustc.action.RegisterAction" method="register">
			<result name="success" type="forward" value="register_success.html"></result>
			<result name="failure" type="redirect" value="register_failed.html"></result>
		</action>
	</controller>
</sc-configuration>
 */

    /**
     * 获取action的Attribute属性
     * @param xml_file
     * @param attr
     * @return
     * @throws MalformedURLException 
     */
    public static List<String> getActionAttributes(File xml_file, String attr) throws MalformedURLException{
        List<String> actionNames = new ArrayList<>();
        SAXReader reader = new SAXReader();
        try {
            // 获取xml文档
            Document document = reader.read(xml_file);
            // 获取根元素 sc-configuration
            Element root = document.getRootElement();
            // 获取Controller
            Element controller = root.element("controller");
            //得到action元素列表
            List<Element> action_list = controller.elements("action");
            for(Element action :action_list){
                actionNames.add(action.attribute(attr).getText());
            }
//            System.out.println(actionNames);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  actionNames;
    }

    /**
     * 获取所有的attribute属性
     * @param xml_file
     * @param attrName  要查询的属性名
     * @return
     * @throws MalformedURLException 
     */
    public static List<String> getAllAttributes(File xml_file,String attrName) throws MalformedURLException{
        List<String> attributes = new ArrayList<>();
        SAXReader reader = new SAXReader();
        try {
            // 获取xml文档
            Document document = reader.read(xml_file);
            // 获取根元素 sc-configuration
            Element root = document.getRootElement();
            //元素名为action的元素
            List<Node> action_list = root.selectNodes("//action");
            for(Node action :action_list){
                attributes.add(((Element)action).attribute(attrName).getText());
            }
            System.out.println(attributes);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  attributes;
    }

    /**
     * 查找属性名attr_name对应值为attr_value的，元素名为element_name的元素节点
     * @param xml_file
     * @param element_name：指定元素的名称
     * @param attr_name：指定属性的名称
     * @param attr_value：指定属性的值
     * @return 
     * @throws MalformedURLException 
     */
    public static Element getElementByAttr(File xml_file, String element_name,String attr_name, String attr_value) throws MalformedURLException{
        SAXReader reader = new SAXReader();
        Element element = null;
        try {
            Document document = reader.read(xml_file);
            Element root = document.getRootElement();
            //element_name[@attr_name='attr_value']
            String sel_str = MessageFormat.format("//{0}[@{1}=''{2}'']", element_name, attr_name, attr_value);
            System.out.println(sel_str);
            //查找满足条件的元素
            
            element = (Element) root.selectSingleNode(sel_str);          //????????
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return element;
    }

    /**
     * 获取指定元素节点中指定属性的属性值
     * @param element   元素名
     * @param attrName  属性名
     * @return
     */
    public static String getAttrValueByName(Element element, String attrName){
        if (element!=null){
            return element.attribute(attrName).getText();
        }else {
            throw new RuntimeException("传入的元素节点为null!");
        }

    }
}
