package com.cedarsoft.photos;

import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.crypt.HashCalculator;
import org.junit.*;
import org.junit.rules.*;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImageStorageTest {
  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private ImageStorage imageStorage;
  private Hash hash;

  @Before
  public void setUp() throws Exception {
    imageStorage = new ImageStorage(tmp.newFolder());
    hash = HashCalculator.calculate(Algorithm.SHA1, "thecontent");
    assertThat(hash.getValueAsHex()).isEqualTo("d958566e96bc10f33f4209fbc2ed05f9096ef9a0");
  }

  @Test
  public void basic() throws Exception {
    File file = imageStorage.getDir(hash);

    assertThat(file.getParentFile()).hasName("d9");
    assertThat(file).hasName("58566e96bc10f33f4209fbc2ed05f9096ef9a0");
    assertThat(file.getParentFile()).exists();
  }

  @Test
  public void dataFile() throws Exception {
    File file = imageStorage.getDataFile(hash);

    assertThat(file.getParentFile().getParentFile()).hasName("d9");
    assertThat(file.getParentFile()).hasName("58566e96bc10f33f4209fbc2ed05f9096ef9a0");
    assertThat(file).hasName("data");
  }
}