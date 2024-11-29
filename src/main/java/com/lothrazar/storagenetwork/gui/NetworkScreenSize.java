package com.lothrazar.storagenetwork.gui;
public enum NetworkScreenSize {
  //normal has the crafting table
  //large is the original inventory and remote with no crafting table
  NORMAL, LARGE, EXPANDED;

  public int lines() {
    switch (this) {
      case NORMAL:
        return 4;
      case LARGE:
        return 4 * 2;
      case EXPANDED:
        return 4 * 5 + 1;
    }
    return 0;
  }

  public int columns() {
    switch (this) {
      case NORMAL:
      case LARGE:
        return 9;
      case EXPANDED:
        return 25;
    }
    return 0;
  }

  public boolean isCrafting() {
    return this != LARGE;
  }
}