package com.github.sommeri.less4j.core.compiler.stages;

import java.util.ArrayList;
import java.util.List;

import com.github.sommeri.less4j.LessCompiler.Configuration;
import com.github.sommeri.less4j.core.ast.BodyOwner;
import com.github.sommeri.less4j.core.ast.Expression;
import com.github.sommeri.less4j.core.ast.MixinReference;
import com.github.sommeri.less4j.core.compiler.expressions.ExpressionEvaluator;
import com.github.sommeri.less4j.core.compiler.scopes.FullExpressionDefinition;
import com.github.sommeri.less4j.core.compiler.scopes.IScope;
import com.github.sommeri.less4j.core.problems.ProblemsHandler;

public class ScopedValuesEvaluator {

  private final ExpressionEvaluator expressionEvaluator;
  private IScope scope;

  public ScopedValuesEvaluator(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public ScopedValuesEvaluator(IScope scope, ProblemsHandler problemsHandler, Configuration configuration) {
    this(new ExpressionEvaluator(scope, problemsHandler, configuration));
    this.scope = scope;
  }

  public FullExpressionDefinition toFullNodeDefinition(Expression value) {
    Expression evaluated = evaluate(value);
    IScope scope = findBodyScope(evaluated);
    return new FullExpressionDefinition(evaluated, scope);
  }

  private IScope findBodyScope(Expression value) {
    IScope scope = null;
    if (AstLogic.isBodyOwner(value)) {
      @SuppressWarnings("rawtypes")
      BodyOwner asBodyOwner = (BodyOwner) value;
      scope = this.scope.childByOwners(value, asBodyOwner.getBody());
    }
    return scope;
  }

  private Expression evaluate(Expression value) {
    return expressionEvaluator.evaluate(value);
  }

  public List<Expression> evaluateAll(List<Expression> remaining) {
    return expressionEvaluator.evaluateAll(remaining);
  }

  public Expression joinAll(List<Expression> allArgumentsFrom, MixinReference reference) {
    return expressionEvaluator.joinAll(allArgumentsFrom, reference);
  }

  public List<FullExpressionDefinition> toFullNodeDefinitions(List<Expression> allArgumentsFrom) {
    List<FullExpressionDefinition> result = new ArrayList<FullExpressionDefinition>();
    for (Expression expression : allArgumentsFrom) {
      result.add(toFullNodeDefinition(expression));
    }
    return result;
  }

  //FIXME:!!!!!!!!!!!!!!! scopeee what should this do?
  public FullExpressionDefinition joinFull(List<FullExpressionDefinition> allValues, MixinReference reference) {
    List<Expression> result = new ArrayList<Expression>();
    for (FullExpressionDefinition expression : allValues) {
      result.add((Expression)expression.getNode());
    }
    return new FullExpressionDefinition(joinAll(result, reference), null);
  }

}
