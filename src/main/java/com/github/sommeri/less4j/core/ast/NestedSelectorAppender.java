package com.github.sommeri.less4j.core.ast;

import java.util.List;

import com.github.sommeri.less4j.core.ast.annotations.NotAstProperty;
import com.github.sommeri.less4j.core.parser.HiddenTokenAwareTree;
import com.github.sommeri.less4j.core.problems.BugHappened;

public class NestedSelectorAppender extends SelectorPart {

  private boolean directlyBefore;
  private boolean directlyAfter;

  public NestedSelectorAppender(HiddenTokenAwareTree underlyingStructure, boolean directlyBefore, boolean directlyAfter, SelectorCombinator leadingCombinator) {
    super(underlyingStructure, leadingCombinator);
    this.directlyBefore = directlyBefore;
    this.directlyAfter = directlyAfter;
  }

  @Override
  @NotAstProperty
  public List<ASTCssNode> getChilds() {
    return super.getChilds();
  }

  public boolean isDirectlyBefore() {
    return directlyBefore;
  }

  public void setDirectlyBefore(boolean directlyBefore) {
    this.directlyBefore = directlyBefore;
  }

  public boolean isDirectlyAfter() {
    return directlyAfter && !hasLeadingCombinator();
  }

  public void setDirectlyAfter(boolean directlyAfter) {
    this.directlyAfter = directlyAfter;
  }

  @Override
  public ASTCssNodeType getType() {
    return ASTCssNodeType.NESTED_SELECTOR_APPENDER;
  }

  @Override
  public NestedSelectorAppender clone() {
    return (NestedSelectorAppender)super.clone();
  }

  public boolean isAppender() {
    return true;
  }

  @Override
  public void setParent(ASTCssNode parent) {
    if (parent!=null && !(parent instanceof Selector))
      throw new BugHappened("Nested selector appender must belong to selector.", this); 
    super.setParent(parent);
  }

  public Selector getParentAsSelector() {
    return (Selector) super.getParent();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("\"");
    if (!isDirectlyAfter())
      builder.append(" ");
    builder.append("&");
    if (!isDirectlyBefore())
      builder.append(" ");
    builder.append("\"");
    
    return builder.toString();
  }
  
  
}
