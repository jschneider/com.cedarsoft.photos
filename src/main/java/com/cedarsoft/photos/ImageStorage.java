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
  private final File baseDir;

  public ImageStorage(@Nonnull File baseDir) {
    this.baseDir = baseDir;

    if (!baseDir.isDirectory()) {
      throw new IllegalArgumentException("Base dir does not exist <" + baseDir.getAbsolutePath() + ">");
    }
  }

  /**
   * Returns the file for the given hash
   */
  @Nonnull
  @NonUiThread
  File getFile(@Nonnull Hash hash) throws IOException {
    return getFile(SplitHash.split(hash));
  }

  @NonUiThread
  @Nonnull
  private File getFile(@Nonnull SplitHash splitHash) throws IOException {
    File dir = getDir(splitHash.getFirstPart());
    if (!dir.isDirectory()) {
      if (!dir.mkdir()) {
        throw new IOException("Could not create directory <" + dir.getAbsolutePath() + ">");
      }
    }
    return new File(dir, splitHash.getLeftover());
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
