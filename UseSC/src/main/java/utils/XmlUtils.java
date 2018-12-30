package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlUtils {
	
	public static Properties config_prop;
    public static SimpleDateFormat date_format;
    static {
        config_prop = new Properties();
        try {
            config_prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/config.properties"));
            date_format = new SimpleDateFormat(config_prop.getProperty("date_format"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	 /**
     * 将XML文件转换成HTML文件字节数组输出流
     * @param xsl_path xsl文件路径 (在资源文件目录下)
     * @param xml_path xml文件路径 (在资源文件目录下)
     * @return HTML字节数组输出流
     */
    public static ByteArrayOutputStream ConvertXml2Html(String xsl_path,String xml_path){
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        StreamSource source_xsl = new StreamSource(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(xsl_path));
        StreamSource source_xml = new StreamSource(Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(xml_path));
        try {
            transformer = factory.newTransformer(source_xsl);
            StreamResult output = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            output = new StreamResult(baos);

            transformer.transform(source_xml,output);
            String str = baos.toString();
            System.out.println(str);
            return baos;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	 
    /**
     * 获取action的Attribute属性
     * @param xml_file
     * @param attr
     * @return
     * @throws MalformedURLException 
     */
    public static List<String> getActionAttributes(File xml_file, String attr) 
    		throws MalformedURLException{
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
    public static List<String> getAllAttributes(File xml_file,String attrName) 
    		throws MalformedURLException{
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
    public static Element getElementByAttr(File xml_file, String element_name,
    		String attr_name, String attr_value) throws MalformedURLException{
        SAXReader reader = new SAXReader();
        Element element = null;
        try {
            Document document = reader.read(xml_file);
            Element root = document.getRootElement();
            //element_name[@attr_name='attr_value']
            String sel_str = MessageFormat.format("//{0}[@{1}=''{2}'']", 
            		element_name, attr_name, attr_value);
            System.out.println(sel_str);
            //查找满足条件的元素
            element = (Element) root.selectSingleNode(sel_str);   
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
    
    /**
     * 获取指定标签名的所有节点
     * @param is
     * @param eleName
     * @return
     */
    public static List<Element> getElementsByName(InputStream is, String eleName){
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        return  root.elements(eleName);
    }

	 /**
     * 获取一个指定标签名的元素
     * @param is
     * @param eleName
     * @return
     */
    public static Element getElementByName(InputStream is, String eleName){
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        return  root.element(eleName);
    }
    
    /**
     * 记录日志到log_xml文件
     * @param document
     * @param file
     */
    public static void writeXML(Document document, File file) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        try {
            XMLWriter writer = new XMLWriter(new FileWriter(file),outputFormat);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
