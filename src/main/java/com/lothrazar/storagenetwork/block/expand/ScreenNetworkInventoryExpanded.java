package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.block.inventory.ContainerNetworkInventory;
import com.lothrazar.storagenetwork.block.inventory.ScreenNetworkInventory;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScreenNetworkInventoryExpanded extends ScreenNetworkInventory {

  private ResourceLocation top = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/inventory_expanded_top.png");
  private ResourceLocation bot = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/inventory_expanded_bottom.png");

  public ScreenNetworkInventoryExpanded(ContainerNetworkInventory container, Inventory inv, Component name) {
    super(container, inv, name);

  }
  public NetworkWidget.NetworkScreenSize getSize() {
    return NetworkWidget.NetworkScreenSize.EXPANDED;
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
}
