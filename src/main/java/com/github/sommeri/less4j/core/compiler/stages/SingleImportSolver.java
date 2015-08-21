package com.github.sommeri.less4j.core.compiler.stages;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sommeri.less4j.LessSource;
import com.github.sommeri.less4j.LessCompiler.Configuration;
import com.github.sommeri.less4j.LessSource.CannotReadFile;
import com.github.sommeri.less4j.LessSource.FileNotFound;
import com.github.sommeri.less4j.LessSource.StringSourceException;
import com.github.sommeri.less4j.core.ast.ASTCssNode;
import com.github.sommeri.less4j.core.ast.ASTCssNodeType;
import com.github.sommeri.less4j.core.ast.FaultyNode;
import com.github.sommeri.less4j.core.ast.GeneralBody;
import com.github.sommeri.less4j.core.ast.Import;
import com.github.sommeri.less4j.core.ast.Import.ImportContent;
import com.github.sommeri.less4j.core.ast.InlineContent;
import com.github.sommeri.less4j.core.ast.Import.ImportMultiplicity;
import com.github.sommeri.less4j.core.ast.Media;
import com.github.sommeri.less4j.core.ast.StyleSheet;
import com.github.sommeri.less4j.core.compiler.expressions.TypesConversionUtils;
import com.github.sommeri.less4j.core.parser.ANTLRParser;
import com.github.sommeri.less4j.core.parser.ASTBuilder;
import com.github.sommeri.less4j.core.parser.HiddenTokenAwareTree;
import com.github.sommeri.less4j.core.problems.ProblemsHandler;

public class SingleImportSolver {

  private final ProblemsHandler problemsHandler;
  private final Configuration configuration;
  private TypesConversionUtils conversionUtils = new TypesConversionUtils();
  private ASTManipulator astManipulator = new ASTManipulator();

  private Set<LessSource> importedSources = new HashSet<LessSource>();

  public SingleImportSolver(ProblemsHandler problemsHandler, Configuration configuration) {
    this.problemsHandler = problemsHandler;
    this.configuration = configuration;
  }

  public void solveImports(StyleSheet node, LessSource source) {
    doSolveImports(node, source);
  }

  private void doSolveImports(StyleSheet node, LessSource source) {
    List<ASTCssNode> childs = new ArrayList<ASTCssNode>(node.getChilds());
    for (ASTCssNode kid : childs) {
      if (kid.getType() == ASTCssNodeType.IMPORT) {
        importEncountered((Import) kid, source);
      }
    }
  }

  public ASTCssNode importEncountered(Import node, LessSource source) {
    String filename = conversionUtils.extractFilename(node.getUrlExpression(), problemsHandler, configuration);
    if (filename == null) {
      problemsHandler.errorWrongImport(node.getUrlExpression());
      return null;
    }
    String urlParams = "";
    int paramsIndx = filename.lastIndexOf("?");
    if (paramsIndx != -1) {
      urlParams = filename.substring(paramsIndx);
      filename = filename.substring(0, paramsIndx);
    }

    // css file imports should be left as they are
    // FIXME ! they should be relativized
    if (!node.isInline() && treatAsCss(node, filename))
      return null; 

    filename = addLessSuffixIfNeeded(filename, urlParams);
    LessSource importedSource;
    try {
      importedSource = source.relativeSource(filename);
    } catch (FileNotFound ex) {
      return importFileNotFound(node, filename);
    } catch (CannotReadFile e) {
      problemsHandler.errorFileCanNotBeRead(node, filename);
      return null;
    } catch (StringSourceException ex) {
      // imports are relative to current file and we do not know its location
      problemsHandler.warnLessImportNoBaseDirectory(node.getUrlExpression());
      return null;
    }

    //System.out.println(importedSource.getURI());
    // import once should not import a file that was already imported
    if (isImportOnce(node) && alreadyVisited(importedSource)) {
      astManipulator.removeFromBody(node);
      return null;
    }

    String importedContent;
    try {
      importedContent = importedSource.getContent();
      importedSources.add(importedSource);
    } catch (FileNotFound e) {
      return importFileNotFound(node, filename);
    } catch (CannotReadFile e) {
      problemsHandler.errorFileCanNotBeRead(node, filename);
      return null;
    }

    if (node.isInline()) {
      return replaceByInlineValue(node, importedContent);
    }
    
    StyleSheet importedAst = buildImportedAst(node, importedSource, importedContent);
    if (node.isReferenceOnly() || node.isSilent()) {
      astManipulator.setTreeSilentness(importedAst, true);
    }
    astManipulator.replaceInBody(node, importedAst.getChilds());
    return importedAst;
  }

