package com.lothrazar.storagenetwork.block.inventory;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.Level;

public class ContainerNetworkInventory extends ContainerNetwork {

  final TileInventory tile;

  public ContainerNetworkInventory(int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
    this(SsnRegistry.Menus.INVENTORY.get(), windowId, world, pos, playerInv, player);
  }

  public ContainerNetworkInventory(MenuType<?> menuType, int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
    super(menuType, windowId);
    tile = (TileInventory) world.getBlockEntity(pos);
    this.playerInv = playerInv;
    bindPlayerInvo(this.playerInv);
    bindHotbar();
  }

  @Override
  public boolean isCrafting() {
    return false;
  }

  @Override
  public void slotChanged() {}

  @Override
  public boolean stillValid(Player playerIn) {
    return true;
  }

  @Override
  public TileMain getTileMain() {
    if (tile == null || tile.getMain() == null) {
      //refresh delay, new chunk load or block placement
      return null;
    }
    return tile.getMain().getTileEntity(TileMain.class);
  }
}
