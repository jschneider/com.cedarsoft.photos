package com.cedarsoft.photos;

import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.cedarsoft.crypt.HashCalculator;
import org.junit.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class SplitHashTest {
  @Test
  public void basic() throws Exception {
    Hash hash = HashCalculator.calculate(Algorithm.SHA1, "thecontent");
    SplitHash splitHash = SplitHash.split(hash);

    assertThat(hash.getValueAsHex()).isEqualTo("d958566e96bc10f33f4209fbc2ed05f9096ef9a0");
    assertThat(splitHash.getFirstPart()).isEqualTo("d9");
    assertThat(splitHash.getLeftover()).isEqualTo("58566e96bc10f33f4209fbc2ed05f9096ef9a0");
  }
}