package com.cedarsoft.photos;

import com.cedarsoft.annotations.NonUiThread;
import com.cedarsoft.crypt.Hash;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImageStorage {
  @Nonnull
  public static final String DATA_FILE_NAME = "data";
  @Nonnull
  private final File baseDir;
  @Nonnull
  private final File deletedBaseDir;

  public ImageStorage(@Nonnull File baseDir, @Nonnull File deletedBaseDir) {
    this.baseDir = baseDir;
    this.deletedBaseDir = deletedBaseDir;

    if (!baseDir.isDirectory()) {
      throw new IllegalArgumentException("Base dir does not exist <" + baseDir.getAbsolutePath() + ">");
    }
  }

  @Nonnull
  @NonUiThread
  File getDataFile(@Nonnull Hash hash) throws IOException {
    File dir = getDir(SplitHash.split(hash));
    return new File(dir, DATA_FILE_NAME);
  }

  /**
   * Returns the dir for the given hash
   */
  @Nonnull
  @NonUiThread
  File getDir(@Nonnull Hash hash) throws IOException {
    return getDir(SplitHash.split(hash));
  }

  @NonUiThread
  @Nonnull
  private File getDir(@Nonnull SplitHash splitHash) throws IOException {
    File firstPartDir = getDir(splitHash.getFirstPart());
    File dir = new File(firstPartDir, splitHash.getLeftover());
    if (!dir.isDirectory()) {
      if (!dir.mkdirs()) {
        throw new IOException("Could not create directory <" + dir.getAbsolutePath() + ">");
      }
    }
    return dir;
  }

  /**
   * Returns the dir for the first part
   */
  @Nonnull
  private File getDir(@Nonnull String firstPart) {
    return new File(baseDir, firstPart);
  }

  @Nonnull
  public File getBaseDir() {
    return baseDir;
  }
}
