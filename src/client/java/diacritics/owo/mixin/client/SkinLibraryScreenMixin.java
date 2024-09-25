package diacritics.owo.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import diacritics.owo.gui.CustomVillagerEditorScreen;
import fabric.net.mca.client.gui.SkinLibraryScreen;
import fabric.net.mca.client.gui.VillagerEditorScreen;

@Mixin(value = SkinLibraryScreen.class, remap = false)
public abstract class SkinLibraryScreenMixin {
  @Shadow
  private final VillagerEditorScreen previousScreen;

  @Shadow
  abstract public void close();

  public SkinLibraryScreenMixin() {
    this.previousScreen = null;
  }

  @WrapMethod(method = "returnToPreviousScreen")
  private void mcaExpanded$returnToPreviousScreen(Operation<Void> original) {
    if (this.previousScreen instanceof CustomVillagerEditorScreen) {
      this.close();
    } else {
      original.call();
    }
  }
}
