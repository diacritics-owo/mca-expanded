package diacritics.owo.util;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.Nullable;
import diacritics.owo.McaExpanded;
import diacritics.owo.config.Config.Model;
import diacritics.owo.gui.CustomVillagerEditorScreen;
import fabric.net.mca.client.gui.immersive_library.SkinCache;
import fabric.net.mca.client.gui.immersive_library.types.LiteContent;
import fabric.net.mca.client.render.layer.ClothingLayer;
import fabric.net.mca.client.render.layer.FaceLayer;
import fabric.net.mca.client.render.layer.HairLayer;
import fabric.net.mca.client.render.layer.SkinLayer;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper.Abgr;

public class ClientHelpers {
  public static final SkinLayer<LivingEntity, BipedEntityModel<LivingEntity>> SKIN =
      new SkinLayer<>(null, null);
  public static final FaceLayer<LivingEntity, BipedEntityModel<LivingEntity>> FACE =
      new FaceLayer<>(null, null, "normal");
  public static final ClothingLayer<LivingEntity, BipedEntityModel<LivingEntity>> CLOTHING =
      new ClothingLayer<>(null, null, "normal");
  public static final HairLayer<LivingEntity, BipedEntityModel<LivingEntity>> HAIR =
      new HairLayer<>(null, null);

  public static NativeImage readImage(Identifier identifier) {
    if (identifier.getNamespace().equals("immersive_library")) {
      try {
        return SkinCache
            .getImage(
                new LiteContent(Integer.valueOf(identifier.getPath()), 0, null, 0, null, null, 0))
            .get();
      } catch (NoSuchElementException | NumberFormatException exception) {
        McaExpanded.LOGGER.error("this should never have happened but it did so", exception);
        return null;
      }
    }

    Resource resource =
        MinecraftClient.getInstance().getResourceManager().getResource(identifier).orElse(null);

    if (resource == null) {
      return null;
    }

    try {
      NativeImage image = NativeImage.read(resource.getInputStream());
      return image;
    } catch (IOException exception) {
      McaExpanded.LOGGER.error("encountered an error while reading {}", identifier, exception);
      return null;
    }
  }

  @Nullable
  public static NativeImage readImage(float[] tint, Identifier identifier) {
    NativeImage image = readImage(identifier);

    if (image != null) {
      image.apply((abgr) -> Abgr.getAbgr(Abgr.getAlpha(abgr), (int) (tint[2] * Abgr.getBlue(abgr)),
          (int) (tint[1] * Abgr.getGreen(abgr)), (int) (tint[0] * Abgr.getRed(abgr))));
    }

    return image;
  }

  public static ButtonComponent destinyButton() {
    return Components.button(Text.translatable("gui.mca-expanded.config.destiny",
        McaExpanded.CONFIG.read().destiny ? "Enabled" : "Disabled"), (button) -> {
          Model config = McaExpanded.CONFIG.read();
          config.destiny = !config.destiny;
          McaExpanded.CONFIG.write(config);

          button.setMessage(Text.translatable("gui.mca-expanded.config.destiny",
              McaExpanded.CONFIG.read().destiny ? "Enabled" : "Disabled"));
        });
  }

  public static ButtonComponent editPresetButton(Screen parent) {
    return editPresetButton(parent, false);
  }

  public static ButtonComponent editPresetButton(Screen parent, boolean drawBackground) {
    MinecraftClient client = MinecraftClient.getInstance();
    return Components.button(Text.translatable("gui.mca-expanded.config.edit-preset"), (button) -> {
      VillagerData data = new VillagerData();

      CustomVillagerEditorScreen screen =
          new CustomVillagerEditorScreen(parent, client.player.getUuid(), client.player.getUuid()) {
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

            @Override
            public void renderBackground(DrawContext context) {
              if (drawBackground) {
                this.renderBackgroundTexture(context);
              } else {
                super.renderBackground(context);
              }
            }
          };

      client.setScreen(screen);
    });
  }
}
