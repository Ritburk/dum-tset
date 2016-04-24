package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Proficiencies implements XML.Processable {

  private double[] levels = new double[Name.values().length];
  
  public Proficiencies() {
    for (int i = 0; i < levels.length; i++)
      levels[i] = 0;
  }
  
  public int getLevel(Name prof) {
    return (int)levels[prof.ordinal()];
  }

  public void improve(Name prof, double d) {
    levels[prof.ordinal()] += d;
  }
  
  @Override
  public Element compile(Document doc) {
    Element profE = doc.createElement("proficiencies");
    for (Name prof : Name.values())
      profE.appendChild(XML.cC(doc, prof.name(), Double.toString(levels[prof.ordinal()])));
    return profE;
  }

  @Override
  public void load(Element profE) {
    NodeList children = profE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        levels[Name.valueOf(e.getTagName()).ordinal()] = 
            Double.parseDouble(((Text)e.getFirstChild()).getData());
      }
    }
  }

  @Override
  public String getFilePath() {return null;}
  
  public enum Name {
    UNARMED,
    SWORD,
    SPEAR,
    DOUBLE,
    AXE,
    CLUB,
    DAGGER,
    STAFF,
    BOW,
    CROSSBOW,
    SLING,
    CLOTH_ARMOR,
    LEATHER_ARMOR,
    SCALE_ARMOR,
    CHAIN_ARMOR,
    PLATE_ARMOR,
    SHIELD
  }
}
