package com.github.sommeri.less4j.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.sommeri.less4j.AbstractFileBasedTest;
import com.github.sommeri.less4j.platform.Constants;
import com.github.sommeri.less4j.utils.URIUtils;

/**
 * The test reproduces test files found in original less.js implementation. Some of those
 * test have been modified, original versions are located in 
 * src/test/resources/less.js-v1.3.3/original-versions-of-modified-cases directory.
 * 
 */
@RunWith(Parameterized.class)
public class LessJsV1_3_3Test extends AbstractFileBasedTest {

  private static final String inputLessDir = "src/test/resources/less.js-v1.3.3/less/";
  private static final String expectedCssDir = "src/test/resources/less.js-v1.3.3/css/";
  private static final Set<String> excludeInput = new HashSet<String>(Arrays.asList(new String[] {"comments.less", "whitespace.less"}));
  private static final Set<String> disabled = new HashSet<String>(Arrays.asList(new String[] {"urls.less"}));

  public LessJsV1_3_3Test(File inputFile, File outputFile, File errorList, File mapdataFile, File configFile, String testName) {
    super(inputFile, outputFile, errorList, mapdataFile, configFile, testName);
  }

  @Parameters(name="Less: {4}")
  public static Collection<Object[]> allTestsParameters() {
    Collection<File> allFiles = FileUtils.listFiles(new File(inputLessDir), null, false);
    Collection<Object[]> result = new ArrayList<Object[]>();
    for (File less : allFiles) {
      if (!excludeInput.contains(less.getName()) && !disabled.contains(less.getName())) {
        File css = findCorrespondingCss(less);
        File err = URIUtils.changeSuffix(css, ".err");
        File mapdata = URIUtils.changeSuffix(css, ".mapdata");
        File config = URIUtils.changeSuffix(css, ".config");
        result.add(new Object[] { less, css, err, mapdata, config, less.getName() });
      }
    }
    return result;
  }

  protected static File findCorrespondingCss(File lessFile) {
    String lessFileName = lessFile.getName();
    String cssFileName = toCssFile(lessFileName);
    File cssFile = new File(expectedCssDir + cssFileName);
    return cssFile;
  }

  private static String toCssFile(String name) {
    return URIUtils.changeSuffix(name, Constants.CSS_SUFFIX);
  }

  @Override
  protected String canonize(String text) {
    text = super.canonize(text);
    //selectors.less
    text = text.replace("color: #ff0000;", "color: red;");
    text = text.replace(".test:nth-child(odd):not( :nth-child(3))", ".test:nth-child(odd):not(:nth-child(3))");
    //colors.less
    text = text.replace("background-color: rgba(0, 0, 0, 0);", "background-color: rgba(0, 0, 0, 0.0);");
    //css-escapes.less
    text = text.replace("color: #ff00ff;", "color: fuchsia;");
    //variables.less
    text = text.replace("color: #888;", "color: #888888;");
    text = text.replace("color: #888 !important;", "color: #888888 !important;");
    //parens.less
    text = text.replace("border: 2px solid #000000;", "border: 2px solid black;");
    //mixins-guards.less - unfinished
    text = text.replace("content: is #ff0000;", "content: is red;");
    text = text.replace("content: is not #0000ff its #ff0000;", "content: is not blue its red;");
    text = text.replace("content: is not #0000ff its #800080;", "content: is not blue its purple;");
    //css.less
    text = text.replace("width: 100%!important;", "width: 100% !important;");
    text = text.replace("height: 20px ! important;", "height: 20px !important;");
    text = text.replace("background: -webkit-gradient(linear, left top, left bottom, from(#ff0000), to(#0000ff));", "background: -webkit-gradient(linear, left top, left bottom, from(red), to(blue));");
    text = text.replace("  font-size: 2.2em;", "  font-size: +2.2em;");
    //mixins-args.less
    text = text.replace("border: 1px solid black;", "border: 1px solid #000000;");
    text = text.replace("border: 2px dotted black;", "border: 2px dotted #000000;");
    text = text.replace("color: red;", "color: #f00;");
    //functions.less
    text = text.replace("background: linear-gradient(#000, #fff);", "background: linear-gradient(#000000, #ffffff);");
    //import.less
    text = text.replace("\n\n", "\n");
    //scope.less
    text = text.replace("color: #998899", "color: #989");
    text = text.replace("color: #0000ff", "color: blue");
    text = text.replace("border-color: black", "border-color: #000000");
    text = text.replace("background-color: white;", "background-color: #ffffff;");
    text = text.replace("scoped-val: green;", "scoped-val: #008000;");
    //css-3.less
    text = text.replace("text-shadow: -1px -1px 1px red, 6px 5px 5px yellow;", "text-shadow: -1px -1px 1px #ff0000, 6px 5px 5px #ffff00;");
    text = text.replace("xxxx", "xxxx");
    
    return text;
  }

  
}
