package net.cmacpherson.mud;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XML {
  
  public static final XML INSTANCE = new XML();
  private DocumentBuilder builder;
  
  public XML() {
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }
  
  public void build(long[] config) throws TransformerConfigurationException,
                                          TransformerException {
    Document doc = builder.newDocument();
    Element root = doc.createElement("config");
    root.appendChild(cC(doc, "last_room_id", Long.toString(config[0])));
    root.appendChild(cC(doc, "last_item_id", Long.toString(config[1])));
    root.appendChild(cC(doc, "last_key_id", Long.toString(config[2])));
    root.appendChild(cC(doc, "last_proto_item_id", Long.toString(config[3])));
    root.appendChild(cC(doc, "last_proto_mob_id", Long.toString(config[4])));
    doc.appendChild(root);
    Transformer t = TransformerFactory.newInstance().newTransformer();
    t.transform(new DOMSource(doc), new StreamResult(Globals.CONFIG_FILE));
  }
  
  public long[] parseConfig(File file) throws IOException,
                                              SAXException {
    long[] config = new long[] {-1, -1, -1, -1, -1};
    Document doc = builder.parse(file);
    Element root = doc.getDocumentElement();
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("last_room_id"))
          config[0] = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("last_item_id"))
          config[1] = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("last_key_id"))
          config[2] = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("last_proto_item_id"))
          config[3] = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("last_proto_mob_id"))
          config[4] = Long.parseLong(((Text)e.getFirstChild()).getData());
      }
    }
    return config;
  }
  
  public void build(Processable obj) throws TransformerConfigurationException,
                                            TransformerException {
    Document doc = builder.newDocument();
    doc.appendChild(obj.compile(doc));
    Transformer t = TransformerFactory.newInstance().newTransformer();
    t.transform(new DOMSource(doc), new StreamResult(new File(obj.getFilePath())));
  }
  
  public Element parse(File file) throws IOException,
                                         SAXException {
    return builder.parse(file).getDocumentElement();
  }
  
  public static Element cC(Document doc, String name, String data) {
    Element e = doc.createElement(name);
    Text t = doc.createTextNode(data);
    e.appendChild(t);
    return e;
  }
  
  public interface Processable {
    public Element compile(Document doc);
    public void load(Element e);
    public String getFilePath();
  }
}
