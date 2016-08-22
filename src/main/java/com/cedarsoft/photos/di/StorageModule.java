package com.cedarsoft.photos.di;

import com.cedarsoft.photos.ImageStorage;
import com.cedarsoft.photos.LinkByDateCreator;
import com.cedarsoft.photos.exif.ExifExtractor;
import com.cedarsoft.photos.exif.ExifTool;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.File;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class StorageModule extends AbstractModule {
  @Override
  protected void configure() {
    File storageBaseDir = new File("/media/mule/data/media/photos/backend");
    bind(ImageStorage.class).toInstance(new ImageStorage(storageBaseDir));
  }

  @Singleton
  @Provides
  @Nonnull
  public ExifTool provideExifTool() {
    File bin = new File("/usr/bin/exiftool");
    if (!bin.exists()) {
      throw new IllegalStateException("No exiftool installed.");
    }
    return new ExifTool(bin);
  }

  @Provides
  @Nonnull
  @Singleton
  public LinkByDateCreator provideLinkByDateCreator(@Nonnull ExifExtractor exifExtractor) {
    File byDateBaseDir = new File("/media/mule/data/media/photos/by-date");
    return new LinkByDateCreator(byDateBaseDir, exifExtractor);
  }
}
