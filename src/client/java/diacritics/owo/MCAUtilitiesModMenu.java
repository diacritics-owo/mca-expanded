package diacritics.owo;

import org.jetbrains.annotations.NotNull;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
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

    @Override
    protected void build(FlowLayout rootComponent) {
      Screen _this = this;

      rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.CENTER);

      FlowLayout parent = Containers.verticalFlow(Sizing.content(), Sizing.content());

      if (this.client.world != null) {
        // TODO: this bypasses permissions
        parent.child(Components.button(Text.literal("Edit"), button -> {
          this.client.setScreen(
              new VillagerEditorScreen(this.client.player.getUuid(), this.client.player.getUuid()) {
                @Override
                public void close() {
                  this.client.setScreen(_this);
                }
              });
        })).child(Components.button(Text.literal("Edit presets"), button -> {
          // TODO: name
          VillagerData data = new VillagerData();

          Screen screen =
              new VillagerEditorScreen(this.client.player.getUuid(), this.client.player.getUuid()) {
                @Override
                protected void setPage(String page) {
                  boolean loaded = this.page == "loading";
                  super.setPage(page);

                  if (loaded) {
                    data.update(this.villager);
                    VillagerData.fromModel(MCAUtilities.CONFIG.read()).apply(this.villager);;
                  }
                }

                @Override
                public void close() {
                  MCAUtilities.CONFIG.write(new VillagerData(this.villager).toModel());

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
