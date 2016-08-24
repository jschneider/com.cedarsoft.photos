package com.cedarsoft.photos;

import com.cedarsoft.crypt.Hash;
import com.cedarsoft.photos.di.Modules;
import com.cedarsoft.photos.tools.exif.ExifInfo;
import com.cedarsoft.photos.tools.imagemagick.Identify;
import com.cedarsoft.photos.tools.imagemagick.ImageInformation;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Finds small images
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class DeleteSmallImagesRunner {
  public static void main(String[] args) throws IOException {
    Injector injector = Guice.createInjector(Modules.getModules());

    ImageFinder imageFinder = injector.getInstance(ImageFinder.class);
    Identify identify = injector.getInstance(Identify.class);

    imageFinder.find((storage, dataFile, hash) -> {
      try {
        System.out.print(".");

        File dir = dataFile.getParentFile();
        File exifFile = new File(dir, "exif");
        if (!exifFile.exists()) {
          System.out.println("Skipping - no exif file found: " + exifFile.getAbsolutePath());
          return;
        }

        ExifInfo exifInfo;
        try (FileInputStream in = new FileInputStream(exifFile)) {
          exifInfo = new ExifInfo(in);
        }
        if (LinkByDateCreator.isRaw(exifInfo.getFileTypeExtension())) {
          return;
        }

        ImageInformation imageInformation = identify.getImageInformation(dataFile);
        if ((imageInformation.getResolution().getWidth() < 2000) && (imageInformation.getResolution().getHeight() < 2000)) {
          System.out.println("\n--> Small Image");

          if (false) {
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
