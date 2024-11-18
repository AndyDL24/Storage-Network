package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.block.AbstractNetworkScreen;
import com.lothrazar.storagenetwork.block.inventory.ContainerNetworkInventory;
import com.lothrazar.storagenetwork.block.inventory.ScreenNetworkInventory;
import com.lothrazar.storagenetwork.block.inventory.TileInventory;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class ScreenNetworkInventoryExpanded extends AbstractNetworkScreen<ContainerNetworkInventoryExpanded> {//AbstractContainerScreen<ContainerNetworkInventoryExpanded> implements IGuiNetwork {

  protected int HEIGHT = 256;
  public int WIDTH = 176;
     protected final NetworkWidget network;
  private TileInventoryExpanded tile;

  private ResourceLocation top = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/inventory_expanded_top.png");
  private ResourceLocation bot = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/inventory_expanded_bottom.png");

  public ScreenNetworkInventoryExpanded(ContainerNetworkInventoryExpanded container, Inventory inv, Component name) {
    super(container, inv, name);

    tile = container.tile;
    network = new NetworkWidget(this, NetworkWidget.NetworkScreenSize.EXPANDED);
    imageHeight = HEIGHT;
    imageWidth = WIDTH;
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
    renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderTooltip(ms, mouseX, mouseY);
    network.searchBar.render(ms, mouseX, mouseY, partialTicks);
    network.render();
  }
  @Override
  public void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    int xCenter = (width - imageWidth) / 2;
    int yCenter = (height - imageHeight) / 2;
    ms.blit(top, xCenter, yCenter - imageHeight / 2, 0, 0, imageWidth, imageHeight);
    ms.blit(bot, xCenter, yCenter + imageHeight / 2, 0, imageHeight, imageWidth, imageHeight);
    network.applySearchTextToSlots();
    network.renderItemSlots(ms, mouseX, mouseY, font);
  }

  //TODO keyboard events in shared class


// all the IGUINETWORK implementations

  @Override
  public void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
    network.drawGuiContainerForegroundLayer(ms, mouseX, mouseY, font);
  }
  @Override
  public void renderStackTooltip(GuiGraphics ms, ItemStack stack, int mousex, int mousey) {
    ms.renderTooltip(font, stack, mousex, mousey);
  }

  @Override
  public void drawGradient(GuiGraphics ms, int x, int y, int x2, int y2, int u, int v) {
    ms.fillGradient(x, y, x2, y2, u, v);
  }

  @Override
  public void setStacks(List<ItemStack> stacks) {
    network.stacks = stacks;
  }


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
  public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isHovering(x, y, width, height, mouseX, mouseY);
  }

  @Override
  public NetworkWidget getNetworkWidget() {
    return network;
  }

}
