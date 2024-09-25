package diacritics.owo.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import diacritics.owo.McaExpanded;
import fabric.net.mca.MCAClient;
import fabric.net.mca.client.gui.DestinyScreen;
import fabric.net.mca.cobalt.network.NetworkHandler;
import fabric.net.mca.network.c2s.DestinyMessage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Mixin(value = DestinyScreen.class, remap = false)
public abstract class DestinyScreenMixin extends Screen {
  protected DestinyScreenMixin(Text title) {
    super(title);
  }

  @WrapMethod(method = "setPage")
  private void mcaExpanded$setPage(String page, Operation<Void> original) {
    if (!McaExpanded.CONFIG.destiny() && page != null
        && (page.equals("destiny") || page.equals("story"))) {
      NetworkHandler.sendToServer(new DestinyMessage(true));
      MCAClient.getDestinyManager().allowClosing();
      super.close();
    } else {
      original.call(page);
    }
  }
}
