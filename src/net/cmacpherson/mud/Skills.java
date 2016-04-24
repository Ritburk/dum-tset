package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Skills implements XML.Processable {
  
  private double[] levels = new double[Name.values().length];
  
  public Skills() {
    for (int i = 0; i < levels.length; i++)
      levels[i] = 0;
  }
  
  public int getLevel(Name skill) {
    return (int)levels[skill.ordinal()];
  }

  public void improve(Name skill, double d) {
    levels[skill.ordinal()] += d;
  }
  
  public String getClassName() {
    //TODO class name
    return "Peasant";
  }
  
  public String getClassAbr() {
    //TODO class abreviation
    return "PEA";
  }
  
  @Override
  public Element compile(Document doc) {
    Element skills = doc.createElement("skills");
    for (Name skill : Name.values())
      skills.appendChild(XML.cC(doc, skill.name(), Double.toString(levels[skill.ordinal()])));
    return skills;
  }

  @Override
  public void load(Element skillsE) {
    NodeList children = skillsE.getChildNodes();
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
    ONE_HANDED,
    TWO_HANDED,
    DUAL_WEILD,
    BOW,
    SHIELD,
    DIVINE_MAGIC,
    ARCANE_MAGIC,
    SNEAKING,
    SMITHING,
    ENCHANTING,
    SOUL_SMITHING,
    ALCHEMY,
    SPEECH,
    SEARCHING,
    MERCANTILISM,
    TRAPPING
  }
}
