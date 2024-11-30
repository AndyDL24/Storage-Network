package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.AbstractNetworkScreen;
import com.lothrazar.storagenetwork.gui.NetworkScreenSize;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fml.ModList;

public class ScreenNetworkInventoryExpanded extends AbstractNetworkScreen<ContainerNetworkInventoryExpanded> {

  protected final NetworkWidget network;
  private TileInventoryExpanded tile;

  public ScreenNetworkInventoryExpanded(ContainerNetworkInventoryExpanded container, Inventory inv, Component name) {
    super(container, inv, name);
    tile = container.tile;
    network = new NetworkWidget(this, NetworkScreenSize.EXPANDED);
    //TODO: refactor this calculation
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
    if (this.network.getSize().isCrafting())
      addRenderableWidget(network.clearGridBtn);
    if (this.getAutoFocus()) {
      network.searchBar.setFocused(true);
    }
    if (ModList.get().isLoaded("jei")) {
      addRenderableWidget(network.jeiBtn);
    }
  }

  @Override
  public void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    //get center points from screen size
    final int xCenter = (width - imageWidth) / 2;
    final int yCenter = (height - imageHeight) / 2;
    network.renderBgExpanded(ms, partialTicks, mouseX, mouseY, xCenter, yCenter);
    //update network
    network.applySearchTextToSlots();
    network.renderItemSlots(ms, mouseX, mouseY, font);
  }
  // all the IGUINETWORK implementations

  @Override
  public boolean getDownwards() {
    return tile.isDownwards();
  }

  @Override
  public void setDownwards(boolean d) {
    tile.setDownwards(d);
  }

  @Override
  public EnumSortType getSort() {
    return tile.getSort();
  }

  @Override
  public void setSort(EnumSortType s) {
    tile.setSort(s);
  }

  @Override
  public BlockPos getPos() {
    return tile.getBlockPos();
  }

  @Override
  public boolean isJeiSearchSynced() {
    return tile.isJeiSearchSynced();
  }

  @Override
  public void setJeiSearchSynced(boolean val) {
    tile.setJeiSearchSynced(val);
  }

  @Override
  public boolean getAutoFocus() {
    return tile.getAutoFocus();
  }

  @Override
  public void setAutoFocus(boolean b) {
    tile.setAutoFocus(b);
  }

  @Override
  public NetworkWidget getNetwork() {
    return network;
  }
}
