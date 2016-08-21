package com.cedarsoft.photos;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImportRunner {
  public static void main(String[] args) throws IOException {
    Injector injector = Guice.createInjector(new StorageModule());

    Importer importer = injector.getInstance(Importer.class);
    importer.importDirectory(new File("/media/mule/data/media/photos/collustra/to-import/2016-08-18"));
  }
}
