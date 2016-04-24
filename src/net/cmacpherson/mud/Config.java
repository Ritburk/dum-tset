package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Config implements XML.Processable {

  //option to show current capacity on containers when displaying their name
  public boolean showPrompt = true;
  
  public enum Channel {
    CHAT,
    TELL,
    GTELL,
    QUESTION,
    AUCTION,
    IMMTALK
  }
  public Object[][] channels = new Object[][] {
      //level on/off color
  /*    CHAT*/{ 0,  true, Color.CYAN},
  /*    TELL*/{ 0,  true, Color.MAGENTA},
  /*   GTELL*/{ 0,  true, Color.WHITE},
  /*QUESTION*/{ 0, false, Color.MAGENTA},
  /* AUCTION*/{ 0, false, Color.RED},
  /* IMMTALK*/{ 1,  true, Color.H_CYAN},
  };
  
  @Override
  public Element compile(Document doc) {
    Element config = doc.createElement("config");
    config.appendChild(XML.cC(doc, "show_prompt", Boolean.toString(showPrompt)));
    Element channelsE = doc.createElement("channels");
    for (Channel c : Channel.values())
      channelsE.appendChild(XML.cC(doc, c.name(), Boolean.toString((Boolean)channels[c.ordinal()][1]) + " " + ((Color)channels[c.ordinal()][2]).name()));
    config.appendChild(channelsE);
    return config;
  }

  @Override
  public void load(Element configE) {
    NodeList children = configE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("show_prompt"))
          showPrompt = Boolean.parseBoolean(((Text)e.getFirstChild()).getData());
        if (e.getTagName().equals("channels")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              for (int k = 0; k < Channel.values().length; k++)
                if (e2.getTagName().equals(Channel.values()[k].name())) {
                  String[] split = ((Text)e2.getFirstChild()).getData().split("\\s");
                  channels[k][1] = Boolean.parseBoolean(split[0]);
                  channels[k][2] = Color.valueOf(split[1]);
                }
            }
          }
        }
      }
    }
  }

  @Override
  public String getFilePath() {return null;}
}
