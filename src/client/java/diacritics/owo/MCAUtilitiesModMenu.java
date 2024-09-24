package diacritics.owo;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class MCAUtilitiesModMenu implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return new MCAUtilitiesModMenuConfigScreenFactory();
  }

  public static class MCAUtilitiesModMenuConfigScreenFactory
      implements ConfigScreenFactory<MCAUtilitiesConfigScreen> {
    @Override
    public MCAUtilitiesConfigScreen create(Screen parent) {
      return new MCAUtilitiesConfigScreen(parent);
    }
  }

  public static class MCAUtilitiesConfigScreen extends Screen {
    public Screen parent;

    protected MCAUtilitiesConfigScreen(Screen parent) {
      super(Text.literal("gui.mca-utilities.config.title"));
      this.parent = parent;
    }

    @Override
    protected void init() {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      this.renderBackground(context);
    }

    @Override
    public void close() {
      this.client.setScreen(this.parent);
    }
  }
}
