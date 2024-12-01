package com.lothrazar.storagenetwork.api;

import com.lothrazar.storagenetwork.block.main.TileMain;
import net.minecraft.core.Direction;

/**
 * Only expose this capability if you want your cable/block to auto-export and import blocks controlled by the networks main. You could quite as well just expose {@link IConnectable} and do the
 * exporting/importing in your own update() method.
 * <p>
 * If you indeed want to add another exporting/importing cable in the style of the integrated ones, this might be for you. In all other cases, this is probably not what you want.
 */
public interface IConnectableItemProcessing {

  Direction facingInventory();

  /**
   * Storages with a higher priority (== lower number) are processed first. You probably want to add a way to configure the priority of your storage.
   *
   * @return Return the priority here
   */
  int getPriority();

  void setPriority(int value);

  void execute(TileMain main);
  /**
   * TODO:
   * 
   * get list of recipe outputs, for requests
   * 
   * @return
   */
}
