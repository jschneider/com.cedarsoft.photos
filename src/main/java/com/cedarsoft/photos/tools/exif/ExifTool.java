/**
 * Copyright (C) cedarsoft GmbH.
 * <p>
 * Licensed under the GNU General Public License version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.cedarsoft.org/gpl3
 * <p>
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 * <p>
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * <p>
 * You should have received a copy of the GNU General Public License version
 * 3 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * <p>
 * Please contact cedarsoft GmbH, 72810 Gomaringen, Germany,
 * or visit www.cedarsoft.com if you need additional information or
 * have any questions.
 */

package com.cedarsoft.photos.tools.exif;

import com.cedarsoft.execution.OutputRedirector;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for exiftool
 */
//TODO add SequenceNumber
public class ExifTool {
  @Nonnull
  private final File bin;

  public ExifTool(@Nonnull File bin) {
    if (!bin.exists()) {
      throw new IllegalArgumentException("bin does not exist " + bin.getAbsolutePath());
    }
    this.bin = bin;
  }

  public ExifTool(@Nonnull String bin) {
    this(new File(bin));
  }

  public void run(@Nonnull InputStream source, @Nonnull OutputStream out, @Nonnull String... args) throws IOException {
    List<String> commands = new ArrayList<>();
    commands.add(bin.getAbsolutePath());
    commands.addAll(Arrays.asList(args));

    ProcessBuilder builder = new ProcessBuilder(commands);
    Process process = builder.start();

    Thread outputRedirectingThread = new Thread(new OutputRedirector(process.getInputStream(), out), "output stream redirection thread");
    outputRedirectingThread.start();

    //Copy the content of in to the output stream
    IOUtils.copy(source, process.getOutputStream());
    process.getOutputStream().close();

    try {
      int result = process.waitFor();
      outputRedirectingThread.join();
      if (result != 0) {
        throw new IOException("Conversion failed due to: " + IOUtils.toString(process.getErrorStream()));
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void run(@Nonnull File source, @Nullable OutputStream out, @Nonnull String... args) throws IOException {
    List<String> commands = new ArrayList<>();
    commands.add(bin.getAbsolutePath());
    commands.addAll(Arrays.asList(args));
    commands.add(source.getAbsolutePath());

    ProcessBuilder builder = new ProcessBuilder(commands);

    Process process = builder.start();

    Thread outputRedirectingThread = null;
    if (out != null) {
      outputRedirectingThread = new Thread(new OutputRedirector(process.getInputStream(), out), "output stream redirection thread");
      outputRedirectingThread.start();
    }

    try {
      int result = process.waitFor();
      if (outputRedirectingThread != null) {
        outputRedirectingThread.join();
      }
      if (result != 0) {
        throw new IOException("Failed due to: " + IOUtils.toString(process.getErrorStream()));
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void clearRotation(@Nonnull File target) throws IOException {
    run(target, null, "-P", "-overwrite_original", "-Orientation=normal");
  }
}
