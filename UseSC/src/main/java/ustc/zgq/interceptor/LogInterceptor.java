package ustc.zgq.interceptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

public class LogInterceptor {
    private static SimpleDateFormat dateFormat 
    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//日期格式
    private static  String name="";
    private static String result="";
    private static String stime,etime;

    /**
     * 记录action，开始时间
     * @param name
     */
    public void preAction(String name){
        Date now = new Date();
        //开始时间
        LogInterceptor.stime=dateFormat.format( now );
        //action
        LogInterceptor.name=name;
        System.out.println("s-time:"+LogInterceptor.stime);
    }

    /**
     * 记录结束时间，返回结果串，完成写日志操作
     * @param result
     */
    public void afterAction(String result){
        Date now = new Date();
        //结束时间
        LogInterceptor.etime = dateFormat.format( now );
        //result
        LogInterceptor.result=result;
        System.out.println("xml: s-time:"+LogInterceptor.stime+"|e-time:"
        +LogInterceptor.etime+"|name:"+LogInterceptor.name+"|result"+LogInterceptor.result);
        writexml();
    }
    
    /**
     * 通过Dom树方式转换为xml方式，向log.xml文件中写入日志信息
     */
    public void writexml(){
        boolean first=false;
        File file=new File("C:\\Users\\zgq\\Desktop\\log_file\\log.xml");
        if (!file.exists()) {   first=true;}
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //通过文档构建器工厂获取一个文档构建器
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过文档通过文档构建器构建一个文档实例
            if(first){
                Document doc = db.newDocument();
                Element root=doc.createElement("log");
                doc.appendChild(root);
                TransformerFactory tff = TransformerFactory.newInstance();
                // 创建Transformer对象
                Transformer tf = tff.newTransformer();
                // 设置输出数据时换行
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                // 使用Transformer的transform()方法将DOM树转换成XML
                tf.transform(new DOMSource(doc), new StreamResult(file));

            }else {
                Document doc = db.parse(file);
                Element actionElement = doc.createElement("action");
                Element nameElement = doc.createElement("name");
                Element stimeElement = doc.createElement("s-time");
                Element etimeElement = doc.createElement("e-time");
                Element resultElement = doc.createElement("result");
                nameElement.setTextContent(name);
                stimeElement.setTextContent(stime);
                etimeElement.setTextContent(etime);
                resultElement.setTextContent(result);
                actionElement.appendChild(nameElement);
                actionElement.appendChild(stimeElement);
                actionElement.appendChild(etimeElement);
                actionElement.appendChild(resultElement);

                doc.getElementsByTagName("log").item(0).appendChild(actionElement);
                // 创建TransformerFactory对象
                TransformerFactory tff = TransformerFactory.newInstance();
                // 创建Transformer对象
                Transformer tf = tff.newTransformer();
                // 设置输出数据时换行
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                // 使用Transformer的transform()方法将DOM树转换成XML
                tf.transform(new DOMSource(doc), new StreamResult(file));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//public class LogInterceptor implements Interface4Interceptor {
//	@Override
//	public void preAction() {
//		System.out.println("preAction...");		
//	}
//	public void afterAction() {
//		System.out.println("afterAction...");
//	}
//}



