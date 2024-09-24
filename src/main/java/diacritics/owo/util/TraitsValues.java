package diacritics.owo.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import fabric.net.mca.entity.ai.Genetics;
import fabric.net.mca.entity.ai.Traits;
import fabric.net.mca.entity.ai.Genetics.GeneType;
import fabric.net.mca.entity.ai.Traits.Trait;

public record TraitsValues(Set<Trait> values) {
  public static final Set<GeneType> GENOMES = genomes();

  public TraitsValues() {
    this(new HashSet<>());
  }

  public void apply(Traits traits) {
    traits.getTraits().forEach((trait) -> {
      if (!this.values.contains(trait)) {
        traits.removeTrait(trait);
      }
    });

    this.values.forEach((trait) -> {
      traits.addTrait(trait);
    });
  }

  public void update(Traits traits) {
    this.values.clear();
    traits.getTraits().forEach((trait) -> {
      this.values.add(trait);
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
