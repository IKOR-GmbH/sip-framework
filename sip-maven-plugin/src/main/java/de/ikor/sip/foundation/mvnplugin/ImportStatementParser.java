package de.ikor.sip.foundation.mvnplugin;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import de.ikor.sip.foundation.mvnplugin.model.ImportStatement;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Parses a source file into a {@link ParsedJavaFile} representation. */
public final class ImportStatementParser {
  public static final String STATIC_PREFIX = "static ";
  private static final String IMPORT_PREFIX = "import ";
  private final LineReader lineReader;

  /**
   * Constructor just for testing purposes.
   *
   * @param charset used by te analysed project.
   */
  ImportStatementParser(Charset charset) {
    this.lineReader = new LineReader(charset);
  }

  /**
   * Converts files into more suitable {@link ParsedJavaFile} format.
   *
   * @param sourceFilePath path of the given file
   * @return instance of {@link ParsedJavaFile} containing encapsulated information of matter about
   *     the file.
   */
  public ParsedJavaFile parse(Path sourceFilePath) {
    final List<ImportStatement> imports = new ArrayList<>();

    final String fileName = getFileNameWithoutExtension(sourceFilePath);
    String packageName;
    try (final Stream<String> lines = this.lineReader.lines(sourceFilePath).stream()) {
      int row = 1;

      JavaDocBuilder builder = new JavaDocBuilder();
      builder.addSource(new FileReader(sourceFilePath.toString()));
      JavaSource src = builder.getSources()[0];
      String[] importStrings = src.getImports();

      packageName = src.getPackage();
      String fqcn = guessFQCN(packageName, fileName);
      for (final Iterator<String> it = lines.map(String::trim).iterator(); it.hasNext(); ++row) {
        final String line = it.next();

        // Implementation note: We check for empty lines here
        // so that we are able to keep track of correct line numbers.
        if (line.isEmpty()) {
          continue;
        }
        if (Arrays.stream(importStrings).anyMatch(line::contains)) {
          final List<ImportStatement> importStatements = parseImport(line, row);
          imports.addAll(importStatements);
        }
      }

      return new ParsedJavaFile(sourceFilePath, fqcn, imports);
    } catch (final IOException e) {
      throw new UncheckedIOException(
          format("Encountered IOException while analyzing %s for banned imports", sourceFilePath),
          e);
    }
  }

  private String guessFQCN(String packageName, String sourceFileName) {
    return isEmpty(packageName) ? sourceFileName : packageName + "." + sourceFileName;
  }

  private String getFileNameWithoutExtension(Path file) {
    final String s = file.getFileName().toString();
    final int i = s.lastIndexOf(".");
    return s.substring(0, i);
  }

  public List<ImportStatement> parseImport(String line, int lineNumber) {
    if (!isImport(line)) {
      return Collections.emptyList();
    }

    // There can be multiple import statements within the same line, so
    // we simply split them at their ';'
    final String[] parts = line.split(";");
    return Arrays.stream(parts)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(s -> s.substring(IMPORT_PREFIX.length()))
        .map(importName -> toImportStatement(importName, lineNumber))
        .collect(Collectors.toList());
  }

  private ImportStatement toImportStatement(String importName, int lineNumber) {
    String trimmedImportName = importName.replace(STATIC_PREFIX, "").trim();
    boolean isStaticImport = importName.startsWith(STATIC_PREFIX);

    return new ImportStatement(trimmedImportName, lineNumber, isStaticImport);
  }

  private boolean isImport(String line) {
    return line.startsWith(IMPORT_PREFIX) && line.endsWith(";");
  }

  /**
   * Supplies lines but skips every encountered comment. Block comments that span multiple lines
   * will be replaced by the same amount of empty lines.
   */
  private static class LineReader {

    private final Charset charset;

    public LineReader(Charset charset) {
      this.charset = charset;
    }

    public Collection<String> lines(Path path) throws IOException {
      final Reader fileReader = Files.newBufferedReader(path, charset);
      try (final BufferedReader lineReader = new BufferedReader(fileReader)) {
        return lineReader.lines().collect(Collectors.toList());
      }
    }
  }
}
