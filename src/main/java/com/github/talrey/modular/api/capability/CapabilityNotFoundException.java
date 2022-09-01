package com.github.talrey.modular.api.capability;

public class CapabilityNotFoundException extends RuntimeException {
  public CapabilityNotFoundException() {
    super ("Unable to get requested Capability!");
  }
}
