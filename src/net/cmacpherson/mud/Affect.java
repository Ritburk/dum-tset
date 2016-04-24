package net.cmacpherson.mud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Affect implements XML.Processable {

  public String cause;
  public Type type;
  public int duration;
  public int mod;
  public long itemID;
  
  public void equipOn(Character c) {
    c.affects.add(this);
    switch (type) {
    case STR:
      c.cSTR += mod;
      break;
    case DEX:
      c.cDEX += mod;
      break;
    case CON:
      c.cCON += mod;
      break;
    case INT:
      c.cINT += mod;
      break;
    case WIS:
      c.cWIS += mod;
      break;
    case CHA:
      c.cCHA += mod;
      break;
    case HP:
      c.mhp += mod;
      break;
    case M:
      c.mm += mod;
      break;
    case MV:
      c.mmv += mod;
      break;
    case HP_REGEN:
      c.hpRegen += mod;
      break;
    case M_REGEN:
      c.mRegen += mod;
      break;
    case MV_REGEN:
      c.mvRegen += mod;
      break;
    case HR:
      c.hr += mod;
      break;
    case DR:
      c.dr += mod;
      break;
    case SHR:
      c.shr += mod;
      break;
    case SDR:
      c.sdr += mod;
      break;
    case AC:
      c.ac += mod;
      break;
    case FORT:
      c.fort += mod;
      break;
    case REF:
      c.ref += mod;
      break;
    case WILL:
      c.will += mod;
      break;
    default:
    }
  }
  
  public void equipOff(Character c) {
    for (int i = 0; i < c.affects.size(); i++)
      if (itemID == c.affects.get(i).itemID &&
          type == c.affects.get(i).type) {
        c.affects.remove(i);
        break;
      }
    switch (type) {
    case STR:
      c.cSTR -= mod;
      break;
    case DEX:
      c.cDEX -= mod;
      break;
    case CON:
      c.cCON -= mod;
      break;
    case INT:
      c.cINT -= mod;
      break;
    case WIS:
      c.cWIS -= mod;
      break;
    case CHA:
      c.cCHA -= mod;
      break;
    case HP:
      c.mhp -= mod;
      break;
    case M:
      c.mm -= mod;
      break;
    case MV:
      c.mmv -= mod;
      break;
    case HP_REGEN:
      c.hpRegen -= mod;
      break;
    case M_REGEN:
      c.mRegen -= mod;
      break;
    case MV_REGEN:
      c.mvRegen -= mod;
      break;
    case HR:
      c.hr -= mod;
      break;
    case DR:
      c.dr -= mod;
      break;
    case SHR:
      c.shr -= mod;
      break;
    case SDR:
      c.sdr -= mod;
      break;
    case AC:
      c.ac -= mod;
      break;
    case FORT:
      c.fort -= mod;
      break;
    case REF:
      c.ref -= mod;
      break;
    case WILL:
      c.will -= mod;
      break;
    default:
    }
  }
  
  @Override
  public String toString() {
    //TODO override toString for displaying affect
    return super.toString();
  }

  @Override
  public Element compile(Document doc) {
    Element aff = doc.createElement("affect");
    aff.appendChild(XML.cC(doc, "cause", cause));
    aff.appendChild(XML.cC(doc, "type", type.name()));
    aff.appendChild(XML.cC(doc, "duration", Integer.toString(duration)));
    aff.appendChild(XML.cC(doc, "mod", Integer.toString(mod)));
    aff.appendChild(XML.cC(doc, "item_id", Long.toString(itemID)));
    return aff;
  }

  @Override
  public void load(Element affectE) {
    NodeList children = affectE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("cause"))
          cause = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("type"))
          type = Type.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("duration"))
          duration = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod"))
          mod = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("item_id"))
          itemID = Long.parseLong(((Text)e.getFirstChild()).getData());
      }
    }
  }

  @Override
  public String getFilePath() {return null;}

  public enum Type {
    STR,
    DEX,
    CON,
    INT,
    WIS,
    CHA,
    HP,
    M,
    MV,
    HP_REGEN,
    M_REGEN,
    MV_REGEN,
    HR,
    DR,
    SHR,
    SDR,
    AC,
    FORT,
    REF,
    WILL,
    
    POISON,
    SNEAK,
    HIDDEN,
    INVIS,
    INFRAVISION,
    DETECT_HIDDEN,
    DETECT_INVIS,
    DETECT_MAGIC,
    DETECT_POISON,
    DETECT_ALIGNMENT,
    
    LIGHT,
    LEVITATE,
    GASEOUS,
    SLOW,
    HASTE,
    STOP,
    CHARMED,
    DEAF,
    BLIND,
    SLEEP,
    CALM,
    RAGE,
    ENSNARE,
    WATER_BREATHING,
  }
}
