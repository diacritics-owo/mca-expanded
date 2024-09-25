package diacritics.owo;

import org.jetbrains.annotations.NotNull;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import diacritics.owo.util.ClientHelpers;
import diacritics.owo.util.Translations;
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

      parent.child(ClientHelpers.destinyButton().margins(Insets.bottom(ClientHelpers.PADDING_B)));

      if (!inWorld) {
        parent.child(
            Components.label(Translations.WORLD_REQUIRED.copy().formatted(Formatting.BLACK)));
      } else {
        parent.child(ClientHelpers.presetListButton(this, rootComponent));
      }

      rootComponent.child(parent.padding(Insets.of(ClientHelpers.PADDING_A)).surface(Surface.PANEL)
          .verticalAlignment(VerticalAlignment.CENTER)
          .horizontalAlignment(HorizontalAlignment.CENTER));
    }

    @Override
    public void close() {
      this.client.setScreen(this.parent);
    }
  }
}
