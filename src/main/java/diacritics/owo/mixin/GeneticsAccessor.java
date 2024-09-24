package diacritics.owo.mixin;

import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import fabric.net.mca.entity.ai.Genetics;
import fabric.net.mca.entity.ai.Genetics.GeneType;

@Mixin(value = Genetics.class, remap = false)
public interface GeneticsAccessor {
  @Accessor(value = "GENOMES")
  public static Set<GeneType> genomes() {
    return null;
  }
}
