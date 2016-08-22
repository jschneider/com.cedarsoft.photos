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
import java.time.Month;
import java.time.ZoneId;
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
  private final ZoneId zoneId;
  @Nonnull
  private final ExifExtractor exifExtractor;

  public LinkByDateCreator(@Nonnull File baseDir, @Nonnull ZoneId zoneId, @Nonnull ExifExtractor exifExtractor) {
    this.baseDir = baseDir;
    this.zoneId = zoneId;
    this.exifExtractor = exifExtractor;
  }

  /**
   * Creates a link for the given source file
   */
  public void createLink(@Nonnull File sourceFile) throws IOException, NotFoundException {
    try (FileInputStream in = new FileInputStream(sourceFile)) {
      ExifInfo exifInfo = exifExtractor.extractInfo(in);

      ZonedDateTime captureTime = exifInfo.getCaptureTime(ZoneOffset.UTC);
      File hourDir = createHourDir(captureTime);
      if (!hourDir.isDirectory()) {
        if (!hourDir.mkdirs()) {
          throw new IOException("Could not create <" + hourDir.getAbsolutePath() + ">");
        }
      }

      String fileName = captureTime.format(DateTimeFormatter.ISO_DATE_TIME) + "_" + exifInfo.getCameraSerial() + "_" + exifInfo.getFileNumber() + "." + exifInfo.getFileTypeExtension();
      File targetFile = new File(hourDir, fileName);

      LinkUtils.createSymbolicLink(sourceFile, targetFile);
    }
  }

  @Nonnull
  private File createHourDir(@Nonnull ZonedDateTime time) {
    File yearDir = new File(baseDir, String.valueOf(time.getYear()));
    File monthDir = new File(yearDir, formatTwoDigits(time.getMonth().getValue()));
    File dayDir = new File(monthDir, formatTwoDigits(time.getDayOfMonth()));
    return new File(dayDir, formatTwoDigits(time.getHour()));
  }

  @Nonnull
  private static String formatTwoDigits(int value) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMinimumIntegerDigits(2);
    return numberFormat.format(value);
  }
}
