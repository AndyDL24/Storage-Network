package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.block.AbstractNetworkScreen;
import com.lothrazar.storagenetwork.gui.NetworkScreenSize;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.gui.TileableTexture;
import com.lothrazar.storagenetwork.util.SsnConsts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fml.ModList;

public class ScreenNetworkInventoryExpanded extends AbstractNetworkScreen<ContainerNetworkInventoryExpanded> {

  protected int HEIGHT = 256;
  public int WIDTH = 176;
  //i know they could all be in the same png file and i pull out sprites from it, but split images is easier to work with
  private TileableTexture head = new TileableTexture(new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/expandable_head.png"), WIDTH, 10);
  private TileableTexture row = new TileableTexture(new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/expandable_row.png"), WIDTH, SsnConsts.SQ);
  private TileableTexture crafting = new TileableTexture(new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/expandable_crafting.png"), WIDTH, 58);
  private TileableTexture player = new TileableTexture(new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/expandable_player.png"), WIDTH, 104);

  protected final NetworkWidget network;
  private TileInventoryExpanded tile;

  public ScreenNetworkInventoryExpanded(ContainerNetworkInventoryExpanded container, Inventory inv, Component name) {
    super(container, inv, name);
    tile = container.tile;
    network = new NetworkWidget(this, NetworkScreenSize.EXPANDED);
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
  public void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    //get center points from screen size
    int xCenter = (width - imageWidth) / 2;
    int yCenter = (height - imageHeight) / 2;
    //render the top
    int ypos = yCenter - imageHeight / 2;
    ms.blit(head.texture(), xCenter, ypos, 0, 0, imageWidth, head.height());
    //render the rows
    // -1 for the top header row, and pixel to math ratio of 2x
    final int textureLines = (network.getLines() - 1)*2;
    for(int line = 0; line < textureLines; line++) {

      ypos += head.height();
      ms.blit(row.texture(), xCenter, ypos, 0, 0, imageWidth, row.height());
    }
    //render player inventory
    ypos = ypos - 12; //border buffers of 8+4 overlap
    ms.blit(player.texture(), xCenter, ypos, 0, imageHeight, imageWidth, imageHeight);
    //update network
    network.applySearchTextToSlots();
    network.renderItemSlots(ms, mouseX, mouseY, font);
  }

  @Override
  public void resize(Minecraft mc, int w, int h) {
    super.resize(mc,w,h);
    network.resize(mc,w,h);
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
