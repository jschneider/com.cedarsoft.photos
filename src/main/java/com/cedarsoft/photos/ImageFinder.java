package com.cedarsoft.photos;

import com.cedarsoft.annotations.NonUiThread;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImageFinder {
  private static final Logger LOG = Logger.getLogger(ImageFinder.class.getName());

  @Nonnull
  private final ImageStorage storage;

  @Inject
  public ImageFinder(@Nonnull ImageStorage storage) {
    this.storage = storage;
  }

  @NonUiThread
  public void find(@Nonnull Consumer consumer) throws IOException {
    @Nullable File[] firstPartHashDirs = storage.getBaseDir().listFiles();

    assert firstPartHashDirs != null;
    for (File firstPartHashDir : firstPartHashDirs) {
      if (!firstPartHashDir.isDirectory()) {
        LOG.warning("Unexpected file found: " + firstPartHashDir.getAbsolutePath());
        continue;
      }

      File[] remainingPartHashDirs = firstPartHashDir.listFiles();
      assert remainingPartHashDirs != null;
      for (File remainingPartHashDir : remainingPartHashDirs) {
        File dataFile = new File(remainingPartHashDir, ImageStorage.DATA_FILE_NAME);
        if (!dataFile.exists()) {
          LOG.warning("Missing data file: <" + dataFile.getAbsolutePath() + ">");
          continue;
        }

        Hash hash = Hash.fromHex(Algorithm.SHA1, firstPartHashDir.getName() + "" + remainingPartHashDir.getName());
        consumer.found(storage, dataFile, hash);
      }
    }
  }

  /**
   * Consumer for found images
   */
  @FunctionalInterface
  public interface Consumer {
    /**
     * Is called for each data file that has been found
     */
    @NonUiThread
    void found(@Nonnull ImageStorage storage, @Nonnull File dataFile, @Nonnull Hash hash) throws IOException;
  }
}
