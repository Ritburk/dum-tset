package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Exit implements XML.Processable {

  public long targetID;
  public Location target;
  public boolean isLinked;
  public Direction direction;
  public boolean isHidden;
  
  @Override
  public Element compile(Document doc) {
    Element dirE = doc.createElement("exit");
    dirE.appendChild(XML.cC(doc, "direction", direction.name()));
    dirE.appendChild(XML.cC(doc, "target_id", Long.toString(targetID)));
    dirE.appendChild(XML.cC(doc, "linked", Boolean.toString(isLinked)));
    dirE.appendChild(XML.cC(doc, "hidden", Boolean.toString(isHidden)));
    return dirE;
  }
  
  @Override
  public void load(Element exitE) {
    NodeList children = exitE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("direction"))
          direction = Direction.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("target_id"))
          targetID = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("linked"))
          isLinked = Boolean.parseBoolean(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("hidden"))
          isHidden = Boolean.parseBoolean(((Text)e.getFirstChild()).getData());
      }
    }
  }
  
  @Override
  public String getFilePath() {return null;}
}
