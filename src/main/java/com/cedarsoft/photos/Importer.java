package com.cedarsoft.photos;

import com.cedarsoft.annotations.NonUiThread;
import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.crypt.HashCalculator;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Imports photos from a given directory.
 * Creates hard links
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Importer {
  private static final Logger LOG = Logger.getLogger(Importer.class.getName());

  @Nonnull
  private static final Set<String> SUPPORTED_FILE_SUFFICIES = ImmutableSet.of("jpeg", "jpg", "cr2");

  public static final Algorithm ALGORITHM = Algorithm.SHA256;

  @Nonnull
  private final ImageStorage imageStorage;

  @Inject
  public Importer(@Nonnull ImageStorage imageStorage) {
    this.imageStorage = imageStorage;
  }

  /**
   * Imports the given file
   */
  @NonUiThread
  public void importFile(@Nonnull File fileToImport) throws IOException {
    LOG.fine("Importing <" + fileToImport + ">");
    Hash hash = HashCalculator.calculate(ALGORITHM, fileToImport);

    File targetFile = imageStorage.getFile(hash);
    if (targetFile.exists()) {
      LOG.fine("skipping file, already exists");
      return;
    }

    File dir = targetFile.getParentFile();

    //Set writable before
    dir.setWritable(true, true);
    try {
      Files.copy(fileToImport.toPath(), targetFile.toPath());
      //Set the file to read only
      targetFile.setWritable(false);
      targetFile.setExecutable(false);
    } finally {
      //Set to read only
      dir.setWritable(false, false);
    }
  }

  /**
   * Imports all files within the given directory
   */
  @NonUiThread
  public void importDirectory(@Nonnull File directory) throws IOException {
    if (!directory.isDirectory()) {
      throw new FileNotFoundException("Not a directory <" + directory.getAbsolutePath() + ">");
    }

    @Nullable File[] files = directory.listFiles();
    if (files == null) {
      throw new IllegalStateException("Could not list files in <" + directory.getAbsolutePath() + ">");
    }

    for (File file : files) {
      if (isSupported(file)) {
        importFile(file);
      }

      //Import all files from the sub directories
      if (file.isDirectory()) {
        importDirectory(file);
      }
    }
  }

  /**
   * Returns whether the given file is supported
   */
  private static boolean isSupported(@Nonnull File file) {
    String fileNameLowerCase = file.getName().toLowerCase();

    for (String supportedFileSuffix : SUPPORTED_FILE_SUFFICIES) {
      if (fileNameLowerCase.endsWith("." + supportedFileSuffix)) {
        return true;
      }
    }

    return false;
  }
}
