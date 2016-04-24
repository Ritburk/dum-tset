package net.cmacpherson.mud;

public class ForestAlgorithm implements Generator.Algorithm {

  @Override
  public Room[][][] generate(Generator.Options opts) {
    //TODO finish algorithm
    
    return null;
  }
  
  public class ForestOptions implements Generator.Options {
    @Override
    public Class<?> getAlgorithmClass() {
      return ForestAlgorithm.class;
    }
    
    //TODO vars needed for algorithm
  }
}
