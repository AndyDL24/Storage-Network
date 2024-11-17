package com.lothrazar.storagenetwork.block.expand;

import com.lothrazar.storagenetwork.block.EntityBlockConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockInventoryExpanded extends EntityBlockConnectable {

  public BlockInventoryExpanded() {
    super();
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileInventoryExpanded(pos, state);
  }

}
