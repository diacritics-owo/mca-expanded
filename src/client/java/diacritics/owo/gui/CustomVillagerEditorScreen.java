package diacritics.owo.gui;

import java.util.UUID;
import fabric.net.mca.client.gui.VillagerEditorScreen;
import net.minecraft.client.gui.screen.Screen;

public class CustomVillagerEditorScreen extends VillagerEditorScreen {
  private Screen parent;

  public CustomVillagerEditorScreen(Screen parent, UUID villagerUUID, UUID playerUUID) {
    super(villagerUUID, playerUUID);
  }

  @Override
  public void close() {
    this.client.setScreen(this.parent);
  }
}
