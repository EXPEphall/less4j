package com.github.sommeri.less4j.core.output;

import java.util.HashSet;
import java.util.Set;

import com.github.sommeri.less4j.platform.Constants;

public class ExtendedStringBuilder {
  private static final String INDENTATION = "  ";
  private static final char SPACE = ' ';
  private static final Set<Character> SEPARATORS = new HashSet<Character>();
  private StringBuilder builder = new StringBuilder();

  private int indentationLevel;
  private int line = 0;
  private boolean onNewLine = true;

  static {
    SEPARATORS.add(' ');
    SEPARATORS.add(';');
    SEPARATORS.add('(');
  }

  public ExtendedStringBuilder() {
    this("");
  }

  public ExtendedStringBuilder(String string) {
    builder = new StringBuilder(string);
  }

  public ExtendedStringBuilder(ExtendedStringBuilder otherBuilder) {
    configureFrom(otherBuilder);
  }

  public void configureFrom(ExtendedStringBuilder otherBuilder) {
    indentationLevel = otherBuilder.indentationLevel;
    onNewLine = otherBuilder.onNewLine;
  }

  public ExtendedStringBuilder appendAsIs(String string) {
    builder.append(string);
    addNewLines(string);
    return this;
  }

  private void addNewLines(String string) {
    int addedLines = string.split(Constants.NEW_LINE).length - 1;
    if (string.endsWith(Constants.NEW_LINE))
      addedLines++;
    
    line = line + addedLines;
  }

  public ExtendedStringBuilder append(boolean arg0) {
    handleIndentation();
    builder.append(arg0);
    return this;
  }

  public ExtendedStringBuilder append(char c) {
    handleIndentation();
    builder.append(c);
    return this;
  }

  public ExtendedStringBuilder append(char[] str, int offset, int len) {
    handleIndentation();
    builder.append(str, offset, len);
    return this;
  }

  public ExtendedStringBuilder append(char[] str) {
    handleIndentation();
    builder.append(str);
    return this;
  }

  public ExtendedStringBuilder append(CharSequence s, int start, int end) {
    handleIndentation();
    builder.append(s, start, end);
    return this;
  }

  public ExtendedStringBuilder append(CharSequence s) {
    handleIndentation();
    builder.append(s);
    return this;
  }

  public ExtendedStringBuilder append(double d) {
    handleIndentation();
    builder.append(d);
    return this;
  }

  public ExtendedStringBuilder append(float f) {
    handleIndentation();
    builder.append(f);
    return this;
  }

  public ExtendedStringBuilder append(int i) {
    handleIndentation();
    builder.append(i);
    return this;
  }

  public ExtendedStringBuilder append(long lng) {
    handleIndentation();
    builder.append(lng);
    return this;
  }

  public ExtendedStringBuilder append(Object obj) {
    handleIndentation();
    builder.append(obj);
    return this;
  }

  public ExtendedStringBuilder append(String str) {
    handleIndentation();
    builder.append(str);
    addNewLines(str);
    return this;
  }

  public ExtendedStringBuilder append(StringBuffer sb) {
    handleIndentation();
    builder.append(sb);
    addNewLines(sb.toString());
    return this;
  }

  public ExtendedStringBuilder appendIgnoreNull(CharSequence s) {
    if (s != null) {
      handleIndentation();
      builder.append(s);
    }

    return this;
  }

  public String toString() {
    return builder.toString();
  }

  public StringBuilder toStringBuilder() {
    return builder;
  }

  public ExtendedStringBuilder ensureNewLine() {
    if (!onNewLine)
      newLine();

    return this;
  }

  public ExtendedStringBuilder newLine() {
    builder.append(Constants.NEW_LINE);
    onNewLine = true;
    line++;

    return this;
  }

  public void increaseIndentationLevel() {
    indentationLevel++;
  }

  public void decreaseIndentationLevel() {
    indentationLevel--;
    if (indentationLevel < 0)
      indentationLevel = 0;
  }

  public void handleIndentation() {
    if (onNewLine()) {
      onNewLine = false;
      for (int i = 0; i < indentationLevel; i++) {
        builder.append(INDENTATION);
      }
    }
  }

  private boolean onNewLine() {
    return onNewLine;
  }

  public ExtendedStringBuilder ensureSeparator() {
    if (onNewLine() || endsWithSeparator())
      return this;

    appendSpace();
    return this;
  }

  public ExtendedStringBuilder appendSpace() {
    builder.append(SPACE);
    return this;
  }

  public boolean endsWithSeparator() {
    int length = builder.length() - 1;
    if (length < 0)
      return false;

    return SEPARATORS.contains(builder.charAt(length));
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    int lastNewLine = builder.lastIndexOf(Constants.NEW_LINE);
    return builder.length() - (lastNewLine+1);
  }
}
