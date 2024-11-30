package com.lothrazar.storagenetwork.item.remote;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.AbstractNetworkScreen;
import com.lothrazar.storagenetwork.gui.NetworkScreenSize;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class ScreenNetworkExpandedRemote extends AbstractNetworkScreen<ContainerNetworkExpandedRemote> {

  private final NetworkWidget network;
  private final ItemStack remote;

  public ScreenNetworkExpandedRemote(ContainerNetworkExpandedRemote screenContainer, Inventory inv, Component titleIn) {
    super(screenContainer, inv, titleIn);
    //since the rightclick action forces only MAIN_HAND openings, is ok
    this.remote = screenContainer.getRemote();
    network = new NetworkWidget(this, NetworkScreenSize.EXPANDED);
    imageHeight = NetworkWidget.player.height() + NetworkWidget.crafting.height()
        + NetworkWidget.row.height() * network.getSize().lines()
        + NetworkWidget.head.height();
    imageWidth = 256 + 12 * 18;//scrollWidth
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
  protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    final int xCenter = (this.width - this.imageWidth) / 2;
    final int yCenter = (this.height - this.imageHeight) / 2;
    network.renderBgExpanded(ms, partialTicks, mouseX, mouseY, xCenter, yCenter);
    network.applySearchTextToSlots();
    //update network
    network.applySearchTextToSlots();
    network.renderItemSlots(ms, mouseX, mouseY, font);
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
