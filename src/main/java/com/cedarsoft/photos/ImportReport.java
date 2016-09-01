package com.cedarsoft.photos;

import com.cedarsoft.crypt.Hash;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Contains the report for imported files
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
@Builder
@Value
@Accessors(fluent = true)
public class ImportReport {
  @Singular("imported")
  @Getter
  @Nonnull
  private final ImmutableList<? extends Hash> importedHashes;
  @Singular("existing")
  @Getter
  @Nonnull
  private final ImmutableList<? extends Hash> alreadyExisting;
  @Singular
  @Getter
  @Nonnull
  private final ImmutableList<? extends File> createdLinks;
}
