package diacritics.owo;

import org.jetbrains.annotations.NotNull;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import diacritics.owo.config.Config.Model;
import diacritics.owo.util.VillagerData;
import fabric.net.mca.client.gui.VillagerEditorScreen;
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

public class MCAUtilitiesModMenu implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return new ConfigScreenFactory<MCAUtilitiesConfigScreen>() {
      @Override
      public MCAUtilitiesConfigScreen create(Screen parent) {
        return new MCAUtilitiesConfigScreen(parent);
      }
    };
  }

  public static class MCAUtilitiesConfigScreen extends BaseOwoScreen<FlowLayout> {
    public final Screen parent;

    public MCAUtilitiesConfigScreen(Screen parent) {
      this.parent = parent;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
      return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    // TODO: using a library closes the screen
    @Override
    protected void build(FlowLayout rootComponent) {
      Screen _this = this;

      rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.CENTER);

      FlowLayout parent = Containers.verticalFlow(Sizing.content(), Sizing.content());

      if (this.client.world == null) {
        parent.child(Components.label(
            Text.translatable("gui.mca-utilities.world-required").formatted(Formatting.RED)));
      } else {
        // TODO: allows bypassing permissions (see command::editor in mca)
        parent.child(Components.button(Text.translatable("gui.mca-utilities.button.use-preset"),
            (button) -> {
              Screen screen = new VillagerEditorScreen(this.client.player.getUuid(),
                  this.client.player.getUuid()) {
                @Override
                protected void setPage(String page) {
                  boolean loaded = this.page == "loading";
                  super.setPage(page);

                  if (loaded) {
                    VillagerData.fromPreset(MCAUtilities.CONFIG.read().preset).apply(this.villager);
                  }
                }

                @Override
                public void close() {
                  this.client.setScreen(_this);
                }
              };

              this.client.setScreen(screen);
            })).child(Components.label(Text.literal(" "))).child(Components
                .button(Text.translatable("gui.mca-utilities.button.edit-preset"), (button) -> {
                  VillagerData data = new VillagerData();

                  Screen screen = new VillagerEditorScreen(this.client.player.getUuid(),
                      this.client.player.getUuid()) {
                    @Override
                    protected void setPage(String page) {
                      boolean loaded = this.page == "loading";
                      super.setPage(page);

                      if (loaded) {
                        data.update(this.villager);
                        VillagerData.fromPreset(MCAUtilities.CONFIG.read().preset)
                            .apply(this.villager);
                      }
                    }

                    @Override
                    public void close() {
                      Model config = MCAUtilities.CONFIG.read();
                      config.preset = new VillagerData(this.villager).toPreset();
                      MCAUtilities.CONFIG.write(config);

                      data.apply(this.villager);
                      this.syncVillagerData();

                      this.client.setScreen(_this);
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
