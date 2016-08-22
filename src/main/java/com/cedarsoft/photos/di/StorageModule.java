package com.cedarsoft.photos.di;

import com.cedarsoft.photos.ImageStorage;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;
import java.io.File;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StorageModule extends AbstractModule {
  @Override
  protected void configure() {
    File baseDir = new File("/media/mule/data/media/photos/backend");
    bind(ImageStorage.class).toInstance(new ImageStorage(baseDir));
  }
}
