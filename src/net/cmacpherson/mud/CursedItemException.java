package net.cmacpherson.mud;

@SuppressWarnings("serial")
public class CursedItemException extends Exception {

  public Item cursed;
  public CursedItemException(Item cursed) {
    this.cursed = cursed;
  }
}
