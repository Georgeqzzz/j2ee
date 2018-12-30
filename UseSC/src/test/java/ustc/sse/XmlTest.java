package ustc.sse;
import org.junit.Test;

import utils.XmlUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

public class XmlTest {
    @Test
    public void test() throws MalformedURLException{
        File xml_file = new File("C:\\Users\\zgq\\git\\repository\\UseSC\\src\\main\\resources\\controller.xml");
        List<String> actionName = XmlUtils.getAllAttributes(xml_file,"name");
        //System.out.println(actionName);
    }
   
}

