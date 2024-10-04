package com.lothrazar.storagenetwork.block.cable.processing;

import com.lothrazar.storagenetwork.block.TileCableWithFacing;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileCableProcess extends TileCableWithFacing {

  protected CapabilityConnectableLink itemStorage;
  private ProcessRequestModel processModel = new ProcessRequestModel();

  public TileCableProcess(BlockPos pos, BlockState state) {
    super(SsnRegistry.Tiles.PROCESS_KABEL.get(), pos, state);
    this.itemStorage = new CapabilityConnectableLink(this);
  }

  @Override
  public void load(CompoundTag compound) {
    super.load(compound);
    this.itemStorage.deserializeNBT(compound.getCompound("capability"));
    this.processModel.readFromNBT(compound);
  }

  @Override
  public void saveAdditional(CompoundTag compound) {
    super.saveAdditional(compound);
    compound.put("capability", itemStorage.serializeNBT());
    this.processModel.writeToNBT(compound);
  }

  @Override
  public void setDirection(Direction direction) {
    super.setDirection(direction);
    this.itemStorage.setInventoryFace(direction);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
    if (capability == StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY) {
      LazyOptional<CapabilityConnectableLink> cap = LazyOptional.of(() -> itemStorage);
      return cap.cast();
    }
    return super.getCapability(capability, facing);
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCableProcess tile) {}

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCableProcess tile) {
    tile.refreshInventoryDirection();
  }
}
