package com.github.sommeri.less4j.commandline;

import org.junit.Test;

public class SingleModeTest extends CommandLineTest {
  

  @Test
  public void oneInputFile() {
    String lessFile = inputDir+"one.less";
    CommandLine.main(new String[] {lessFile});
    assertSysout(correctCss("one"));
    assertNoErrors();
  }

  @Test
  public void twoInputFiles() {
    String lessFile = inputDir+"one.less";
    String cssFile = inputDir+"oneNew.css";
    String mapFile = inputDir+"oneNew.css.map";
    fileUtils.removeFile(cssFile);
    fileUtils.removeFile(mapFile);
    CommandLine.main(new String[] {lessFile, cssFile});
    fileUtils.assertFileContent(cssFile, correctCss("one"));
    fileUtils.assertFileNotExists(mapFile);
    assertNoErrors();
  }

  @Test
  public void generateSourceMap() {
    String lessFile = inputDir+"one.less";
    String mapdataFile = inputDir+"one.mapdata";
    String cssFile = inputDir+"oneNew.css";
    String mapFile = inputDir+"oneNew.css.map";
    fileUtils.removeFile(cssFile);
    fileUtils.removeFile(mapFile);
    CommandLine.main(new String[] {lessFile, cssFile, "-sm"});
    fileUtils.assertFileContent(cssFile, correctCssWithSourceMap("one", "oneNew.css.map"));
    assertNoErrors();
    validateSourceMap(mapdataFile, cssFile, mapFile);
  }

  @Test
  public void inputFileDoesNotExists1() {
    String lessFile = inputDir+"doesNotExists.less";
    CommandLine.main(new String[] {lessFile});
    assertError(FILE_DOES_NOT_EXISTS);
  }

  @Test
  public void inputFileDoesNotExists2() {
    String lessFile = inputDir+"doesNotExists.less";
    String cssFile = inputDir+"doesNotExists.css";
    CommandLine.main(new String[] {lessFile, cssFile});
    assertError(FILE_DOES_NOT_EXISTS);
  }

  @Test
  public void fileWithErrors() {
    String lessFile = inputDir+"errors.less";
    String cssFile = inputDir+"errors.css";
    fileUtils.removeFile(cssFile);
    CommandLine.main(new String[] {lessFile});
    assertSysout(incorrectCss());
    assertErrorsAsInFile(inputDir+"errors.err");
  }

  @Test
  public void fileWithWarnings() {
    String lessFile = inputDir+"warnings.less";
    String cssFile = inputDir+"warnings.css";
    fileUtils.removeFile(cssFile);
    CommandLine.main(new String[] {lessFile});
    assertSysout(warningsCss());
    assertErrorsAsInFile(inputDir+"warnings.err");
  }

  @Test
  public void fileWithErrorsAndWarnings() {
    String lessFile = inputDir+"errorsandwarnings.less";
    String cssFile = inputDir+"errorsandwarnings.css";
    fileUtils.removeFile(cssFile);
    CommandLine.main(new String[] {lessFile});
    assertSysout(incorrectCss());
    assertErrorsAsInFile(inputDir+"errorsandwarnings.err");
  }

  @Test
  public void partialOutputWithErrorsAndWarnings() {
    String lessFile = inputDir+"errorsandwarnings.less";
    String cssFile = inputDir+"errorsandwarnings.css";
    String expectedCssFile = inputDir+"errorsandwarnings.expcss";
    fileUtils.removeFile(cssFile);
    CommandLine.main(new String[] {"-pi", lessFile});
    assertSysoutAsInFile(expectedCssFile);
    assertErrorsAsInFile(inputDir+"errorsandwarnings.err");
  }

  @Test
  public void compressCss() {
    String lessFile = inputDir+"one.less";
    String cssFile = inputDir+"oneNew.css";
    fileUtils.removeFile(cssFile);
    CommandLine.main(new String[] {lessFile, cssFile, "-x"});
    fileUtils.assertFileContent(cssFile, correctCompressedCss("one"));
    assertNoErrors();
  }

}
