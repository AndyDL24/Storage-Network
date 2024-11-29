package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.gui.slot.SlotCraftingNetwork;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class ContainerNetworkInventoryExpanded extends ContainerNetwork {

  TileInventoryExpanded tile;
  private final ContainerLevelAccess access;

  public ContainerNetworkInventoryExpanded(int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
    this(SsnRegistry.Menus.INVENTORY_EXPANDED.get(), windowId, world, pos, playerInv, player);
  }

  public ContainerNetworkInventoryExpanded(MenuType<?> menuType, int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
    super(menuType, windowId);
    tile = (TileInventoryExpanded) world.getBlockEntity(pos);
    setCraftMatrix(new NetworkCraftingInventory(this));
    access = ContainerLevelAccess.create(world, pos);
    this.yPlayer = 256 + 196;
    this.yCrafting = this.yPlayer - 64 + 7;
    this.playerInv = playerInv;
    SlotCraftingNetwork slotCraftOutput = new SlotCraftingNetwork(this, playerInv.player, getCraftMatrix(), resultInventory, 0,
        101, yPlayer - 46 + 7);
    slotCraftOutput.setTileMain(getTileMain());
    addSlot(slotCraftOutput);
    bindGrid();
    bindPlayerInvo(this.playerInv);
    bindHotbar();
    slotsChanged(getCraftMatrix());
  }

  @Override
  public boolean isCrafting() {
    return true;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    //the contents of the crafting matrix gets returned to the player
    this.access.execute((level, pos) -> {
      this.clearContainer(player, getCraftMatrix());
    });
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
