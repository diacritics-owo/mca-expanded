package diacritics.owo.mixin.client;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import diacritics.owo.McaExpanded;
import diacritics.owo.util.ClientHelpers;
import diacritics.owo.util.VillagerData;
import fabric.net.mca.client.gui.VillagerEditorScreen;
import fabric.net.mca.entity.VillagerEntityMCA;
import fabric.net.mca.util.compat.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
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

    this.addDrawableChild(new ButtonWidget(x, y, width, height,
        Text.translatable("gui.mca-expanded.button.use-preset"), (button) -> {
          VillagerData.fromPreset(McaExpanded.CONFIG.read().preset).apply(this.villager);
          this.syncVillagerData();
          this.requestVillagerData();
        }));

    this.addDrawableChild(new ButtonWidget(x + width, y, width, height,
        Text.translatable("gui.mca-expanded.button.export"), (button) -> {
          // TODO: coloring doesn't work with hair from the library
          Optional<NativeImage> result = Stream
              .of(ClientHelpers.readImage(ClientHelpers.SKIN.getColor(this.villager, 0),
                  ClientHelpers.SKIN.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.FACE.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.CLOTHING.getSkin(this.villager)),
                  ClientHelpers.readImage(ClientHelpers.HAIR.getColor(this.villager, 0),
                      ClientHelpers.HAIR.getSkin(this.villager)))
              .filter((image) -> image != null).reduce((a, b) -> {
                // TODO: verify size
                NativeImage image = new NativeImage(64, 64, true);

                for (int i = 0; i < image.getWidth(); i++) {
                  for (int j = 0; j < image.getHeight(); j++) {
                    image.setColor(i, j, a.getColor(i, j));
                    image.blend(i, j, b.getColor(i, j)); // TODO: does blending always work?
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
        }));
  }
}
