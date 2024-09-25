package diacritics.owo.util;

import java.util.function.Function;
import net.minecraft.text.Text;

public class Translations {
  public static final Text EDIT_PRESET = of("gui.mca-expanded.config.preset.edit");
  public static final Text REMOVE_PRESET = of("gui.mca-expanded.config.preset.remove");
  public static final Text USE_PRESET = of("gui.mca-expanded.config.preset.use");
  public static final Text CREATE_PRESET = of("gui.mca-expanded.config.preset.create");
  public static final Function<Object, Text> EDITING_PRESET = ofParameterized("gui.mca-expanded.config.preset.editing");

  public static final Text PRESETS = of("gui.mca-expanded.config.presets");
  public static final Text EDIT_PRESETS = of("gui.mca-expanded.config.presets.edit");

  public static final Text WORLD_REQUIRED = of("gui.mca-expanded.config.world_required");

  public static final Function<Object, Text> DESTINY =
      ofParameterized("gui.mca-expanded.config.destiny");

  public static final Text EXPORT = of("gui.mca-expanded.skin.export");
  public static final Text EXPORT_TOOLTIP = of("gui.mca-expanded.skin.export.tooltip");

  public static Text of(String translationKey) {
    return Text.translatable(translationKey);
  }

  public static Function<Object, Text> ofParameterized(String translationKey) {
    return (arg) -> Text.translatable(translationKey, arg);
  }
}
