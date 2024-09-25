package diacritics.owo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import diacritics.owo.McaExpanded;
import diacritics.owo.config.Config.Model;
import diacritics.owo.config.Config.PresetModel;
import diacritics.owo.gui.CustomVillagerEditorScreen;
import fabric.net.mca.client.gui.immersive_library.SkinCache;
import fabric.net.mca.client.gui.immersive_library.types.LiteContent;
import fabric.net.mca.client.render.layer.ClothingLayer;
import fabric.net.mca.client.render.layer.FaceLayer;
import fabric.net.mca.client.render.layer.HairLayer;
import fabric.net.mca.client.render.layer.SkinLayer;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
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
  public static final int PADDING_A = 10;
  public static final int PADDING_B = 5;

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

          button.setMessage(Translations.DESTINY
              .apply(McaExpanded.CONFIG.read().destiny ? "Enabled" : "Disabled"));
        });
  }

  public static ButtonComponent presetListButton(Screen parent, FlowLayout root) {
    return presetListButton(parent, root, false, () -> {
    }, Optional.empty());
  }

  public static ButtonComponent presetListButton(Screen parent, FlowLayout root,
      boolean drawBackground, Runnable beforeOpen, Optional<Consumer<String>> use) {
    return Components.button(use.isPresent() ? Translations.PRESETS : Translations.EDIT_PRESETS,
        (button) -> {
          beforeOpen.run();
          OverlayContainer<ParentComponent> overlay = Containers.overlay(null);
          overlay.child(Containers
              .verticalScroll(Sizing.content(), Sizing.fill(55),
                  presetList(parent, overlay, drawBackground, use))
              .surface(Surface.PANEL).padding(Insets.of(PADDING_B)))
              .surface(Surface.VANILLA_TRANSLUCENT).zIndex(100);
          root.child(overlay);
        });
  }

  public static FlowLayout presetList(Screen parent, Component root, boolean drawBackground,
      Optional<Consumer<String>> use) {
    return Components.list(new ArrayList<>(McaExpanded.CONFIG.read().presets.keySet()),
        (layout) -> {
          layout.child(Components.button(Translations.CREATE_PRESET, (button) -> {
            Model config = McaExpanded.CONFIG.read();
            config.presets.put(UUID.randomUUID().toString(), PresetModel.DEFAULT);
            McaExpanded.CONFIG.write(config);

            FlowLayout list = ((FlowLayout) button.parent());
            list.clearChildren();
            list.children(presetList(parent, root, drawBackground, use).children());
          })).padding(Insets.of(PADDING_B)).verticalAlignment(VerticalAlignment.CENTER);
        }, (id) -> {
          TextBoxComponent name = Components.textBox(Sizing.fixed(135),
              McaExpanded.CONFIG.read().presets.getOrDefault(id, PresetModel.DEFAULT).presetName);
          name.onChanged().subscribe((newValue) -> {
            Model config = McaExpanded.CONFIG.read();
            if (config.presets.containsKey(id)) {
              config.presets.get(id).presetName = newValue;
            }
            McaExpanded.CONFIG.write(config);
          });
          name.margins(Insets.right(PADDING_B));

          GridLayout result = Containers.grid(Sizing.content(), Sizing.content(), 1, 4);

          result.child(name, 0, 0).child(editPresetButton(parent, id, drawBackground), 0, 1)
              .child(Components.button(Translations.REMOVE_PRESET, (button) -> {
                Model config = McaExpanded.CONFIG.read();
                config.presets.remove(id);
                McaExpanded.CONFIG.write(config);

                FlowLayout list = ((FlowLayout) button.parent().parent());
                list.clearChildren();
                list.children(presetList(parent, root, drawBackground, use).children());
              }).margins(Insets.left(PADDING_B)), 0, 2).margins(Insets.top(PADDING_B))
              .verticalAlignment(VerticalAlignment.CENTER);

          if (use.isPresent()) {
            result.child(Components.button(Translations.USE_PRESET, (button) -> {
              root.remove();
              use.get().accept(id);
            }).margins(Insets.left(PADDING_B)), 0, 3);
          }

          return result;
        }, true);
  }

  public static ButtonComponent editPresetButton(Screen parent, String preset) {
    return editPresetButton(parent, preset, false);
  }

  public static ButtonComponent editPresetButton(Screen parent, String preset,
      boolean drawBackground) {
    MinecraftClient client = MinecraftClient.getInstance();
    return Components.button(Translations.EDIT_PRESET, (button) -> {
      VillagerData data = new VillagerData();

      CustomVillagerEditorScreen screen = new CustomVillagerEditorScreen(parent,
          McaExpanded.CONFIG.read().presets.getOrDefault(preset, PresetModel.DEFAULT).presetName,
          client.player.getUuid(), client.player.getUuid()) {
        @Override
        protected void setPage(String page) {
          boolean loaded = this.page != null && this.page.equals("loading");
          super.setPage(page);

          if (loaded) {
            data.update(this.villager);
            VillagerData
                .fromPreset(
                    McaExpanded.CONFIG.read().presets.getOrDefault(preset, PresetModel.DEFAULT))
                .apply(this.villager);
            // TODO: the selected gender is feminine even though the default is masc
          }
        }

        @Override
        public void close() {
          Model config = McaExpanded.CONFIG.read();
          config.presets.put(preset, new VillagerData(this.villager)
              .toPreset(config.presets.getOrDefault(preset, PresetModel.DEFAULT).presetName));
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
