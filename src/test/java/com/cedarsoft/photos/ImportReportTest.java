package com.cedarsoft.photos;

import com.cedarsoft.crypt.Algorithm;
import com.cedarsoft.crypt.Hash;
import com.google.common.collect.ImmutableList;
import org.junit.*;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class ImportReportTest {
  @Test
  public void basic() throws Exception {
    ImportReport importReport = new ImportReport(ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
    assertThat(importReport.alreadyExisting()).isEmpty();
  }

  @Test
  public void builder() throws Exception {
    ImportReport importReport = ImportReport.builder()
      .imported(new Hash(Algorithm.SHA256, "asdf".getBytes()))
      .existing(new Hash(Algorithm.SHA256, "2".getBytes()))
      .createdLink(new File("asdf"))
      .build();

    assertThat(importReport.alreadyExisting()).hasSize(1);
    assertThat(importReport.importedHashes()).hasSize(1);
    assertThat(importReport.createdLinks()).hasSize(1);

  }
}