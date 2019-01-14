package com.invisibletech.service;

import java.util.List;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MaxFlowRequest {
  @Value.Immutable
  public static abstract class Arc {
    public abstract String tail();
    public abstract String head();
    public abstract long capacity();
  }

  public abstract List<Arc> arcs();
  public abstract String source();
  public abstract String sink();
}
