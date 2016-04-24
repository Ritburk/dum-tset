package net.cmacpherson.mud;

public interface Keywords {
  //come up with a better class name to describe what this does.
  //this is an interface to allow classes like Character and
  //Item to be searchable by supplying their keywords in order
  //to contain them all within one searchable list
  public String[] keywords();
}
