package com.cedarsoft.photos;

import com.cedarsoft.photos.di.Modules;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class LinkByDateCreatorRunner {
  public static void main(String[] args) throws IOException {
    System.out.println("Creating links by date");

    Injector injector = Guice.createInjector(Modules.getModules());
    LinkByDateCreator linkByDateCreator = injector.getInstance(LinkByDateCreator.class);

    linkByDateCreator.createLink(new File("/media/mule/data/media/photos/backend/ff/716a25782bdaad8e76a7fb7a4dd740d9ed7fc358515c71f8c919b0590d8e5c"));
  }
}
