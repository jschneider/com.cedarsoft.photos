package com.cedarsoft.photos;

import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class LombokTest {
  @Test
  public void run() throws Exception {
    Address address = new Address("max mustermann");
    assertThat(address.getName()).isEqualTo("max mustermann");
  }

  @Test(expected = IllegalAccessException.class)
  public void sneak() throws Exception {
    sneaky();
  }

  @SneakyThrows
  public void sneaky() {
    throw new IllegalAccessException();
  }

  /**
   * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
   */
  @EqualsAndHashCode
  public static class Address {
    @Getter
    @Nonnull
    private final String name;

    public Address(@Nonnull String name) {
      this.name = name;
    }

    public void doit() throws IOException {
      @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      System.out.println("asdfasdf");
    }
  }
}
