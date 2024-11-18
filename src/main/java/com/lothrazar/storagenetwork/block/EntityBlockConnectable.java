package com.lothrazar.storagenetwork.block;

import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.storagenetwork.network.SortClientMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;

public abstract class EntityBlockConnectable extends EntityBlockFlib {

  protected boolean hasGui = true;

  public EntityBlockConnectable() {
    this(Block.Properties.of().strength(0.5F).sound(SoundType.STONE));
  }

  public EntityBlockConnectable(BlockBehaviour.Properties p) {
    super(p);
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
    if (!world.isClientSide && hasGui) {
      if (world.getBlockEntity(pos) instanceof TileConnectable tile) {
        if (tile.getMain() == null || tile.getMain().getBlockPos() == null) {
          return InteractionResult.PASS;
        }
        //sync
        ServerPlayer sp = (ServerPlayer) player;
        PacketRegistry.INSTANCE.sendTo(new SortClientMessage(pos, tile.isDownwards(), tile.getSort()), sp.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        //end sync
        if (tile instanceof MenuProvider) {
          NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) tile, tile.getBlockPos());
        }
        else {
          throw new IllegalStateException("Our named container provider is missing!");
        }
      }
    }
    return InteractionResult.SUCCESS;
  }
}
