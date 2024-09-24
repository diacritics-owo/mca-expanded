package diacritics.owo.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import diacritics.owo.MCAUtilities;
import net.fabricmc.loader.api.FabricLoader;

public class Config {
  private Gson gson = new Gson();
  private String filename;

  public Config(String filename) {
    this.filename = filename;
  }

  public String filename() {
    return this.filename;
  }

  public Path path() {
    return FabricLoader.getInstance().getConfigDir().resolve(this.filename + ".json");
  }

  public Boolean exists() {
    return Files.exists(this.path());
  }

  public Model read() {
    if (!this.exists()) {
      MCAUtilities.LOGGER.info(
          "could not find a configuration file, so the default value will be written to {}",
          this.path());
      this.write(Model.DEFAULT);
    }

    try {
      return gson.fromJson(Files.readString(this.path()), Model.class);
    } catch (IOException exception) {
      MCAUtilities.LOGGER.error(
          "encountered an error while reading the configuration, so the default value will be used",
          exception);
      return Model.DEFAULT;
    }
  }

  public void write(Model value) {
    try {
      this.path().getParent().toFile().mkdirs();
      Files.writeString(this.path(), this.gson.toJson(value));
    } catch (IOException error) {
      MCAUtilities.LOGGER.error("failed to write configuration to {}!", this.path(), error);
    }
  }

  public static class Model {
    public static final Model DEFAULT = new Model("", new HairModel("", new float[] {0, 0, 0}),
        new GeneticsModel(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), new TraitModel[0],
        GenderModel.MASCULINE);

    public String clothing;
    public HairModel hair;
    public GeneticsModel genetics;
    public TraitModel[] traits;
    public GenderModel gender;

    public Model(String clothes, HairModel hair, GeneticsModel genetics, TraitModel[] traits,
        GenderModel gender) {
      this.clothing = clothes;
      this.hair = hair;
      this.genetics = genetics;
      this.traits = traits;
      this.gender = gender;
    }

    public static class HairModel {
      public String hair;
      public float[] color;

      public HairModel(String hair, float[] color) {
        this.hair = hair;
        this.color = color;
      }
    }

    public static class GeneticsModel {
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

      public GeneticsModel(float size, float width, float breast, float melanin, float hemoglobin,
          float eumelanin, float pheomelanin, float skin, float face, float voice,
          float voiceTone) {
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
