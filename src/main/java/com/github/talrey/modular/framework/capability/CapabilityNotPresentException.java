package com.github.talrey.modular.framework.capability;

public class CapabilityNotPresentException extends RuntimeException {
  private static final String MSG = "Required capability is not present!";

  public CapabilityNotPresentException () {
    this(MSG);
  }

  public CapabilityNotPresentException (final String message) {
    super(message);
  }

  public CapabilityNotPresentException (final String message, final Throwable cause) {
    super(message, cause);
  }

  public CapabilityNotPresentException (final Throwable cause) {
    this(MSG, cause);
  }

  public CapabilityNotPresentException (final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
