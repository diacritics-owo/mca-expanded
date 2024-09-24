package diacritics.owo.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import diacritics.owo.McaExpanded;
import diacritics.owo.util.VillagerData;
import fabric.net.mca.client.gui.VillagerEditorScreen;
import fabric.net.mca.entity.VillagerEntityMCA;
import fabric.net.mca.util.compat.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Mixin(value = VillagerEditorScreen.class, remap = false)
public abstract class VillagerEditorScreenMixin extends Screen {
  @Shadow
  protected final VillagerEntityMCA villager;

  protected VillagerEditorScreenMixin(Text title) {
    super(title);
    this.villager = null;
  }

  @Inject(at = @At("TAIL"), method = "setPage")
  protected void setPage(String page, CallbackInfo info) {
    int width = 135;
    int height = 20;

    int x = this.width / 2 - 175 + 20;
    int y = this.height / 2 - 85;

    this.addDrawableChild(new ButtonWidget(x, y, width, height,
        Text.translatable("gui.mca-expanded.button.use-preset"), (button) -> {
          VillagerData.fromPreset(McaExpanded.CONFIG.read().preset).apply(this.villager);
        }));
  }
}
