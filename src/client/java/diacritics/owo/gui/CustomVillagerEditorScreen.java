package diacritics.owo.gui;

import java.util.UUID;
import diacritics.owo.util.ClientHelpers;
import diacritics.owo.util.Translations;
import fabric.net.mca.client.gui.VillagerEditorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextWidget;

public class CustomVillagerEditorScreen extends VillagerEditorScreen {
  private Screen parent;
  private String title;

  public CustomVillagerEditorScreen(Screen parent, String title, UUID villagerUUID,
      UUID playerUUID) {
    super(villagerUUID, playerUUID);
    this.parent = parent;
    this.title = title;
  }

  @Override
  public void close() {
    this.client.setScreen(this.parent);
  }

  @Override
  protected void setPage(String page) {
    super.setPage(page);

    if (this.textRenderer != null) {
      TextWidget label =
          new TextWidget(Translations.EDITING_PRESET.apply(this.title), this.textRenderer);
      label.setPosition((this.width - label.getWidth()) / 2,
          this.height - label.getHeight() - (ClientHelpers.PADDING_B / 2));
      this.addDrawableChild(label);
    }
  }
}
