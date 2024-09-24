package diacritics.owo.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import fabric.net.mca.entity.ai.Genetics;
import fabric.net.mca.entity.ai.Genetics.GeneType;

public record GeneticsValues(Map<GeneType, Float> values) {
  public static final Set<GeneType> GENOMES = genomes();

  public GeneticsValues() {
    this(new HashMap<>());
  }

  public void apply(Genetics genetics) {
    this.values.entrySet().forEach((gene) -> genetics.setGene(gene.getKey(), gene.getValue()));
  }

  public void update(Genetics genetics) {
    this.values.clear();
    GENOMES.stream().forEach((genome) -> {
      this.values.put(genome, genetics.getGene(genome));
    });
  }

  @SuppressWarnings("unchecked")
  public static final Set<GeneType> genomes() {
    try {
      Field field = Genetics.class.getDeclaredField("GENOMES");
      field.setAccessible(true);
      return (Set<GeneType>) field.get(null);
    } catch (Exception exception) {
      return null;
    }
  }
}
