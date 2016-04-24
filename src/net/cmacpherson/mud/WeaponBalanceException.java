package net.cmacpherson.mud;

@SuppressWarnings("serial")
public class WeaponBalanceException extends Exception {

  public Item other;
  
  public WeaponBalanceException(Item other) {
    this.other = other;
  }
}
