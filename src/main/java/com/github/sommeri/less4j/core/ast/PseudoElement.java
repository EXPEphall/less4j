package com.github.sommeri.less4j.core.ast;

import java.util.Collections;
import java.util.List;

import com.github.sommeri.less4j.core.ast.annotations.NotAstProperty;
import com.github.sommeri.less4j.core.parser.HiddenTokenAwareTree;

public class PseudoElement extends Pseudo {

  private boolean level12Form;

  public PseudoElement(HiddenTokenAwareTree token, String name) {
    this(token, name, false);
  }

  public PseudoElement(HiddenTokenAwareTree token, String name, boolean level12Form) {
    super(token, name);
    this.level12Form = level12Form;
  }

  public String getFullName() {
    if (isLevel12Form())
      return ":"+getName();
    
    return "::"+getName();
  }

  public boolean isLevel12Form() {
    return level12Form;
  }

  public void setLevel12Form(boolean level12Form) {
    this.level12Form = level12Form;
  }

  @Override
  @NotAstProperty
  public List<? extends ASTCssNode> getChilds() {
    return Collections.emptyList();
  }

  @Override
  public ASTCssNodeType getType() {
    return ASTCssNodeType.PSEUDO_ELEMENT;
  }

  @Override
  public PseudoElement clone() {
    return (PseudoElement) super.clone();
  }
}
