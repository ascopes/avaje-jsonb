package io.avaje.jsonb.generator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

final class MethodReader {

  private final ExecutableElement element;
  private final String methodName;
  private final List<MethodParam> params = new ArrayList<>();

  MethodReader(ExecutableElement element, TypeElement beanType) {
    this.element = element;
    this.methodName = element.getSimpleName().toString();
  }

  @Override
  public String toString() {
    return methodName;
  }

  TypeMirror returnType() {
    return element.getReturnType();
  }

  MethodReader read() {
    List<? extends VariableElement> ps = element.getParameters();
    for (VariableElement p : ps) {
      params.add(new MethodParam(p));
    }
    return this;
  }

  String getName() {
    return methodName;
  }

  List<MethodParam> getParams() {
    return params;
  }


  public boolean isPublic() {
    return element.getModifiers().contains(Modifier.PUBLIC);
  }

  public boolean isProtected() {
    return element.getModifiers().contains(Modifier.PROTECTED);
  }

  static class MethodParam {

    private final String simpleName;
    private final String type;

    MethodParam(VariableElement param) {
      this.simpleName = param.getSimpleName().toString();
      this.type = param.asType().toString();
    }

    String name() {
      return simpleName;
    }

    public String type() {
      return type;
    }
  }
}
