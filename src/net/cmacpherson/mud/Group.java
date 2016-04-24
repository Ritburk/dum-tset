package net.cmacpherson.mud;

import java.util.ArrayList;

public class Group {

  public Character leader;
  public ArrayList<Character> members = new ArrayList<Character>();
  
  public Group(Character leader) {
    this.leader = leader;
    members.add(leader);
  }
}
