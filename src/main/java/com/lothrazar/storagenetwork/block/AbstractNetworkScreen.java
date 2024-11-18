package com.lothrazar.storagenetwork.block;

import com.lothrazar.storagenetwork.api.IGuiNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractNetworkScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IGuiNetwork  {
    public AbstractNetworkScreen(T container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void setStacks(List<ItemStack> stacks) {
        getNetwork().setStacks(stacks);
    }

    @Override
    public void drawGradient(GuiGraphics ms, int x, int y, int x2, int y2, int u, int v) {
        ms.fillGradient(x, y, x2, y2, u, v);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (getNetwork().charTyped(typedChar, keyCode)) {
            return true;
        }
        return false;
    }

    // used by ItemSlotNetwork and NetworkWidget
    @Override
    public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    public boolean isScrollable(double x, double y) {
        return isHovering(0, 0,
                this.width - 8, getNetwork().scrollHeight,
                x, y);
    }
    @Override
    public boolean mouseScrolled(double x, double y, double mouseButton) {
        super.mouseScrolled(x, y, mouseButton);
        //<0 going down
        // >0 going up
        if (isScrollable(x, y) && mouseButton != 0) {
            getNetwork().mouseScrolled(mouseButton);
        }
        return true;
    }

    @Override
    public void renderStackTooltip(GuiGraphics ms, ItemStack stack, int mousex, int mousey) {
        ms.renderTooltip(font, stack, mousex, mousey);
    }

    @Override
    public void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
        getNetwork().drawGuiContainerForegroundLayer(ms, mouseX, mouseY, font);
    }


}
