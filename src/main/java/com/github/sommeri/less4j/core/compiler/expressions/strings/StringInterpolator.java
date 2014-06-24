package com.github.sommeri.less4j.core.compiler.expressions.strings;

import java.util.regex.Pattern;

import com.github.sommeri.less4j.EmbeddedScriptGenerator;
import com.github.sommeri.less4j.EmbeddedLessGenerator;
import com.github.sommeri.less4j.core.ast.Expression;
import com.github.sommeri.less4j.core.ast.Variable;
import com.github.sommeri.less4j.core.compiler.expressions.IScopeAwareExpressionsEvaluator;
import com.github.sommeri.less4j.core.parser.HiddenTokenAwareTree;
import com.github.sommeri.less4j.core.problems.ProblemsHandler;

public class StringInterpolator extends AbstractStringReplacer<IScopeAwareExpressionsEvaluator> {

  private static final Pattern STR_INTERPOLATION = Pattern.compile("@\\{([^\\{\\}@])*\\}");
  private final EmbeddedScriptGenerator embeddedScriptEvaluator;
  private final ProblemsHandler problemsHandler;

  public StringInterpolator(ProblemsHandler problemsHandler) {
    this(new EmbeddedLessGenerator(), problemsHandler);
  }

  public StringInterpolator(EmbeddedScriptGenerator embeddedScriptEvaluator, ProblemsHandler problemsHandler) {
    this.embeddedScriptEvaluator = embeddedScriptEvaluator;
    this.problemsHandler = problemsHandler;
  }

  @Override
  protected Pattern getPattern() {
    return STR_INTERPOLATION;
  }

  @Override
  protected String extractMatchName(String group) {
    return "@" + group.substring(2, group.length() - 1);
  }

  @Override
  protected String replacementValue(IScopeAwareExpressionsEvaluator expressionEvaluator, HiddenTokenAwareTree technicalUnderlying, MatchRange matchRange) {
    Expression value = expressionEvaluator.evaluateIfPresent(new Variable(technicalUnderlying, matchRange.getName()));
    if (value == null) {
      return matchRange.getFullMatch();
    }

    return embeddedScriptEvaluator.toScript(value, problemsHandler);
  }

}
