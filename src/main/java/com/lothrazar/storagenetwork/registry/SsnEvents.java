package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.block.cable.TileCable;
import com.lothrazar.storagenetwork.item.ItemBuilder;
import com.lothrazar.storagenetwork.network.CableFacadeMessage;
import com.lothrazar.storagenetwork.network.KeybindCurioMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SsnEvents {

  @SubscribeEvent
  public void onEntityItemPickupEvent(EntityItemPickupEvent event) {
    SsnRegistry.Items.COLLECTOR_REMOTE.get().onEntityItemPickupEvent(event);
  }

  @SubscribeEvent
  public void onHit(PlayerInteractEvent.LeftClickBlock event) {
    ItemBuilder.onLeftClickBlock(event);
    if (ConfigRegistry.enableFacades.get()) {
      onHitFacadeHandler(event);
    }
  }

  private void onHitFacadeHandler(PlayerInteractEvent.LeftClickBlock event) {
    Level level = event.getLevel();
    if (!level.isClientSide) {
      return; // dont save client data; server side only from here on
    }
    Player player = event.getEntity();
    if (!player.isCrouching()) {
      return; // match with 1.12 pr, only put facades when crouching
    }
    ItemStack held = player.getItemInHand(event.getHand());
    TileCable cable = TileCable.getTileCable(level, event.getPos());
    if (cable != null) {
      if (held.isEmpty()) {
        PacketRegistry.INSTANCE.sendToServer(new CableFacadeMessage(event.getPos(), true));
      }
      else {
        Block block = Block.byItem(held.getItem());
        if (block == null || block == Blocks.AIR) {
          return;
        }
        if (!ConfigRegistry.isFacadeAllowed(held)) {
          StorageNetworkMod.log("not allowed as a facade from config file: " + held.getItem());
          return;
        }
        //pick the block, write to tags, and send to server
        boolean pickFluids = false;
        BlockHitResult bhr = (BlockHitResult) player.pick(player.getBlockReach(), 1, pickFluids);
        BlockPlaceContext context = new BlockPlaceContext(player, event.getHand(), held, bhr);
        BlockState facadeState = block.getStateForPlacement(context);
        CompoundTag tags = (facadeState == null) ? null : NbtUtils.writeBlockState(facadeState);
        PacketRegistry.INSTANCE.sendToServer(new CableFacadeMessage(event.getPos(), tags));
      }
    }
  }

  @SubscribeEvent
  public void onKeyInput(InputEvent.Key event) {
    if (ClientEventRegistry.INVENTORY_KEY.consumeClick()) {
      //gogo client -> server event
      PacketRegistry.INSTANCE.sendToServer(new KeybindCurioMessage());
    }
  }
}
