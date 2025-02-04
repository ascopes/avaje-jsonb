package io.avaje.jsonb.generator;

import static io.avaje.jsonb.generator.ProcessingContext.createMetaInfWriterFor;
import static io.avaje.jsonb.generator.APContext.createSourceFile;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;

final class SimpleComponentWriter {

  private final ComponentMetaData metaData;
  private final Set<String> importTypes = new TreeSet<>();
  private Append writer;
  private JavaFileObject fileObject;

  SimpleComponentWriter(ComponentMetaData metaData) {
    this.metaData = metaData;
  }

  void initialise() throws IOException {
    var name = metaData.fullName();
    if (fileObject == null) {
      fileObject = createSourceFile(name);
    }
    if (!metaData.isEmpty()) {
      ProcessingContext.validateModule(name);
    }
  }

  private Writer createFileWriter() throws IOException {
    return fileObject.openWriter();
  }

  void write() throws IOException {
    writer = new Append(createFileWriter());
    writePackage();
    writeImports();
    writeClassStart();
    writeRegister();
    writeClassEnd();
    writer.close();
  }

  void writeMetaInf() throws IOException {
    final FileObject fileObject = createMetaInfWriterFor(Constants.META_INF_COMPONENT);
    if (fileObject != null) {
      final Writer writer = fileObject.openWriter();
      writer.write(metaData.fullName());
      writer.close();
    }
  }

  private void writeRegister() {
    writer.append("  @Override").eol();
    writer.append("  public void register(Jsonb.Builder builder) {").eol();
    final List<String> strings = metaData.allFactories();
    for (final String adapterFullName : strings) {
      final String adapterShortName = Util.shortName(adapterFullName);
      writer.append("    builder.add(%s.FACTORY);", adapterShortName).eol();
    }
    for (final String adapterFullName : metaData.all()) {
      final String adapterShortName = Util.shortName(adapterFullName);
      final String typeName = Util.shortType(Util.baseTypeOfAdapter(adapterFullName).replace("$", "."));
      writer.append("    builder.add(%s.class, %s::new);", typeName, adapterShortName).eol();
    }
    writer.append("  }").eol().eol();
  }

  private void writeClassEnd() {
    writer.append("}").eol();
  }

  private void writeClassStart() {
    final String fullName = metaData.fullName();
    final String shortName = Util.shortName(fullName);
    writer.append("@Generated").eol();
    final List<String> factories = metaData.allFactories();
    if (!factories.isEmpty()) {
      writer.append("@MetaData.Factory({");
      writeMetaDataEntry(factories);
      writer.append("})").eol();
    }
    writer.append("@MetaData({");
    final List<String> all = metaData.all();
    writeMetaDataEntry(all);
    writer.append("})").eol();

    writer.append("public class %s implements Jsonb.GeneratedComponent {", shortName).eol().eol();
  }

  private void writeMetaDataEntry(List<String> entries) {
    for (int i = 0, size = entries.size(); i < size; i++) {
      if (i > 0) {
        writer.append(", ");
      }
      writer.append("%s.class", Util.shortName(entries.get(i)));
    }
  }


  private void writeImports() {
    importTypes.add(Constants.JSONB);
    importTypes.add(Constants.JSONB_SPI);
    importTypes.addAll(metaData.allImports());

    for (final String importType : importTypes) {
      if (Util.validImportType(importType)) {
        writer.append("import %s;", importType).eol();
      }
    }
    writer.eol();
  }

  private void writePackage() {
    final String packageName = metaData.packageName();
    if (packageName != null && !packageName.isEmpty()) {
      writer.append("package %s;", packageName).eol().eol();
    }
  }
}
