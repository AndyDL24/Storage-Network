package com.lothrazar.storagenetwork.jei;

import java.util.List;
import java.util.stream.Collectors;
import com.lothrazar.storagenetwork.network.RecipeMessage;
import com.lothrazar.storagenetwork.registry.ConfigRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

/**
 * https://github.com/mezz/JustEnoughItems/blob/1.19/Common/src/main/java/mezz/jei/common/transfer/BasicRecipeTransferHandler.java
 * 
 * NEW in mc1.19: abstract class to override in the Plugin during registration
 */
public abstract class RequestRecipeTransferHandler<C extends AbstractContainerMenu> implements IRecipeTransferHandler<C, CraftingRecipe> {

  @Override
  public IRecipeTransferError transferRecipe(C c, CraftingRecipe recipe, IRecipeSlotsView recipeSlots, Player playerEntity,
      boolean maxTransfer, boolean doTransfer) {
    if (doTransfer) {
      CompoundTag nbt = RequestRecipeTransferHandler.recipeToTag(c, recipeSlots);
      PacketRegistry.INSTANCE.sendToServer(new RecipeMessage(nbt));
    }
    return null;
  }

  public static CompoundTag recipeToTag(AbstractContainerMenu container, IRecipeSlotsView recipeSlots) {
    CompoundTag nbt = new CompoundTag();

    List<IRecipeSlotView> slotsViewList = recipeSlots.getSlotViews();
    for (Slot slot : container.slots) {
      if (slot.container instanceof net.minecraft.world.inventory.CraftingContainer) {
        //for some reason it was looping like this  (int j = 1; j < 10; j++)
        IRecipeSlotView slotView = slotsViewList.get(slot.getSlotIndex() + 1);
        if (slotView == null) {
          continue;
        }
        List<ItemStack> possibleItems = slotView.getIngredients(VanillaTypes.ITEM_STACK).collect(Collectors.toList());
        if (possibleItems == null || possibleItems.isEmpty()) {
          continue;
        }
        ListTag invList = new ListTag();
        for (int i = 0; i < possibleItems.size(); i++) {
          if (i >= ConfigRegistry.RECIPEMAXTAGS.get()) {
            break;
          }
          ItemStack itemStack = possibleItems.get(i);
          if (!itemStack.isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            itemStack.save(stackTag);
            invList.add(stackTag);
          }
        }
        nbt.put("s" + (slot.getSlotIndex()), invList);
      }
    }
    return nbt;
  }

  @Override
  public RecipeType<CraftingRecipe> getRecipeType() {
    return RecipeTypes.CRAFTING;
  }
}
