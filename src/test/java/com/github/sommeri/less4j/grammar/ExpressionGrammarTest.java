package com.github.sommeri.less4j.grammar;

import static com.github.sommeri.less4j.grammar.GrammarAsserts.assertChilds;
import static com.github.sommeri.less4j.grammar.GrammarAsserts.assertValidExpression;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.github.sommeri.less4j.core.parser.LessLexer;
import com.github.sommeri.less4j.core.parser.ANTLRParser;
import com.github.sommeri.less4j.core.parser.ANTLRParser.ParseResult;

/**
 * Testing numbers and arithmetic expression parser.
 * 
 * Note: strange things happen with EOF during unit tests, so I added a dummy
 * space to the test expressions.
 * 
 * Found something about EOF:
 * http://www.antlr.org/pipermail/antlr-interest/2009-January/032219.html
 * 
 */
//the test is ignored. I will do pure css first and add other features later
@Ignore
public class ExpressionGrammarTest {

  @Test
  public void sequenceNumbersLong() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12-34 56+7 8*90 ", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.NUMBER, LessLexer.MINUS, LessLexer.NUMBER, LessLexer.EMPTY_SEPARATOR, LessLexer.NUMBER, LessLexer.PLUS, LessLexer.NUMBER, LessLexer.EMPTY_SEPARATOR, LessLexer.NUMBER, LessLexer.STAR, LessLexer.NUMBER);
  }

  @Test
  public void sequenceNumbersLongSpaces() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12 - 34 56 + 7 8 * 90 ", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.NUMBER, LessLexer.MINUS, LessLexer.NUMBER, LessLexer.EMPTY_SEPARATOR, LessLexer.NUMBER, LessLexer.PLUS, LessLexer.NUMBER, LessLexer.EMPTY_SEPARATOR, LessLexer.NUMBER, LessLexer.STAR, LessLexer.NUMBER);
  }

  @Test
  public void sequencePixels() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12px 13px", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.LENGTH, LessLexer.EMPTY_SEPARATOR, LessLexer.LENGTH);
  }

  @Test
  public void plusPixels() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12px + 13px", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.LENGTH, LessLexer.PLUS, LessLexer.LENGTH);
  }

  @Test
  public void plusPixelsShort() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12px+13px", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.LENGTH, LessLexer.PLUS, LessLexer.LENGTH);
  }

  @Test
  public void sequenceNumbers() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("1.2 13 ", null);
    assertValidExpression(result);
    assertChilds(result.getTree(), LessLexer.NUMBER, LessLexer.EMPTY_SEPARATOR, LessLexer.NUMBER);
  }

  @Test
  public void floating() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("1.2 ", null);

    assertValidExpression(result);
    assertEquals(1, result.getTree().getChildren().size());
    assertEquals(LessLexer.NUMBER, result.getTree().getChild(0).getGeneralType());
  }

  @Test
  public void integer() {
    ANTLRParser compiler = new ANTLRParser();
    ParseResult result = compiler.parseExpression("12 ", null);

    assertValidExpression(result);
    assertEquals(1, result.getTree().getChildren().size());
    assertEquals(LessLexer.NUMBER, result.getTree().getChild(0).getGeneralType());
  }

}
