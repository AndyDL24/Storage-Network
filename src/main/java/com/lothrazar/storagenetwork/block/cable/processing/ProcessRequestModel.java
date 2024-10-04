package com.lothrazar.storagenetwork.block.cable.processing;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

//Not done but planned:
//{A}Text box input for priority (all)
//{B}CLONE settings between multiple cables
public class ProcessRequestModel {

  //EXPORTING:from network to inventory (to start crafting, add ingredients) . also default state
  //IMPORTING: from inventory to network (after crafting is done)
  public enum ProcessStatus {
    HALTED, IMPORTING, EXPORTING;
  }

  private static final String PREFIX = "sn_process_";
  //you can request more than 64
  private int count;
  private boolean alwaysActive = false;
  private ProcessStatus status = ProcessStatus.EXPORTING;
  // current stack (in order of filters) being exported or imported
  private int stackIndex;
  private Direction inputFace = Direction.UP; // processingTop IE top of furnace
  private Direction outputFace = Direction.DOWN; // processingBottom IE pulling output from the furnace

  public int getCount() {
    return count;
  }

  public void reduceCount() {
    if (count > 0) {
      count--;
    }
  }

  public void setCount(int countRequested) {
    if (count <= 0 && countRequested > 0) {
      //if we are going from zero to non zero, kickstart the thing
      setStatus(ProcessStatus.EXPORTING);
    }
    this.count = countRequested;
  }

  public void readFromNBT(CompoundTag compound) {
    this.count = compound.getInt(PREFIX + "count");
    this.status = ProcessStatus.values()[compound.getInt(PREFIX + "status")];
    this.alwaysActive = compound.getBoolean(PREFIX + "always");
    this.stackIndex = compound.getInt(PREFIX + "stack");
    this.inputFace = Direction.values()[compound.getInt(PREFIX + "in")];
    this.outputFace = Direction.values()[compound.getInt(PREFIX + "out")];
  }

  public CompoundTag writeToNBT(CompoundTag compound) {
    compound.putInt(PREFIX + "count", count);
    compound.putInt(PREFIX + "status", status.ordinal());
    compound.putBoolean(PREFIX + "always", alwaysActive);
    compound.putInt(PREFIX + "stack", stackIndex);
    compound.putInt(PREFIX + "in", this.inputFace.ordinal());
    compound.putInt(PREFIX + "out", this.outputFace.ordinal());
    return compound;
  }

  public ProcessStatus getStatus() {
    return status;
  }

  public void setStatus(ProcessStatus status) {
    if (status == this.status) {
      return;
    }
    // reset stack index on status change
    this.status = status;
    this.stackIndex = 0;
  }

  public boolean isAlwaysActive() {
    return alwaysActive;
  }

  public void setAlwaysActive(boolean alwaysActive) {
    this.alwaysActive = alwaysActive;
  }

  public int getStackIndex() {
    return stackIndex;
  }

  public void increaseStackIndex() {
    this.stackIndex++;
  }

  public Direction getInputFace() {
    return inputFace;
  }

  public void setInputFace(Direction inputFace) {
    this.inputFace = inputFace;
  }

  public Direction getOutputFace() {
    return outputFace;
  }

  public void setOutputFace(Direction outputFace) {
    this.outputFace = outputFace;
  }
}
