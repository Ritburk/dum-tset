package net.cmacpherson.mud;

import java.util.ArrayList;

import net.cmacpherson.mud.Character.EQSlot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Item implements XML.Processable, Keywords {
  
  // TODO update forgotten item types
  public static final int TANGABLE =         0;
  public static final int KEY =              1;
  public static final int TRASH =            2;
  public static final int TREASURE =         3;
  public static final int GEM =              4;
  public static final int WEARABLE =         5;
  public static final int LIGHT =            6;
  public static final int ARMOR =            7;
  public static final int WEAPON =           8;
  public static final int BOW =              9;
  public static final int CONSUMABLE =      10;
  public static final int STAFF =           11;
  public static final int WAND =            12;
  public static final int ROD =             13;
  public static final int PILL =            14;
  public static final int POTION =          15;
  public static final int CONTAINER =       16;
  public static final int CORPSE =          17;
  public static final int PC_CORPSE =       18;
  public static final int STRUCTURE =       19;
  public static final int GATEWAY =         20;
  public static final int TWO_HANDED =      21;
  public static final int FOUNTAIN =        22;
  public static final int DRINK_CONTAINER = 23;
  public static final int AMMUNITION =      24;
  public static final int ARROW =           25;
  public static final int BOLT =            26;
  public static final int QUIVER =          27;
  public static final int CROSSBOW =        28;
  public static final int POISON =          29;
  public static final int SCROLL =          30;
  
  public static final String[] TYPE_NAMES = new String[] {
    "tangable",
    "key",
    "trash",
    "treasure",
    "gem",
    "wearable",
    "light",
    "armor",
    "weapon",
    "bow",
    "consumable",
    "staff",
    "wand",
    "rod",
    "pill",
    "potion",
    "container",
    "corpse",
    "pc_corpse",
    "structure",
    "gateway",
    "two_handed",
    "fountain",
    "drink_container",
    "ammunition",
    "arrow",
    "bolt",
    "quiver",
    "crossbow",
    "poison",
    "scroll",
    "liquid",
  };
  
  public static final int GLOWING =      0;
  public static final int MAGIC =        1;
  public static final int INVIS =        2;
  public static final int ANTI_GOOD =    3;
  public static final int ANTI_EVIL =    4;
  public static final int ANTI_NEUTRAL = 5;
  public static final int SHARP =        6;
  public static final int CURSED =       7;
  public static final int CLOSABLE =     8;
  
  public static final String[] FLAG_NAMES = new String[] {
    "glowing",
    "magic",
    "invis",
    "anti_good",
    "anti_evil",
    "anti_neutral",
    "sharp",
    "cursed",
    "closable",
  };
  
  public long vnum;
  public int resetID;
  public long id;
  public String name;
  public String longName;
  public String[] keywords;
  public ArrayList<String> description = new ArrayList<String>();
  public int type;
  public int flags;
  public int level;
  public double weight;
  public int dur;
  public int mDur;
  public long value;
//  public Material material;
  public Proficiencies.Name reqProficiency; 
  public int reqProficiencyLevel;
  public Slot slot;
  public Character.EQSlot eqSlot;
  public ArrayList<Affect> affects = new ArrayList<Affect>();
  public int modSTR;
  public int modDEX;
  public int modCON;
  public int modINT;
  public int modWIS;
  public int modCHA;
  public int modAC;
  public int modFORT;
  public int modREF;
  public int modWILL;
  public int modHP;
  public int modM;
  public int modMV;
  public int modHR;
  public int modDR;
  public int modSHR;
  public int modSDR;
  public int modARC;
  public int ac;
  public int modEAC;
  public Dice damage;
  public int modEHR;
  public int modEDR;
  public int modESHR;
  public int modESDR;
  public int sharpens;
  public int mSharpens;
  public int drawSTR;
  public Spell[] spells = new Spell[0];
  public int[][] charges;
  public long keyID;
  public int uses;
  public int duration;
  public long targetID;
  public Status status;
  public double mContainerWeight;
  public int mCapacity;
  public ArrayList<Item> contents = new ArrayList<Item>();
  public Poison poison;
  public Liquid liquid;
  public int ammoCount;
  
  @Override
  public boolean equals(Object obj) {
    return id == ((Item)obj).id;
  }
  
  public String[] keywords() {return keywords;}
  
  public boolean isEquipped() {return eqSlot != null;}
  
  public double weight() {
    double w = weight;
    if (is(1 << CONTAINER))
      for (Item item : contents)
        w += item.weight();
    return w;
  }
  public int capacity() {
    return contents.size();
  }
  
  public void addToContents(Item item) throws TooHeavyException, NoSpaceException {
    if (weight() + item.weight() > mContainerWeight)
      throw new TooHeavyException();
    if (capacity() + 1 > mCapacity)
      throw new NoSpaceException();
    contents.add(item);
  }
  
  public boolean isType(int flag) {return (type & (1 << flag)) != 0;}
  public void setType(int flag, boolean on) {
    if (on)
      type |= 1 << flag;
    else
      type &= ~(1 << flag);
  }
  public boolean is(int flag) {return (flags & (1 << flag)) != 0;}
  public void setFlag(int flag, boolean on) {
    if (on)
      flags |= 1 << flag;
    else
      flags &= ~(1 << flag);
  }
  
  @Override
  public Element compile(Document doc) {
    Element itemE = doc.createElement("item");
    itemE.appendChild(XML.cC(doc, "vnum", Long.toString(vnum)));
    itemE.appendChild(XML.cC(doc, "id", Long.toString(id)));
    itemE.appendChild(XML.cC(doc, "name", name));
    itemE.appendChild(XML.cC(doc, "long_name", longName));
    String line = "";
    for (String k : keywords)
      line += k + " ";
    itemE.appendChild(XML.cC(doc, "keywords", line.trim()));
    Element descE = doc.createElement("description");
    for (String line2 : description)
      descE.appendChild(XML.cC(doc, "line", line2));
    itemE.appendChild(descE);
    itemE.appendChild(XML.cC(doc, "type", Integer.toString(type)));
    itemE.appendChild(XML.cC(doc, "flags", Integer.toString(flags)));
    itemE.appendChild(XML.cC(doc, "level", Integer.toString(level)));
    itemE.appendChild(XML.cC(doc, "weight", Double.toString(weight)));
    itemE.appendChild(XML.cC(doc, "durability", Integer.toString(dur)));
    itemE.appendChild(XML.cC(doc, "max_durability", Integer.toString(mDur)));
    itemE.appendChild(XML.cC(doc, "value", Long.toString(value)));
    if (reqProficiency != null) {
      itemE.appendChild(XML.cC(doc, "required_proficiency", reqProficiency.name()));
      itemE.appendChild(XML.cC(doc, "required_proficiency_level", Integer.toString(reqProficiencyLevel)));
    }
//    if (material != null)
//      itemE.appendChild(XML.cC(doc, "material", material.name()));
    if (slot != null)
      itemE.appendChild(XML.cC(doc, "slot", slot.name()));
    if (eqSlot != null)
      itemE.appendChild(XML.cC(doc, "eq_slot", eqSlot.name()));
    if (affects.size() > 0) {
      Element e = doc.createElement("affects");
      for (Affect affect : affects)
        e.appendChild(affect.compile(doc));
      itemE.appendChild(e);
    }
    itemE.appendChild(XML.cC(doc, "mod_str", Integer.toString(modSTR)));
    itemE.appendChild(XML.cC(doc, "mod_con", Integer.toString(modCON)));
    itemE.appendChild(XML.cC(doc, "mod_dex", Integer.toString(modDEX)));
    itemE.appendChild(XML.cC(doc, "mod_int", Integer.toString(modINT)));
    itemE.appendChild(XML.cC(doc, "mod_wis", Integer.toString(modWIS)));
    itemE.appendChild(XML.cC(doc, "mod_cha", Integer.toString(modCHA)));
    itemE.appendChild(XML.cC(doc, "mod_ac", Integer.toString(modAC)));
    itemE.appendChild(XML.cC(doc, "mod_fortitude", Integer.toString(modFORT)));
    itemE.appendChild(XML.cC(doc, "mod_reflex", Integer.toString(modREF)));
    itemE.appendChild(XML.cC(doc, "mod_will", Integer.toString(modWILL)));
    itemE.appendChild(XML.cC(doc, "mod_hp", Integer.toString(modHP)));
    itemE.appendChild(XML.cC(doc, "mod_mana", Integer.toString(modM)));
    itemE.appendChild(XML.cC(doc, "mod_moves", Integer.toString(modMV)));
    itemE.appendChild(XML.cC(doc, "mod_hit_roll", Integer.toString(modHR)));
    itemE.appendChild(XML.cC(doc, "mod_dam_roll", Integer.toString(modDR)));
    itemE.appendChild(XML.cC(doc, "mod_spell_hit_roll", Integer.toString(modSHR)));
    itemE.appendChild(XML.cC(doc, "mod_spell_dam_roll", Integer.toString(modSDR)));
    itemE.appendChild(XML.cC(doc, "mod_archery", Integer.toString(modARC)));
    itemE.appendChild(XML.cC(doc, "ac", Integer.toString(ac)));
    itemE.appendChild(XML.cC(doc, "mod_enchanted_ac", Integer.toString(modEAC)));
    itemE.appendChild(XML.cC(doc, "damage", damage.toString()));
    itemE.appendChild(XML.cC(doc, "mod_enchanted_hit_roll", Integer.toString(modEHR)));
    itemE.appendChild(XML.cC(doc, "mod_enchanted_dam_roll", Integer.toString(modEDR)));
    itemE.appendChild(XML.cC(doc, "mod_enchanted_spell_hit_roll", Integer.toString(modESHR)));
    itemE.appendChild(XML.cC(doc, "mod_enchanted_spell_dam_roll", Integer.toString(modESDR)));
    itemE.appendChild(XML.cC(doc, "sharpens", Integer.toString(sharpens)));
    itemE.appendChild(XML.cC(doc, "max_sharpens", Integer.toString(mSharpens)));
    itemE.appendChild(XML.cC(doc, "draw_strength", Integer.toString(drawSTR)));
    itemE.appendChild(XML.cC(doc, "duration", Integer.toString(duration)));
    if (spells.length > 0) {
      Element e = doc.createElement("spells");
      for (Spell spell : spells)
        e.appendChild(spell.compile(doc));
      itemE.appendChild(e);
    }
    if (charges != null &&
        charges[0].length > 0) {
      Element e = doc.createElement("charges");
      for (int[] i : charges) {
        line = "";
        for (int j : i)
          line += j + " ";
        e.appendChild(XML.cC(doc, "charge", line));
      }
      itemE.appendChild(e);
    }
    if (contents.size() > 0) {
      Element e = doc.createElement("contents");
      for (Item item : contents)
        e.appendChild(item.compile(doc));
      itemE.appendChild(e);
    }
    itemE.appendChild(XML.cC(doc, "max_container_weight", Double.toString(mContainerWeight)));
    itemE.appendChild(XML.cC(doc, "max_capacity", Integer.toString(mCapacity)));
    if (status != null)
      itemE.appendChild(XML.cC(doc, "status", status.name()));
    itemE.appendChild(XML.cC(doc, "key_id", Long.toString(keyID)));
    itemE.appendChild(XML.cC(doc, "uses", Integer.toString(uses)));
    itemE.appendChild(XML.cC(doc, "target_id", Long.toString(targetID)));
    if (poison != null)
      itemE.appendChild(XML.cC(doc, "poison", poison.name()));
    if (liquid != null)
      itemE.appendChild(XML.cC(doc, "liquid", liquid.type.name()));
    itemE.appendChild(XML.cC(doc, "ammo_count", Integer.toString(ammoCount)));
    return itemE;
  }

  @Override
  public void load(Element itemE) {
    NodeList children = itemE.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child instanceof Element) {
        Element e = (Element)child;
        if (e.getTagName().equals("vnum"))
          vnum = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("id"))
          id = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("name"))
          name = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("long_name"))
          longName = ((Text)e.getFirstChild()).getData();
        else if (e.getTagName().equals("keywords"))
          keywords = ((Text)e.getFirstChild()).getData().split("\\s");
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
        } else if (e.getTagName().equals("type"))
          type = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("flags"))
          flags = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("level"))
          level = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("weight"))
          weight = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("durability"))
          dur = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_durability"))
          mDur = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("value"))
          value = Long.parseLong(((Text)e.getFirstChild()).getData());
