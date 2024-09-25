package diacritics.owo.util;

import java.io.IOException;
import org.jetbrains.annotations.Nullable;
import diacritics.owo.McaExpanded;
import fabric.net.mca.client.render.layer.ClothingLayer;
import fabric.net.mca.client.render.layer.FaceLayer;
import fabric.net.mca.client.render.layer.HairLayer;
import fabric.net.mca.client.render.layer.SkinLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
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

  public static final NativeImage readImage(Identifier identifier) {
    return readImage(new float[] {1, 1, 1}, identifier);
  }

  @Nullable
  public static final NativeImage readImage(float[] tint, Identifier identifier) {
    Resource resource =
        MinecraftClient.getInstance().getResourceManager().getResource(identifier).orElse(null);

    if (resource == null) {
      return null;
    }

    // TODO: support immersive_library
    try {
      NativeImage image = NativeImage.read(resource.getInputStream());
      image.apply((abgr) -> Abgr.getAbgr(Abgr.getAlpha(abgr), (int) (tint[2] * Abgr.getBlue(abgr)),
          (int) (tint[1] * Abgr.getGreen(abgr)), (int) (tint[0] * Abgr.getRed(abgr))));
      return image;
    } catch (IOException exception) {
      McaExpanded.LOGGER.error("encountered an error while reading {}", identifier, exception);
      return null;
    }
  }
}
