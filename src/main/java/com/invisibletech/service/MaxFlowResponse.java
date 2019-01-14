package com.invisibletech.service;

import java.util.List;
import org.immutables.value.Value;
import org.immutables.gson.Gson;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MaxFlowResponse {
  @Value.Immutable
  public static abstract class SolutionArc {
    public abstract String tail();
    public abstract String head();
    public abstract long maxFlow();
    public abstract long capacity();
  }

  public enum Status {
    BAD_INPUT,
    BAD_RESULT,
    OPTIMAL,
    POSSIBLE_OVERFLOW;
  }

  public abstract Status status();
  public abstract long optimalFlow();
  public abstract List<SolutionArc> arcs();
}
