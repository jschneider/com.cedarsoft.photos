package com.cedarsoft.photos;

import com.cedarsoft.image.Resolution;
import com.cedarsoft.photos.di.Modules;
import com.cedarsoft.photos.exif.ExifExtractor;
import com.cedarsoft.photos.exif.ExifInfo;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;

/**
 * Finds small images
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class FindSmallImagesRunner {
  public static void main(String[] args) throws IOException {
    Injector injector = Guice.createInjector(Modules.getModules());

    ImageFinder imageFinder = injector.getInstance(ImageFinder.class);
    ExifExtractor exifExtractor = injector.getInstance(ExifExtractor.class);

    imageFinder.find((storage, dataFile) -> {
      System.out.println("\t\t" + dataFile.getAbsolutePath());
      ExifInfo exifInfo = exifExtractor.extractInfo(dataFile);
      Resolution dimension = exifInfo.getDimension();

      if ((dimension.getWidth() <= 2000) && (dimension.getHeight() <= 2000)) {
        System.out.println("--> Small image found: <" + dataFile.getAbsolutePath() + "> (" + dimension + ")");
        File dir = dataFile.getParentFile();
        dir.setWritable(true);
        dataFile.setWritable(true);
        boolean deleted = dataFile.delete();
        if (!deleted) {
          throw new IOException("Could not delete <" + dataFile.getAbsolutePath() + ">");
        }
        //Delete the parent dir
        dir.delete();
      }
    });
  }
}
