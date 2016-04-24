package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Door extends Exit {

  public static final int CLOSED = 1 << 0;
  public static final int LOCKED = 1 << 1;
  public static final int PASSABLE = 1 << 2;
  
  private int flags;
  public long keyID;
  
  public boolean isClosed() {return (flags & CLOSED) != 0;}
  public boolean isLocked() {return (flags & LOCKED) != 0;}
  public boolean isPassable() {return (flags & PASSABLE) != 0;}
  
  public void setFlag(int flag, boolean on) {
    if (on)
      flags |= flag;
    else
      flags &= ~flag;
  }
  
  @Override
  public Element compile(Document doc) {
    Element exitE = doc.createElement("door");
    exitE.appendChild(XML.cC(doc, "direction", direction.name()));
    exitE.appendChild(XML.cC(doc, "target_id", Long.toString(targetID)));
    exitE.appendChild(XML.cC(doc, "linked", Boolean.toString(isLinked)));
    exitE.appendChild(XML.cC(doc, "flags", Integer.toString(flags)));
    exitE.appendChild(XML.cC(doc, "key_id", Long.toString(keyID)));
    return exitE;
  }
  
  @Override
  public void load(Element doorE) {
    super.load(doorE);
    NodeList children = doorE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("flags"))
          flags = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("key_id"))
          keyID = Long.parseLong(((Text)e.getFirstChild()).getData());
      }
    }
  }
}
