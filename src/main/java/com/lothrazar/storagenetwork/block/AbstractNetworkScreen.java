package com.lothrazar.storagenetwork.block;

import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.block.request.ContainerNetworkCraftingTable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractNetworkScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IGuiNetwork  {
    public AbstractNetworkScreen(T container, Inventory inv, Component name) {
        super(container, inv, name);
    }
}
