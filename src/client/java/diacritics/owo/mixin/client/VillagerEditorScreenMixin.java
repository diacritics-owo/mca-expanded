package diacritics.owo.mixin.client;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import diacritics.owo.McaExpanded;
import diacritics.owo.config.Config.Model;
import diacritics.owo.util.ClientHelpers;
import diacritics.owo.util.VillagerData;
import fabric.net.mca.client.gui.VillagerEditorScreen;
import fabric.net.mca.entity.VillagerEntityMCA;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.UIErrorToast;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Mixin(value = VillagerEditorScreen.class, remap = false)
public abstract class VillagerEditorScreenMixin extends Screen {
  @Shadow
  protected final VillagerEntityMCA villager;

  @Shadow
  abstract public void syncVillagerData();

  @Shadow
  private void requestVillagerData() {};

  @Shadow
  protected String page;

  @Shadow
  abstract protected void setPage(String page);

  protected VillagerEditorScreenMixin(Text title) {
    super(title);
    this.villager = null;
  }

  @Inject(at = @At("TAIL"), method = "setPage")
  protected void setPage(String page, CallbackInfo info) {
    if (page.equals("clothing") || page.equals("hair"))
      return;

    int width = 135 / 2;
    int height = 20;

    int x = this.width / 2 - 175 + 20;
    int y = this.height / 2 - 85;

    ButtonComponent presetListButton =
        ClientHelpers.presetListButton(this, this.uiAdapter.rootComponent, () -> {
          this.syncVillagerData();
        }, Optional.of((preset) -> {
          Model config = McaExpanded.CONFIG.read();
          VillagerData.fromPreset(config.presets.get(preset)).apply(this.villager);
          this.syncVillagerData();
          this.requestVillagerData();
        }));

    presetListButton.sizing(Sizing.fixed(width), Sizing.fixed(height));
    presetListButton.positioning(Positioning.absolute(x, y));

    this.uiAdapter.rootComponent.child(presetListButton);

    this.addDrawableChild(
        ButtonWidget.builder(Text.translatable("gui.mca-expanded.button.export"), (button) -> {
          Optional<NativeImage> result = Stream
              .of(ClientHelpers.readImage(ClientHelpers.SKIN.getColor(this.villager, 0),
                  ClientHelpers.SKIN.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.FACE.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.CLOTHING.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.HAIR.getColor(this.villager, 0),
                      ClientHelpers.HAIR.getSkin(this.villager)))
              .filter((image) -> image != null).reduce((a, b) -> {
                NativeImage image = new NativeImage(64, 64, true);

                for (int i = 0; i < image.getWidth(); i++) {
                  for (int j = 0; j < image.getHeight(); j++) {
                    image.setColor(i, j, a.getColor(i, j));
                    image.blend(i, j, b.getColor(i, j));
                  }
                }

                return image;
              });

          if (result.isPresent()) {
            try {
              File output = File.createTempFile("mca", ".png");
              result.get().writeTo(output);
              Util.getOperatingSystem().open(output);
            } catch (IOException exception) {
              McaExpanded.LOGGER.error("failed to create and write to temp file", exception);
            }
          } else {
            McaExpanded.LOGGER.error("failed to export image (optional was empty)");
          }
        }).dimensions(x + width, y, width, height)
            .tooltip(Tooltip.of(Text.translatable("gui.mca-expanded.button.export.tooltip")))
            .build());
  }

  // owo-ui things :3

  public OwoUIAdapter<FlowLayout> uiAdapter = null;
  public boolean invalid = false;

  public @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(this, Containers::verticalFlow);
  };

  public void build(FlowLayout rootComponent) {};

  @Inject(method = "init", at = @At("HEAD"))
  public void init(CallbackInfo info) {
    if (this.invalid)
      return;

    if (this.uiAdapter != null) {
      this.uiAdapter.moveAndResize(0, 0, this.width, this.height);
      this.addDrawableChild(this.uiAdapter);
    } else {
      try {
        this.uiAdapter = this.createAdapter();
        this.build(this.uiAdapter.rootComponent);
        this.uiAdapter.inflateAndMount();
      } catch (Exception error) {
        Owo.LOGGER.warn("Could not initialize owo screen", error);
        UIErrorToast.report(error);
        this.invalid = true;
      }
    }
  }

  public <C extends Component> @Nullable C component(Class<C> expectedClass, String id) {
    return this.uiAdapter.rootComponent.childById(expectedClass, id);
  }

  @WrapMethod(method = "render")
  public void render(DrawContext context, int mouseX, int mouseY, float delta,
      Operation<Void> original) {
    if (!this.invalid) {
      original.call(context, mouseX, mouseY, delta);
    } else {
      this.close();
    }
  }

  @Override
  public void removed() {
    if (this.uiAdapter != null)
      this.uiAdapter.dispose();
  }

  @Override
  public void clearChildren() {
    if (this.uiAdapter != null) {
      this.uiAdapter.rootComponent.clearChildren();
      super.remove(this.uiAdapter);
    }

    super.clearChildren();

    if (this.uiAdapter != null) {
      this.addDrawableChild(this.uiAdapter);
    }
  }
}
