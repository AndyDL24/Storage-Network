package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.block.inventory.TileInventory;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class TileInventoryExpanded extends TileInventory {

  public TileInventoryExpanded(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.INVENTORY_EXPANDED.get(), pos, state);
  }

  @Override
  public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
    return new ContainerNetworkInventoryExpanded(i, level, worldPosition, playerInventory, playerEntity);
  }
}
