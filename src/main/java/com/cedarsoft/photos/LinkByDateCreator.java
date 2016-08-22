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
import java.time.format.TextStyle;
import java.util.Locale;

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

      String extension = exifInfo.getFileTypeExtension();
      String fileName = captureTime.format(DateTimeFormatter.ISO_DATE_TIME) + "_" + exifInfo.getCameraSerial() + "_" + exifInfo.getFileNumber() + "." + extension;

      //Store in the day
      createLink(sourceFile, new File(createDayDir(captureTime), "all"), fileName, extension);

      //Store in the hour
      createLink(sourceFile, createHourDir(captureTime), fileName, extension);
    }
  }

  /**
   * Creates a link
   */
  private void createLink(@Nonnull File sourceFile, @Nonnull File targetDir, @Nonnull String targetFileName, @Nonnull String extension) throws IOException {
    //The target directory (uses the extension as directory name)
    File targetDirWithExtension;
    if (isRaw(extension)) {
      targetDirWithExtension = new File(targetDir, extension);
    }
    else {
      targetDirWithExtension = targetDir;
    }
    ensureDirExists(targetDirWithExtension);

    File targetFile = new File(targetDirWithExtension, targetFileName);
    LinkUtils.createSymbolicLink(sourceFile, targetFile);
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
    String dirName = formatTwoDigits(time.getDayOfMonth()) + "_" + time.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.US);
    return new File(monthDir, dirName);
  }

  @Nonnull
  private File createMonthDir(@Nonnull ZonedDateTime time) {
    File yearDir = createYearDir(time);
    String dirName = formatTwoDigits(time.getMonth().getValue()) + "_" + time.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    return new File(yearDir, dirName);
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
