package com.cedarsoft.photos.imagemagick;

import com.cedarsoft.execution.OutputRedirector;
import com.cedarsoft.image.Resolution;
import com.google.common.base.Splitter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Identify {
  @Nonnull
  private final File bin;

  public Identify(@Nonnull File bin) {
    if (!bin.exists()) {
      throw new IllegalArgumentException("bin does not exist " + bin.getAbsolutePath());
    }
    this.bin = bin;
  }

  public void run(@Nonnull OutputStream out, @Nonnull String... args) throws IOException {
    List<String> commands = new ArrayList<>();
    commands.add(bin.getAbsolutePath());
    commands.addAll(Arrays.asList(args));

    ProcessBuilder builder = new ProcessBuilder(commands);
    Process process = builder.start();

    Thread outputRedirectingThread = new Thread(new OutputRedirector(process.getInputStream(), out), "output stream redirection thread");
    outputRedirectingThread.start();

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

  public ImageInformation getIdentify(@Nonnull File file) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    run(out, file.getAbsolutePath());

    String output = out.toString(StandardCharsets.UTF_8);
    List<String> parts = Splitter.on(' ').splitToList(output);

    if (parts.size() < 6) {
      throw new IllegalStateException("Could not parse output <" + output + ">");
    }

    String type = parts.get(1);
    String dimensionString = parts.get(2);

    String[] dimensionParts = dimensionString.split("x");
    if (dimensionParts.length != 2) {
      throw new IllegalStateException("Invalid dimension <" + dimensionString + "> in output <" + output + ">");
    }

    int width = Integer.parseInt(dimensionParts[0]);
    int height = Integer.parseInt(dimensionParts[1]);

    return new ImageInformation(type, width, height);
  }
}
