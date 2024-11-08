package org.example;

import org.apache.ignite.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleService implements Service {
  public static final Logger LOG = LogManager.getLogger();

  private final SerializableSupplier<String> textSupplier;

  public SimpleService(SerializableSupplier<String> textSupplier) {
    this.textSupplier = textSupplier;
  }

  @Override
  public void init() {
    LOG.info("Init {}!", getClass().getSimpleName());
  }

  @Override
  public void execute() {
    LOG.info(textSupplier.get());
  }

  @Override
  public void cancel() {
    LOG.info("Cancel {}!", getClass().getSimpleName());
  }
}