  private ASTCssNode replaceByInlineValue(Import node, String importedContent) {
    HiddenTokenAwareTree underlyingStructure = node.getUnderlyingStructure();
    StyleSheet result = new StyleSheet(underlyingStructure);
    InlineContent content = new InlineContent(underlyingStructure, importedContent);
    result.addMember(content);
    result.configureParentToAllChilds();
    
    astManipulator.replaceInBody(node, content);
    return result;
  }

  private ASTCssNode importFileNotFound(Import node, String filename) {
    if (!node.isOptional()) {
      problemsHandler.errorFileNotFound(node, filename);
      return null;
    }
    return replaceByInlineValue(node, "");
  }

  private StyleSheet buildImportedAst(Import node, LessSource source, String content) {
    // parse imported file
    StyleSheet importedAst = parseContent(node, content, source);

    // add media queries if needed
    if (node.hasMediums()) {
      HiddenTokenAwareTree underlyingStructure = node.getUnderlyingStructure();
      StyleSheet result = new StyleSheet(underlyingStructure);
      Media media = new Media(underlyingStructure);
      result.addMember(media);
      media.setParent(result);
      media.setMediums(node.getMediums());
      GeneralBody mediaBody = new GeneralBody(underlyingStructure, importedAst.getMembers());
      media.setBody(mediaBody);
      media.configureParentToAllChilds();
      mediaBody.configureParentToAllChilds();
      return result;
    } 
    
    return importedAst;
  }

  private boolean isImportOnce(Import node) {
    return node.getMultiplicity() == ImportMultiplicity.IMPORT 
            || node.getMultiplicity() == ImportMultiplicity.IMPORT_ONCE;
  }

  private boolean alreadyVisited(LessSource importedSource) {
    return importedSources.contains(importedSource);
  }
  
  public Set<LessSource> getImportedSources() {
    return importedSources;
  }

  private StyleSheet parseContent(Import importNode, String importedContent, LessSource source) {
    ANTLRParser parser = new ANTLRParser();
    ANTLRParser.ParseResult parsedSheet = parser.parseStyleSheet(importedContent, source);
    if (parsedSheet.hasErrors()) {
      StyleSheet result = new StyleSheet(importNode.getUnderlyingStructure());
      result.addMember(new FaultyNode(importNode));
      problemsHandler.addErrors(parsedSheet.getErrors());
      return result;
    }
    ASTBuilder astBuilder = new ASTBuilder(problemsHandler);
    StyleSheet lessStyleSheet = astBuilder.parse(parsedSheet.getTree());
    return lessStyleSheet;

  }

  private String addLessSuffixIfNeeded(String filename, String urlParams) {
    if ((new File(filename)).getName().contains("."))
      return filename;

    return filename + ".less" + urlParams;
  }

  private boolean treatAsCss(Import node, String filename) {
    ImportContent contentKind = node.getContentKind();
    return contentKind==ImportContent.CSS || (contentKind==ImportContent.SUFFIX_BASED && isCssFile(filename));
  }

  private boolean isCssFile(String filename) {
    String lowerCase = filename.toLowerCase();
    return lowerCase.endsWith(".css") || lowerCase.endsWith("/css");
  }

}
