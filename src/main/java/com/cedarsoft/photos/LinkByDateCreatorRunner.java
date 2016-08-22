package com.cedarsoft.photos;

import com.cedarsoft.photos.di.Modules;
import com.cedarsoft.photos.di.StorageModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class LinkByDateCreatorRunner {
  public static void main(String[] args) throws IOException {
    System.out.println("Creating links by date");

    Injector injector = Guice.createInjector(Modules.getModules());
    LinkByDateCreator linkByDateCreator = injector.getInstance(LinkByDateCreator.class);

    ImageStorage storage = injector.getInstance(ImageStorage.class);

    //link all files
    @Nullable File[] subDirs = storage.getBaseDir().listFiles();
    assert subDirs != null;
    for (File subDir : subDirs) {
      if (!subDir.isDirectory()) {
        continue;
      }

      File[] files = subDir.listFiles();
      assert files != null;
      for (File file : files) {
        System.out.println("Creating link for <" + file + ">");
        linkByDateCreator.createLink(file);
      }
    }
  }
}
