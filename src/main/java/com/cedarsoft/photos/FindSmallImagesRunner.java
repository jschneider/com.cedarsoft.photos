package com.cedarsoft.photos;

import com.cedarsoft.photos.di.Modules;
import com.cedarsoft.photos.exif.ExifExtractor;
import com.cedarsoft.photos.exif.ExifInfo;
import com.cedarsoft.photos.imagemagick.Identify;
import com.cedarsoft.photos.imagemagick.ImageInformation;
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
    Identify identify = injector.getInstance(Identify.class);
    ExifExtractor exifExtractor = injector.getInstance(ExifExtractor.class);

    imageFinder.find((storage, dataFile) -> {
      try {
        System.out.print(".");

        ExifInfo exifInfo = exifExtractor.extractInfo(dataFile);
        if (LinkByDateCreator.isRaw(exifInfo.getFileTypeExtension())) {
          return;
        }

        ImageInformation imageInformation = identify.getImageInformation(dataFile);
        if ((imageInformation.getResolution().getWidth() < 2000) && (imageInformation.getResolution().getHeight() < 2000)) {
          System.out.println("\n--> Small Image");

          if (false) {
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
        }
      } catch (Exception e) {
        System.err.println("Problem when checking " + dataFile.getAbsolutePath());
        e.printStackTrace();
        System.exit(1);
      }
    });
  }
}
