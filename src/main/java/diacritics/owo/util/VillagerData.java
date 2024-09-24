package diacritics.owo.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import diacritics.owo.config.Config.PresetModel;
import diacritics.owo.config.Config.PresetModel.GenderModel;
import diacritics.owo.config.Config.PresetModel.GeneticsModel;
import diacritics.owo.config.Config.PresetModel.HairModel;
import diacritics.owo.config.Config.PresetModel.TraitModel;
import diacritics.owo.mixin.GeneticsAccessor;
import fabric.net.mca.entity.VillagerLike;
import fabric.net.mca.entity.ai.Genetics;
import fabric.net.mca.entity.ai.Traits;
import fabric.net.mca.entity.ai.Genetics.GeneType;
import fabric.net.mca.entity.ai.Traits.Trait;
import fabric.net.mca.entity.ai.relationship.Gender;

public class VillagerData {
  public static final Set<GeneType> GENOMES = GeneticsAccessor.genomes();

  // we don't need the name

  private String clothing;
  private String hair;
  private float[] hairColor;

  private final Map<GeneType, Float> genetics;

  private final Set<Trait> traits;

  private Gender gender;

  public VillagerData() {
    this(null, null, null, new HashMap<>(), new HashSet<>(), null);
  }

  public VillagerData(VillagerLike<?> entity) {
    this();
    this.update(entity);
  }

  public VillagerData(String clothing, String hair, float[] hairColor,
      Map<GeneType, Float> genetics, Set<Trait> traits, Gender gender) {
    this.clothing = clothing;
    this.hair = hair;
    this.hairColor = hairColor;
    this.genetics = genetics;
    this.traits = traits;
    this.gender = gender;
  }

  public void apply(VillagerLike<?> entity) {
    entity.setClothes(this.clothing);
    entity.setHair(this.hair);
    entity.setHairDye(this.hairColor[0], this.hairColor[1], this.hairColor[2]);

    this.genetics.entrySet()
        .forEach((gene) -> entity.getGenetics().setGene(gene.getKey(), gene.getValue()));

    entity.getTraits().getTraits().forEach((trait) -> {
      if (!this.traits.contains(trait)) {
        entity.getTraits().removeTrait(trait);
      }
    });
    this.traits.forEach((trait) -> {
      entity.getTraits().addTrait(trait);
    });

    entity.getGenetics().setGender(this.gender);
  }

  public void update(VillagerLike<?> entity) {
    this.clothing = entity.getClothes();
    this.hair = entity.getHair();
    this.hairColor = entity.getHairDye();

    this.genetics.clear();
    GENOMES.stream().forEach((genome) -> {
      this.genetics.put(genome, entity.getGenetics().getGene(genome));
    });

    this.traits.clear();
    entity.getTraits().getTraits().forEach((trait) -> {
      this.traits.add(trait);
    });

    this.gender = entity.getGenetics().getGender();
  }

  public PresetModel toPreset() {
    return new PresetModel(this.clothing, new HairModel(this.hair, this.hairColor),
        new GeneticsModel(this.genetics.get(Genetics.SIZE), this.genetics.get(Genetics.WIDTH),
            this.genetics.get(Genetics.BREAST), this.genetics.get(Genetics.MELANIN),
            this.genetics.get(Genetics.HEMOGLOBIN), this.genetics.get(Genetics.EUMELANIN),
            this.genetics.get(Genetics.PHEOMELANIN), this.genetics.get(Genetics.SKIN),
            this.genetics.get(Genetics.FACE), this.genetics.get(Genetics.VOICE),
            this.genetics.get(Genetics.VOICE_TONE)),
        this.traits.stream().map((trait) -> switch (trait.id()) {
          case "LEFT_HANDED" -> TraitModel.LEFT_HANDED;
          case "WEAK" -> TraitModel.WEAK;
          case "TOUGH" -> TraitModel.TOUGH;
          case "COLOR_BLIND" -> TraitModel.COLOR_BLIND;
          case "HETEROCHROMIA" -> TraitModel.HETEROCHROMIA;
          case "LACTOSE_INTOLERANCE" -> TraitModel.LACTOSE_INTOLERANCE;
          case "COELIAC_DISEASE" -> TraitModel.COELIAC_DISEASE;
          case "DIABETES" -> TraitModel.DIABETES;
          case "DWARFISM" -> TraitModel.DWARFISM;
          case "ALBINISM" -> TraitModel.ALBINISM;
          case "VEGETARIAN" -> TraitModel.VEGETARIAN;
          case "BISEXUAL" -> TraitModel.BISEXUAL;
          case "HOMOSEXUAL" -> TraitModel.HOMOSEXUAL;
          case "ELECTRIFIED" -> TraitModel.ELECTRIFIED;
          case "SIRBEN" -> TraitModel.SIRBEN;
          case "RAINBOW" -> TraitModel.RAINBOW;
          case "UNKNOWN" -> TraitModel.UNKNOWN;
          default -> null;
        }).filter((trait) -> trait != null).toList().toArray(new TraitModel[] {}),
        switch (this.gender) {
          case FEMALE -> GenderModel.FEMININE;
          default -> GenderModel.MASCULINE;
        });
  }

  public static VillagerData fromPreset(PresetModel model) {
    return new VillagerData(model.clothing, model.hair.hair, model.hair.color,
        Map.ofEntries(Map.entry(Genetics.SIZE, model.genetics.size),
            Map.entry(Genetics.WIDTH, model.genetics.width),
            Map.entry(Genetics.BREAST, model.genetics.breast),
            Map.entry(Genetics.MELANIN, model.genetics.melanin),
            Map.entry(Genetics.HEMOGLOBIN, model.genetics.hemoglobin),
            Map.entry(Genetics.EUMELANIN, model.genetics.eumelanin),
            Map.entry(Genetics.PHEOMELANIN, model.genetics.pheomelanin),
            Map.entry(Genetics.SKIN, model.genetics.skin),
            Map.entry(Genetics.FACE, model.genetics.face),
            Map.entry(Genetics.VOICE, model.genetics.voice),
            Map.entry(Genetics.VOICE_TONE, model.genetics.voiceTone)),
        Arrays.stream(model.traits).map((trait) -> switch (trait) {
          case LEFT_HANDED -> Traits.LEFT_HANDED;
          case WEAK -> Traits.WEAK;
          case TOUGH -> Traits.TOUGH;
          case COLOR_BLIND -> Traits.COLOR_BLIND;
          case HETEROCHROMIA -> Traits.HETEROCHROMIA;
          case LACTOSE_INTOLERANCE -> Traits.LACTOSE_INTOLERANCE;
          case COELIAC_DISEASE -> Traits.COELIAC_DISEASE;
          case DIABETES -> Traits.DIABETES;
          case DWARFISM -> Traits.DWARFISM;
          case ALBINISM -> Traits.ALBINISM;
          case VEGETARIAN -> Traits.VEGETARIAN;
          case BISEXUAL -> Traits.BISEXUAL;
          case HOMOSEXUAL -> Traits.HOMOSEXUAL;
          case ELECTRIFIED -> Traits.ELECTRIFIED;
          case SIRBEN -> Traits.SIRBEN;
          case RAINBOW -> Traits.RAINBOW;
          case UNKNOWN -> Traits.UNKNOWN;
        }).collect(Collectors.toSet()), switch (model.gender) {
          case FEMININE -> Gender.FEMALE;
          case MASCULINE -> Gender.MALE;
        });
  }
}