//        else if (e.getTagName().equals("material"))
//          material = Material.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("required_proficiency"))
          reqProficiency = Proficiencies.Name.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("required_proficiency_level"))
          reqProficiencyLevel = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("slot"))
          slot = Slot.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("eq_slot"))
          eqSlot = EQSlot.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("affects")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("affect")) {
                Affect a = new Affect();
                a.load(e2);
                affects.add(a);
              }
            }
          }
        } else if (e.getTagName().equals("mod_str"))
          modSTR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_con"))
          modCON = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_dex"))
          modDEX = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_int"))
          modINT = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_wis"))
          modWIS = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_cha"))
          modCHA = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_ac"))
          modAC = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_fortitude"))
          modFORT = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_reflex"))
          modREF = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_will"))
          modWILL = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_hp"))
          modHP = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_mana"))
          modM = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_moves"))
          modMV = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_hit_roll"))
          modHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_dam_roll"))
          modDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_spell_hit_roll"))
          modSHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_spell_dam_roll"))
          modSDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_archery"))
          modARC = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("ac"))
          ac = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_enchanted_ac"))
          modEAC = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("damage"))
          try {
            damage = new Dice(((Text)e.getFirstChild()).getData());
          } catch (InvalidDiceFormatException ex) {}
        else if (e.getTagName().equals("mod_enchanted_hit_roll"))
          modEHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_enchanted_dam_roll"))
          modEDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_enchanted_spell_hit_roll"))
          modESHR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("mod_enchanted_spell_dam_roll"))
          modESDR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("sharpens"))
          sharpens = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_sharpens"))
          mSharpens = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("draw_strength"))
          drawSTR = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("duration"))
          duration = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("spells")) {
          ArrayList<Spell> temp = new ArrayList<Spell>();
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("spell")) {
                Spell spell = new Spell();
                spell.load(e2);
                temp.add(spell);
              }
            }
          }
          spells = temp.toArray(new Spell[] {});
        } else if (e.getTagName().equals("charges")) {
          ArrayList<int[]> temp = new ArrayList<int[]>();
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("charge")) {
                String[] temp2 = ((Text)e2.getFirstChild()).getData().split("\\s");
                int[] temp3 = new int[temp2.length];
                for (int k = 0; k < temp2.length; k++)
                  temp3[k] = Integer.parseInt(temp2[k]);
                temp.add(temp3);
              }
            }
          }
          charges = temp.toArray(new int[][] {});
        } else if (e.getTagName().equals("contents")) {
          NodeList children2 = e.getChildNodes();
          for (int j = 0; j < children2.getLength(); j++) {
            Node child2 = children2.item(j);
            if (child2 instanceof Element) {
              Element e2 = (Element)child2;
              if (e2.getTagName().equals("item")) {
                Item item = new Item();
                item.load(e2);
                contents.add(item);
              }
            }
          }
        } else if (e.getTagName().equals("max_container_weight"))
          mContainerWeight = Double.parseDouble(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("max_capacity"))
          mCapacity = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("status"))
          status = Status.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("key_id"))
          keyID = Long.parseLong(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("uses"))
          uses = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("target_id"))
          targetID = Integer.parseInt(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("poison"))
          poison = Poison.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("liquid"))
          liquid = Liquid.valueOf(((Text)e.getFirstChild()).getData());
        else if (e.getTagName().equals("ammo_count"))
          ammoCount = Integer.parseInt(((Text)e.getFirstChild()).getData());
      }
    }
  }

  @Override
  public String getFilePath() {return null;}
  
  public enum Slot {
    LIGHT,
    RING,
    NECK,
    BODY,
    HEAD,
    LEGS,
    FEET,
    HANDS,
    ARMS,
    ABOUT,
    WAIST,
    WRIST,
    OFFHAND,
    WIELD
  }
  
  public enum Status {
    OPEN,
    CLOSED,
    LOCKED
  }
  
  public enum Poison {
    HIGH_SINGLE_DAMAGE,
    LOW_LINGERING_DAMAGE
  }
  
//  public enum Material {
//    INSIGNIFICANT,
//    CLOTH,
//    LEATHER,
//    IRON,
//    WOOD
//  }
}