package com.lothrazar.storagenetwork.block.cable.export;

import java.util.List;
import java.util.Optional;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.StorageNetworkMod;
import com.lothrazar.storagenetwork.api.IGuiPrivate;
import com.lothrazar.storagenetwork.api.OpCompareType;
import com.lothrazar.storagenetwork.block.cable.inputfilter.ScreenCableImportFilter;
import com.lothrazar.storagenetwork.capability.handler.FilterItemStackHandler;
import com.lothrazar.storagenetwork.gui.components.ButtonRequest;
import com.lothrazar.storagenetwork.gui.components.ButtonRequest.TextureEnum;
import com.lothrazar.storagenetwork.gui.components.TextboxInteger;
import com.lothrazar.storagenetwork.gui.slot.ItemSlotNetwork;
import com.lothrazar.storagenetwork.network.CableIOMessage;
import com.lothrazar.storagenetwork.registry.ClientEventRegistry;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.lothrazar.storagenetwork.util.SsnConsts;
import com.lothrazar.storagenetwork.util.UtilTileEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ScreenCableExportFilter extends AbstractContainerScreen<ContainerCableExportFilter> implements IGuiPrivate {

  protected static final Button.CreateNarration DEFAULT_NARRATION = (supplier) -> {
    return supplier.get();
  };
  private final ResourceLocation texture = new ResourceLocation(StorageNetworkMod.MODID, "textures/gui/cable_filter.png");
  ContainerCableExportFilter containerCableLink;
  private ButtonRequest btnRedstone;
  private ButtonRequest btnMinus;
  private ButtonRequest btnPlus;
  private ButtonRequest btnImport;
  private boolean isAllowlist;
  private List<ItemSlotNetwork> itemSlotsGhost;
  private ButtonRequest btnOperationToggle;
  private ItemSlotNetwork operationItemSlot;
  private TextboxInteger txtHeight;

  public ScreenCableExportFilter(ContainerCableExportFilter containerCableFilter, Inventory inv, Component name) {
    super(containerCableFilter, inv, name);
    this.containerCableLink = containerCableFilter;
  }

  @Override
  public void renderStackTooltip(GuiGraphics ms, ItemStack stack, int mousex, int mousey) {
    ms.renderTooltip(font, stack, mousex, mousey);
  }

  @Override
  public void drawGradient(GuiGraphics ms, int x, int y, int x2, int y2, int u, int v) {
    ms.fillGradient(x, y, x2, y2, u, v);
  }

  @Override
  public void init() {
    super.init();
    this.isAllowlist = containerCableLink.cap.getFilter().isAllowList;
    btnRedstone = addRenderableWidget(new ButtonRequest(leftPos + 4, topPos + 4, "", (p) -> {
      this.syncData(0);
      PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.REDSTONE.ordinal()));
    }, DEFAULT_NARRATION));
    btnMinus = addRenderableWidget(new ButtonRequest(leftPos + 22, topPos + 4, "", (p) -> {
      this.syncData(-1);
    }, DEFAULT_NARRATION));
    btnMinus.setTextureId(TextureEnum.MINUS);
    btnPlus = addRenderableWidget(new ButtonRequest(leftPos + 60, topPos + 4, "", (p) -> {
      this.syncData(+1);
    }, DEFAULT_NARRATION));
    btnPlus.setTextureId(TextureEnum.PLUS);
    btnImport = addRenderableWidget(new ButtonRequest(leftPos + 80, topPos + 4, "", (p) -> {
      importFilterSlots();
    }, DEFAULT_NARRATION));
    btnImport.setTextureId(TextureEnum.IMPORT);
    txtHeight = new TextboxInteger(this.font, leftPos + 48, topPos + 26, 36);
    txtHeight.setMaxLength(4);
    txtHeight.setValue("" + containerCableLink.cap.operationLimit);
    this.addRenderableWidget(txtHeight);
    btnOperationToggle = addRenderableWidget(new ButtonRequest(leftPos + 29, topPos + 26, "=", (p) -> {
      //      containerCableLink.cap.operationType = containerCableLink.cap.operationType.toggle();
      OpCompareType old = OpCompareType.get(containerCableLink.cap.operationType);
      containerCableLink.cap.operationType = old.toggle().ordinal();
      PacketRegistry.INSTANCE.sendToServer(
          new CableIOMessage(CableIOMessage.CableMessageType.SYNC_OP.ordinal(),
              containerCableLink.cap.operationType, false));
    }, DEFAULT_NARRATION));
    txtHeight.visible = btnOperationToggle.visible = false;
  }

  private void importFilterSlots() {
    PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.IMPORT_FILTER.ordinal()));
  }

  private void sendStackSlot(int value, ItemStack stack) {
    PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.SAVE_FITLER.ordinal(), value, stack));
  }

  private void syncData(int priority) {
    containerCableLink.cap.getFilter().isAllowList = this.isAllowlist;
    PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.SYNC_DATA.ordinal(), priority, isAllowlist));
  }

  @Override
  public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
    renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderTooltip(ms, mouseX, mouseY);
    //true means we need redstone in order to work
    //false (default) means always on ignoring worldstate
    btnRedstone.setTextureId(containerCableLink.cap.needsRedstone() ? TextureEnum.REDSTONETRUE : TextureEnum.REDSTONEFALSE);
    btnOperationToggle.visible = this.isOperationMode();
    txtHeight.visible = btnOperationToggle.active = btnOperationToggle.visible;
  }

  @Override
  public void renderLabels(GuiGraphics ms, int mouseX, int mouseY) {
    //    this.font.draw(ms, this.title, this.titleLabelX, this.titleLabelY, 4210752);// TODO: gui titles
    int priority = containerCableLink.cap.getPriority();
    ms.drawString(font, String.valueOf(priority),
        50 - font.width(String.valueOf(priority)) / 2,
        12,
        4210752);
    if (btnOperationToggle != null && this.isOperationMode()) {
      OpCompareType t = OpCompareType.get(containerCableLink.cap.operationType);
      btnOperationToggle.setMessage(Component.literal(t.symbol()));
    }
    this.drawTooltips(ms, mouseX, mouseY);
  }

  private boolean isOperationMode() {
    return this.containerCableLink.cap.isOperationMode();
  }

  private void drawTooltips(GuiGraphics ms, final int mouseX, final int mouseY) {
    //    this.renderTooltip();
    if (btnImport != null && btnImport.isMouseOver(mouseX, mouseY)) {
      ms.renderTooltip(font, Lists.newArrayList(Component.translatable("gui.storagenetwork.import")), Optional.empty(),
          mouseX - leftPos, mouseY - topPos);
    }
    if (btnMinus != null && btnMinus.isMouseOver(mouseX, mouseY)) {
      ms.renderTooltip(font, Lists.newArrayList(Component.translatable("gui.storagenetwork.priority.down")), Optional.empty(),
          mouseX - leftPos, mouseY - topPos);
    }
    if (btnPlus != null && btnPlus.isMouseOver(mouseX, mouseY)) {
      ms.renderTooltip(font, Lists.newArrayList(Component.translatable("gui.storagenetwork.priority.up")), Optional.empty(),
          mouseX - leftPos, mouseY - topPos);
    }
    if (btnRedstone != null && btnRedstone.isMouseOver(mouseX, mouseY)) {
      ms.renderTooltip(font, Lists.newArrayList(Component.translatable("gui.storagenetwork.redstone."
          + containerCableLink.cap.needsRedstone())), Optional.empty(),
          mouseX - leftPos, mouseY - topPos);
    }
    if (btnOperationToggle != null && btnOperationToggle.isMouseOver(mouseX, mouseY)) {
      OpCompareType t = OpCompareType.get(containerCableLink.cap.operationType);
      String two = "gui.storagenetwork.operate.tooltip." + t.word();
      ms.renderTooltip(font, Lists.newArrayList(Component.translatable("gui.storagenetwork.operate.tooltip"),
          Component.translatable(two)),
          Optional.empty(), mouseX - leftPos, mouseY - topPos);
    }
  }

  public static final int SLOT_SIZE = SsnConsts.SQ;

  @Override
  protected void renderBg(GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
    int xCenter = (width - imageWidth) / 2;
    int yCenter = (height - imageHeight) / 2;
    ms.blit(texture, xCenter, yCenter, 0, 0, imageWidth, imageHeight);
    itemSlotsGhost = Lists.newArrayList();
    //TODO: shared with GuiCableIO
    int rows = 2;
    int cols = 9;
    int index = 0;
    int y = 45;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        ItemStack stack = containerCableLink.cap.getFilter().getStackInSlot(index);
        int x = 8 + col * SLOT_SIZE;
        itemSlotsGhost.add(new ItemSlotNetwork(this, stack, leftPos + x, topPos + y, stack.getCount(), leftPos, topPos, true));
        index++;
      }
      //move down to second row
      y += SLOT_SIZE;
    }
    for (ItemSlotNetwork s : itemSlotsGhost) {
      s.drawSlot(ms, font, mouseX, mouseY);
    }
    int x = leftPos + 6;
    y = topPos + 26;
    int size = SsnConsts.SQ;
    operationItemSlot = new ItemSlotNetwork(this, containerCableLink.cap.operationStack, x, y, size, leftPos, topPos, false);
    if (this.isOperationMode()) {
      operationItemSlot.drawSlot(ms, font, mouseX, mouseY);
      ms.blit(ClientEventRegistry.SLOT, x - 1, y - 1, 0, 0, size, size, size, size);
    }
  }

  public void setFilterItems(List<ItemStack> stacks) {
    FilterItemStackHandler filter = this.containerCableLink.cap.getFilter();
    for (int i = 0; i < stacks.size(); i++) {
      ItemStack s = stacks.get(i);
      filter.setStackInSlot(i, s);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    ItemStack stackCarriedByMouse = minecraft.player.containerMenu.getCarried();
    if (operationItemSlot.isMouseOverSlot((int) mouseX, (int) mouseY)) {
      PacketRegistry.INSTANCE.sendToServer(new CableIOMessage(CableIOMessage.CableMessageType.SYNC_OP_STACK.ordinal(), stackCarriedByMouse.copy()));
      return true;
    }
    for (int i = 0; i < this.itemSlotsGhost.size(); i++) {
      ItemSlotNetwork slot = itemSlotsGhost.get(i);
      if (slot.isMouseOverSlot((int) mouseX, (int) mouseY)) {
        if (slot.getStack().isEmpty() == false) {
          //i hit non-empty slot, clear it no matter what
          if (mouseButton == UtilTileEntity.MOUSE_BTN_RIGHT) {
            int direction = hasShiftDown() ? -1 : 1;
            int newCount = Math.min(64, slot.getStack().getCount() + direction);
            if (newCount < 1) {
              newCount = 1;
            }
            slot.getStack().setCount(newCount);
          }
          else {
            slot.setStack(ItemStack.EMPTY);
          }
          this.sendStackSlot(i, slot.getStack());
          return true;
        }
        else {
          //i hit an empty slot, save what im holding
          slot.setStack(stackCarriedByMouse.copy());
          this.sendStackSlot(i, stackCarriedByMouse.copy());
          return true;
        }
      }
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (delta != 0) {
      for (int i = 0; i < this.itemSlotsGhost.size(); i++) {
        ItemSlotNetwork slot = itemSlotsGhost.get(i);
        if (slot.isMouseOverSlot((int) mouseX, (int) mouseY)) {
          ItemStack changeme = ScreenCableImportFilter.scrollStack(delta, slot);
          if (changeme != null) {
            this.sendStackSlot(i, changeme);
            return true;
          }
        }
      }
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isHovering(x, y, width, height, mouseX, mouseY);
  }
}
