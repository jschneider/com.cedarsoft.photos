package com.cedarsoft.photos;

import com.cedarsoft.photos.di.Modules;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImportRunner {
  public static void main(String[] args) throws IOException {
    Injector injector = Guice.createInjector(Modules.getModules());

    Importer importer = injector.getInstance(Importer.class);
    importer.importDirectory(new File("/media/mule/data/media/photos/import/collustra/to-import/2016-08-18"), new Importer.Listener() {
      @Override
      public void skipped(@Nonnull File fileToImport, @Nonnull File targetFile) {
        System.out.println("Skipped " + fileToImport);
      }

      @Override
      public void imported(@Nonnull File fileToImport, @Nonnull File targetFile) {
        System.out.println("Imported " + fileToImport + " --> " + targetFile.getParentFile().getParentFile().getName() + "/" + targetFile.getParentFile().getName());
      }
    });
  }
}
