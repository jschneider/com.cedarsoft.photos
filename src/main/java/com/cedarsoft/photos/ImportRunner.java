package com.cedarsoft.photos;

import com.cedarsoft.photos.di.Modules;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImportRunner {
  public static void main(String[] args) throws IOException {
    Injector injector = Guice.createInjector(Modules.getModules());

    LinkByDateCreator linkByDateCreator = injector.getInstance(LinkByDateCreator.class);

    List<File> failedLinks = new ArrayList<>();

    Importer importer = injector.getInstance(Importer.class);
    importer.importDirectory(new File("/media/mule/data/media/photos/import/done"), new Importer.Listener() {
      @Override
      public void skipped(@Nonnull File fileToImport, @Nonnull File targetFile) {
        try {
          linkByDateCreator.createLink(targetFile).getAbsolutePath();
        } catch (Exception e) {
          e.printStackTrace();
          failedLinks.add(targetFile);
        }
      }

      @Override
      public void imported(@Nonnull File fileToImport, @Nonnull File targetFile) {
        System.out.println("Imported " + fileToImport + " --> " + targetFile.getParentFile().getParentFile().getName() + "/" + targetFile.getParentFile().getName());
        try {
          System.out.println("\t" + linkByDateCreator.createLink(targetFile).getAbsolutePath());
        } catch (Exception e) {
          e.printStackTrace();
          failedLinks.add(targetFile);
        }
      }
    });

    System.out.println("# Import finished ################");
    System.out.println("Failed links:");

    for (File failedLink : failedLinks) {
      System.out.println("\t" + failedLink.getAbsolutePath());
    }
  }
}
