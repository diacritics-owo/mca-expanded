package diacritics.owo;

import org.jetbrains.annotations.NotNull;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import diacritics.owo.config.Config.Model;
import diacritics.owo.gui.CustomVillagerEditorScreen;
import diacritics.owo.util.VillagerData;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class McaExpandedModMenu implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return new ConfigScreenFactory<McaExpandedConfigScreen>() {
      @Override
      public McaExpandedConfigScreen create(Screen parent) {
        return new McaExpandedConfigScreen(parent);
      }
    };
  }

  public static class McaExpandedConfigScreen extends BaseOwoScreen<FlowLayout> {
    public final Screen parent;

    public McaExpandedConfigScreen(Screen parent) {
      this.parent = parent;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
      return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
      boolean inWorld = this.client.world != null;

      rootComponent.surface(inWorld ? Surface.VANILLA_TRANSLUCENT : Surface.OPTIONS_BACKGROUND)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.CENTER);

      FlowLayout parent = Containers.verticalFlow(Sizing.content(), Sizing.content());

      parent.child(Components.button(Text.translatable("gui.mca-expanded.config.destiny",
          McaExpanded.CONFIG.read().destiny ? "Enabled" : "Disabled"), (button) -> {
            Model config = McaExpanded.CONFIG.read();
            config.destiny = !config.destiny;
            McaExpanded.CONFIG.write(config);

            button.setMessage(Text.translatable("gui.mca-expanded.config.destiny",
                McaExpanded.CONFIG.read().destiny ? "Enabled" : "Disabled"));
          })).child(Components.label(Text.literal(" ")));

      if (!inWorld) {
        parent.child(Components.label(
            Text.translatable("gui.mca-expanded.world-required").formatted(Formatting.BLACK)));
      } else {
        parent.child(Components.button(Text.translatable("gui.mca-expanded.config.edit-preset"),
            (button) -> {
              VillagerData data = new VillagerData();

              Screen screen = new CustomVillagerEditorScreen(this, this.client.player.getUuid(),
                  this.client.player.getUuid()) {
                @Override
                protected void setPage(String page) {
                  boolean loaded = this.page != null && this.page.equals("loading");
                  super.setPage(page);

                  if (loaded) {
                    data.update(this.villager);
                    VillagerData.fromPreset(McaExpanded.CONFIG.read().preset).apply(this.villager);
                  }
                }

                @Override
                public void close() {
                  Model config = McaExpanded.CONFIG.read();
                  config.preset = new VillagerData(this.villager).toPreset();
                  McaExpanded.CONFIG.write(config);

                  data.apply(this.villager);
                  this.syncVillagerData();

                  super.close();
                }
              };

              this.client.setScreen(screen);
            }));
      }

      rootComponent.child(parent.padding(Insets.of(10)).surface(Surface.PANEL)
          .verticalAlignment(VerticalAlignment.CENTER)
          .horizontalAlignment(HorizontalAlignment.CENTER));
    }

    @Override
    public void close() {
      this.client.setScreen(this.parent);
    }
  }
}
