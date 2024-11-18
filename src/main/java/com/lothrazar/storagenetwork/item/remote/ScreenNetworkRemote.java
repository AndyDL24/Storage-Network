package com.lothrazar.storagenetwork.item.remote;

import java.util.List;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.block.AbstractNetworkScreen;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.gui.NetworkWidget.NetworkScreenSize;
import com.lothrazar.storagenetwork.gui.TextboxInteger;
import com.lothrazar.storagenetwork.jei.JeiHooks;
import com.lothrazar.storagenetwork.network.SettingsSyncMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class ScreenNetworkRemote  extends AbstractNetworkScreen<ContainerNetworkRemote> {// extends AbstractContainerScreen<ContainerNetworkRemote> implements IGuiNetwork {

  private static final int HEIGHT = 256;
  private static final int WIDTH = 176;
  private static final ResourceLocation texture = new ResourceLocation(StorageNetworkMod.MODID,
      "textures/gui/inventory.png");
  private final NetworkWidget network;
  private final ItemStack remote;

  public ScreenNetworkRemote(ContainerNetworkRemote screenContainer, Inventory inv, Component titleIn) {
    super(screenContainer, inv, titleIn);
    //since the rightclick action forces only MAIN_HAND openings, is ok
    this.remote = screenContainer.getRemote();
    network = new NetworkWidget(this, NetworkScreenSize.LARGE);
    this.imageWidth = WIDTH;
    this.imageHeight = HEIGHT;
  }


  @Override
  public void init() {
    super.init();

    network.init(this.font);

    addRenderableWidget(network.directionBtn);
    addRenderableWidget(network.sortBtn);
    addRenderableWidget(network.focusBtn);
    if (this.getAutoFocus()) {
      network.searchBar.setFocused(true);
    }
    if (ModList.get().isLoaded("jei")) {
      addRenderableWidget(network.jeiBtn);
    }
  }

  @Override
  public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderTooltip(ms, mouseX, mouseY);
    network.searchBar.render(ms, mouseX, mouseY, partialTicks);
    network.render();
  }

  @Override
  protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    int xCenter = (this.width - this.imageWidth) / 2;
    int yCenter = (this.height - this.imageHeight) / 2;
    ms.blit(texture, xCenter, yCenter, 0, 0, this.imageWidth, this.imageHeight);
    getNetwork().applySearchTextToSlots();
    getNetwork().renderItemSlots(ms, mouseX, mouseY, font);
  }



  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    getNetwork().mouseClicked(mouseX, mouseY, mouseButton);
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int b) {
    InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
    if (keyCode == TextboxInteger.KEY_ESC) {
      minecraft.player.closeContainer();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    if (network.searchBar.isFocused()) {
      if (keyCode == TextboxInteger.KEY_BACKSPACE) {
        network.syncTextToJei();
      }
      network.searchBar.keyPressed(keyCode, scanCode, b);
      return true;
    }
    else if (!network.stackUnderMouse.isEmpty()) {
      try {
        JeiHooks.testJeiKeybind(mouseKey, network.stackUnderMouse);
      }
      catch (Throwable e) {
        StorageNetworkMod.LOGGER.error("JEI compat issue ", e);
      }
    }
    //regardles of above branch, also check this
    if (minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
      minecraft.player.closeContainer();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    return super.keyPressed(keyCode, scanCode, b);
  }

  @Override
  public boolean getDownwards() {
    return ItemStorageCraftingRemote.getDownwards(remote);
  }

  @Override
  public void setDownwards(boolean val) {
    ItemStorageCraftingRemote.setDownwards(remote, val);
  }

  @Override
  public EnumSortType getSort() {
    return ItemStorageCraftingRemote.getSort(remote);
  }

  @Override
  public void setSort(EnumSortType val) {
    ItemStorageCraftingRemote.setSort(remote, val);
  }

  @Override
  public boolean isJeiSearchSynced() {
    return ItemStorageCraftingRemote.isJeiSearchSynced(remote);
  }

  @Override
  public void setJeiSearchSynced(boolean val) {
    ItemStorageCraftingRemote.setJeiSearchSynced(remote, val);
  }

  @Override
  public boolean getAutoFocus() {
    return ItemStorageCraftingRemote.getAutoFocus(remote);
  }

  @Override
  public void setAutoFocus(boolean b) {
    ItemStorageCraftingRemote.setAutoFocus(remote, b);
  }

  @Override
  public NetworkWidget getNetwork() {
    return network;
  }
}
