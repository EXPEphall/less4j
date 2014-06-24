package com.github.sommeri.less4j.core.compiler.stages;

import java.util.ArrayList;
import java.util.List;

import com.github.sommeri.less4j.LessCompiler.Configuration;
import com.github.sommeri.less4j.core.ast.ASTCssNode;
import com.github.sommeri.less4j.core.ast.Expression;
import com.github.sommeri.less4j.core.ast.MixinReference;
import com.github.sommeri.less4j.core.ast.ReusableStructure;
import com.github.sommeri.less4j.core.compiler.expressions.ExpressionComparator;
import com.github.sommeri.less4j.core.compiler.expressions.IScopeAwareExpressionsEvaluator;
import com.github.sommeri.less4j.core.compiler.expressions.PatternsComparator;
import com.github.sommeri.less4j.core.compiler.scopes.FullMixinDefinition;
import com.github.sommeri.less4j.core.compiler.scopes.IScope;
import com.github.sommeri.less4j.core.problems.ProblemsHandler;

public class MixinsReferenceMatcher {

  private IScopeAwareExpressionsEvaluator evaluator;
  private ExpressionComparator comparator = new PatternsComparator();

  public MixinsReferenceMatcher(IScope scope, ProblemsHandler problemsHandler, Configuration configuration) {
    evaluator = new IScopeAwareExpressionsEvaluator(scope, problemsHandler, configuration);
  }

  public List<FullMixinDefinition> filterByParametersNumber(MixinReference reference, List<FullMixinDefinition> mixins) {
    int requiredNumber = reference.getNumberOfDeclaredParameters();
    List<FullMixinDefinition> result = new ArrayList<FullMixinDefinition>();
    for (FullMixinDefinition mixin : mixins) {
      if (hasRightNumberOfParameters(mixin.getMixin(), requiredNumber))
        result.add(mixin);
    }
    return result;
  }

  public List<FullMixinDefinition> filterByPatterns(MixinReference reference, List<FullMixinDefinition> mixins) {
    List<FullMixinDefinition> result = new ArrayList<FullMixinDefinition>();
    for (FullMixinDefinition mixin : mixins) {
      if (patternsMatch(reference, mixin.getMixin()))
        result.add(mixin);
    }
    return result;
  }

  private boolean hasRightNumberOfParameters(ReusableStructure mixin, int requiredNumber) {
    int allDefined = mixin.getParameters().size();
    int mandatory = mixin.getMandatoryParameters().size();
    boolean hasRightNumberOfParameters = requiredNumber >= mandatory && (requiredNumber <= allDefined || mixin.hasCollectorParameter());
    return hasRightNumberOfParameters;
  }

  //FIXME: how does pattern matching and named arguments mix? This is most likely faulty
  private boolean patternsMatch(MixinReference reference, ReusableStructure mixin) {
    int i = 0;
    for (ASTCssNode parameter : mixin.getParameters()) {
      if (parameter instanceof Expression) {
        if (!reference.hasPositionalParameter(i))
          return false;

        Expression pattern = (Expression) parameter;
        if (!comparator.equal(pattern, evaluator.evaluate(reference.getPositionalParameter(i))))
          return false;
      }
      i++;
    }
    
    return true;
  }

}
