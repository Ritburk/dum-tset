package net.cmacpherson.mud;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Room implements XML.Processable {
  
  public static final int LIGHT = 1 << 0;
  public static final int CURSED = 1 << 1;
  public static final int SAFE = 1 << 2;
  
  public long id;
  public String name;
  public ArrayList<String> description = new ArrayList<String>();
  public Exit[] exits = new Exit[Direction.values().length];
  public Environment environment;
  private int flags;
  public Location location;
  public ArrayList<Character> chars = new ArrayList<Character>();
  public ArrayList<Item> items = new ArrayList<Item>();
  public Coordinate pos;
  
  public boolean isLit() {return (flags & LIGHT) != 0;}
  public boolean isCursed() {return (flags & CURSED) != 0;}
  public boolean isSafe() {return (flags & SAFE) != 0;}
  
  public void setFlag(int flag, boolean on) {
    if (on)
      flags |= flag;
    else
      flags &= ~flag;
  }
  
  public boolean hasLight() {
    if (isLit())
      return true;
    boolean hasLight = false;
    for (Character c : chars)
      hasLight |= c.isProducingLight();
    return hasLight;
  }
  
  public String displayExits() {
    String line = "Exits: [";
    for (int i = 0; i < exits.length; i++)
      if (exits[i] != null &&
          !exits[i].isHidden) {
        if (exits[i] instanceof Door) {
          if (((Door)exits[i]).isClosed())
            line += "<closed:" + Direction.values()[i].name().toLowerCase() + "> ";
        } else
          line += Direction.values()[i].name().toLowerCase() + " ";
      }
    line = line.trim() + "]";
    if (line.equals("Exits: []"))
      line = "Exits: [none]";
    return line;
  }
  
  @Override
  public Element compile(Document doc) {
    Element roomE = doc.createElement("room");
    roomE.appendChild(XML.cC(doc, "id", Long.toString(id)));
    roomE.appendChild(XML.cC(doc, "name", name));
    Element descE = doc.createElement("description");
    for (String line : description)
      descE.appendChild(XML.cC(doc, "line", line));
    roomE.appendChild(descE);
    Element exitsE = doc.createElement("exits");
    for (int i = 0; i < exits.length; i++)
      if (exits[i] != null)
        exitsE.appendChild(exits[i].compile(doc));
    roomE.appendChild(exitsE);
    roomE.appendChild(XML.cC(doc, "environment", environment.name()));
    roomE.appendChild(XML.cC(doc, "flags", Integer.toString(flags)));
    roomE.appendChild(XML.cC(doc, "pos", pos.toString()));
    return roomE;
  }
  
  @Override
  public void load(Element roomE) {
     NodeList children = roomE.getChildNodes();
     for (int i = 0; i < children.getLength(); i++) {
       Node child = children.item(i);
       if (child instanceof Element) {
         Element e = (Element)child;
         if (e.getTagName().equals("id"))
           id = Long.parseLong(((Text)e.getFirstChild()).getData());
         else if (e.getTagName().equals("name"))
           name = ((Text)e.getFirstChild()).getData();
         else if (e.getTagName().equals("description")) {
           NodeList children2 = e.getChildNodes();
           for (int j = 0; j < children2.getLength(); j++) {
             Node child2 = children2.item(j);
             if (child2 instanceof Element) {
               Element e2 = (Element)child2;
               if (e2.getTagName().equals("line"))
                 description.add(((Text)e2.getFirstChild()).getData());
             }
           }
         } else if (e.getTagName().equals("exits")) {
           NodeList children2 = e.getChildNodes();
           for (int j = 0; j < children2.getLength(); j++) {
             Node child2 = children2.item(j);
             if (child2 instanceof Element) {
               Element e2 = (Element)child2;
               if (e2.getTagName().equals("exit")) {
                 Exit exit = new Exit();
                 exit.load(e2);
                 exits[exit.direction.ordinal()] = exit;
               } else if (e2.getTagName().equals("door")) {
                 Exit door = new Door();
                 door.load(e2);
                 exits[door.direction.ordinal()] = door;
               }
             }
           }
         } else if (e.getTagName().equals("environment"))
           environment = Environment.valueOf(((Text)e.getFirstChild()).getData());
         else if (e.getTagName().equals("flags"))
           flags = Integer.parseInt(((Text)e.getFirstChild()).getData());
         else if (e.getTagName().equals("pos"))
           pos = Coordinate.parse(((Text)e.getFirstChild()).getData());
       }
     }
  }
  
  @Override
  public String getFilePath() {return null;}
}
