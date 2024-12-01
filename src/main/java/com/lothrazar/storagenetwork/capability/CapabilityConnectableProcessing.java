package com.lothrazar.storagenetwork.capability;

import java.util.concurrent.Callable;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.IConnectable;
import com.lothrazar.storagenetwork.api.IConnectableItemProcessing;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.capability.handler.FilterItemStackHandler;
import com.lothrazar.storagenetwork.capability.handler.UpgradesItemStackHandler;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityConnectableProcessing implements INBTSerializable<CompoundTag>, IConnectableItemProcessing {

  public static final int DEFAULT_ITEMS_PER = 4;
  public static final int IO_DEFAULT_SPEED = 30; // TODO CONFIG

  public static class Factory implements Callable<IConnectableItemProcessing> {

    @Override
    public IConnectableItemProcessing call() throws Exception {
      return new CapabilityConnectableProcessing();
    }
  }

  private Direction inventoryFace;
  public final IConnectable connectable;
  public final UpgradesItemStackHandler upgrades = new UpgradesItemStackHandler();
  private final FilterItemStackHandler filters = new FilterItemStackHandler(9);
  private final FilterItemStackHandler filtersOut = new FilterItemStackHandler(1);
  private int priority;

  CapabilityConnectableProcessing() {
    connectable = new CapabilityConnectable();
  }

  public CapabilityConnectableProcessing(BlockEntity tile) {
    connectable = tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_CAPABILITY, null).orElse(null);
  }

  public void setInventoryFace(Direction inventoryFace) {
    this.inventoryFace = inventoryFace;
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag result = new CompoundTag();
    result.putInt("prio", priority);
    result.put("upgrades", this.upgrades.serializeNBT());
    result.put("filtersIn", this.filters.serializeNBT());
    result.put("filtersOut", this.filtersOut.serializeNBT());
    if (inventoryFace != null) {
      result.putString("inventoryFace", inventoryFace.toString());
    }
    //    result.putBoolean("needsRedstone", this.needsRedstone());
    return result;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    priority = nbt.getInt("prio");
    CompoundTag upgrades = nbt.getCompound("upgrades");
    if (upgrades != null) {
      this.upgrades.deserializeNBT(upgrades);
    }
    CompoundTag filters = nbt.getCompound("filters");
    if (filters != null) {
      this.filters.deserializeNBT(filters);
    }
    if (nbt.contains("inventoryFace")) {
      inventoryFace = Direction.byName(nbt.getString("inventoryFace"));
    }
    //    this.needsRedstone(nbt.getBoolean("needsRedstone"));
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public void setPriority(int value) {
    this.priority = value;
  }

  @Override
  public Direction facingInventory() {
    return this.inventoryFace;
  }

  @Override
  public void execute(TileMain main) {
    StorageNetworkMod.log("Run Now processing cable; " + main + "..." + this);
  }
}
