package diacritics.owo.config;

import java.util.Map;
import java.util.UUID;
import io.wispforest.owo.config.annotation.Config;

@Config(name = "mca-expanded", wrapperName = "McaExpandedConfig")
public class ConfigModel implements Cloneable {
  public static ConfigModel defaultValue() {
    return new ConfigModel();
  }

  public boolean destiny;
  public Map<String, PresetModel> presets;

  public ConfigModel(boolean destiny, Map<String, PresetModel> presets) {
    this.destiny = destiny;
    this.presets = presets;
  }

  public ConfigModel() {
    this(true, Map.of(UUID.randomUUID().toString(), PresetModel.defaultValue()));
  }

  public static class PresetModel {
    public static PresetModel defaultValue() {
      return new PresetModel();
    }

    public String presetName;
    public String clothing;
    public String hair;
    public TraitModel[] traits;
    public GenderModel gender;
    public float size;
    public float width;
    public float breast;
    public float melanin;
    public float hemoglobin;
    public float eumelanin;
    public float pheomelanin;
    public float skin;
    public float face;
    public float voice;
    public float voiceTone;

    public PresetModel(String presetName, String clothes, String hair, TraitModel[] traits,
        GenderModel gender, float size, float width, float breast, float melanin, float hemoglobin,
        float eumelanin, float pheomelanin, float skin, float face, float voice, float voiceTone) {
      this.presetName = presetName;
      this.clothing = clothes;
      this.hair = hair;
      this.traits = traits;
      this.gender = gender;
      this.size = size;
      this.width = width;
      this.breast = breast;
      this.melanin = melanin;
      this.hemoglobin = hemoglobin;
      this.eumelanin = eumelanin;
      this.pheomelanin = pheomelanin;
      this.skin = skin;
      this.face = face;
      this.voice = voice;
      this.voiceTone = voiceTone;
    }

    public PresetModel() {
      this("Preset", "mca:skins/clothing/normal/neutral/none/0.png", "mca:skins/hair/male/25.png",
          new TraitModel[0], GenderModel.MASCULINE, 0.5f, 0.5f, 0.5f, 0.33f, 0.33f, 0.33f, 1, 1, 0,
          0.5f, 0.5f);
    }

    public static enum TraitModel {
      LEFT_HANDED, //
      WEAK, //
      TOUGH, //
      COLOR_BLIND, //
      HETEROCHROMIA, //
      LACTOSE_INTOLERANCE, //
      COELIAC_DISEASE, //
      DIABETES, //
      DWARFISM, //
      ALBINISM, //
      VEGETARIAN, //
      BISEXUAL, //
      HOMOSEXUAL, //
      ELECTRIFIED, //
      SIRBEN, //
      RAINBOW, //
      UNKNOWN;
    }

    // no 'other' option since mca doesn't support it
    public static enum GenderModel {
      MASCULINE, //
      FEMININE;
    }
  }
}
