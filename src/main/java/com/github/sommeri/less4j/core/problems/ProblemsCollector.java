package com.github.sommeri.less4j.core.problems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.sommeri.less4j.LessCompiler.Problem;

public class ProblemsCollector {

  private List<Problem> warnings = new ArrayList<Problem>();
  private List<Problem> errors = new ArrayList<Problem>();

  public void addErrors(Collection<Problem> errors) {
    this.errors.addAll(errors);
  }

  public void addError(CompilationError error) {
    errors.add(error);
  }

  public void addWarning(CompilationWarning warning) {
    warnings.add(warning);
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public List<Problem> getErrors() {
    return errors;
  }

  public boolean hasWarnings() {
    return !warnings.isEmpty();
  }

  public List<Problem> getWarnings() {
    return warnings;
  }

  @Override
  public String toString() {
    return "ProblemsCollector [errors:" + errors.size() + ", " + "warnings: " + warnings.size() + "]";
  }

}
