package com.cedarsoft.photos;

import com.cedarsoft.exceptions.NotFoundException;
import com.cedarsoft.io.LinkUtils;
import com.cedarsoft.photos.exif.ExifExtractor;
import com.cedarsoft.photos.exif.ExifInfo;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creates symlinks by date
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class LinkByDateCreator {
  @Nonnull
  private final File baseDir;
  @Nonnull
  private final ExifExtractor exifExtractor;

  public LinkByDateCreator(@Nonnull File baseDir, @Nonnull ExifExtractor exifExtractor) {
    this.baseDir = baseDir;
    this.exifExtractor = exifExtractor;
  }

  /**
   * Creates a link for the given source file
   */
  public void createLink(@Nonnull File sourceFile) throws IOException, NotFoundException {
    try (FileInputStream in = new FileInputStream(sourceFile)) {
      ExifInfo exifInfo = exifExtractor.extractInfo(in);

      ZonedDateTime captureTime = exifInfo.getCaptureTime(ZoneOffset.UTC);

      //Store in the hour
      File hourDir = createHourDir(captureTime);

      //The target directory (uses the extension as directory name)
      File targetDir;
      String extension = exifInfo.getFileTypeExtension();
      if (isRaw(extension)) {
        targetDir = new File(hourDir, extension);
      }
      else {
        targetDir = hourDir;
      }
      ensureDirExists(targetDir);

      String fileName = captureTime.format(DateTimeFormatter.ISO_DATE_TIME) + "_" + exifInfo.getCameraSerial() + "_" + exifInfo.getFileNumber() + "." + extension;
      File targetFile = new File(targetDir, fileName);

      LinkUtils.createSymbolicLink(sourceFile, targetFile);
    }
  }

  //TODO move
  private static boolean isRaw(@Nonnull String extension) {
    return extension.equalsIgnoreCase("cr2");
  }

  private void ensureDirExists(File hourDir) throws IOException {
    if (!hourDir.isDirectory()) {
      if (!hourDir.mkdirs()) {
        throw new IOException("Could not create <" + hourDir.getAbsolutePath() + ">");
      }
    }
  }

  @Nonnull
  private File createHourDir(@Nonnull ZonedDateTime time) {
    File dayDir = createDayDir(time);
    return new File(dayDir, formatTwoDigits(time.getHour()));
  }

  @Nonnull
  private File createDayDir(@Nonnull ZonedDateTime time) {
    File monthDir = createMonthDir(time);
    return new File(monthDir, formatTwoDigits(time.getDayOfMonth()));
  }

  @Nonnull
  private File createMonthDir(@Nonnull ZonedDateTime time) {
    File yearDir = createYearDir(time);
    return new File(yearDir, formatTwoDigits(time.getMonth().getValue()));
  }

  @Nonnull
  private File createYearDir(@Nonnull ZonedDateTime time) {
    return new File(baseDir, String.valueOf(time.getYear()));
  }

  @Nonnull
  private static String formatTwoDigits(int value) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMinimumIntegerDigits(2);
    return numberFormat.format(value);
  }
}
