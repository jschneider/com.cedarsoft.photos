package com.cedarsoft.photos;

import com.cedarsoft.io.FileOutputStreamWithMove;
import com.cedarsoft.photos.di.Modules;
import com.cedarsoft.photos.tools.exif.ExifExtractor;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ExtractExifsRunner {
  public static void main(String[] args) throws IOException {
    System.out.println("Extracting exif files");

    Injector injector = Guice.createInjector(Modules.getModules());
    ExifExtractor exifExtractor = injector.getInstance(ExifExtractor.class);

    ImageFinder imageFinder = injector.getInstance(ImageFinder.class);
    imageFinder.find((storage, dataFile, hash) -> {
      System.out.println("\t\tExtracting exif for " + dataFile);
      File dir = dataFile.getParentFile();
      File exifFile = new File(dir, "exif");
      if (exifFile.exists()) {
        return;
      }

      dir.setWritable(true);
      try (FileInputStream in = new FileInputStream(dataFile); FileOutputStreamWithMove out = new FileOutputStreamWithMove(exifFile)) {
        exifExtractor.extractDetailed(in, out);
      } finally {
        dir.setWritable(false);
      }
    });
  }
}
