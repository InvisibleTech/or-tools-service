package com.invisibletech.service;

import java.util.ArrayList;
import java.util.List;

public class MaxFlowRequest {
  public static class Arc {
    private final String sourceNode;
    private final String sinkNode;
    private final long capacity;

    Arc(String sourceNode, String sinkNode, long capacity) {
      this.sourceNode = sourceNode;
      this.sinkNode = sinkNode;
      this.capacity = capacity;
    }

    public String getSourceNode() {
      return this.sourceNode;
    }

    public String getSinkNode() {
      return this.sinkNode;
    }
  }

  private List<Arc> arcs;

  public List<Arc> getArcs() {
    return new ArrayList<>(this.arcs);
  }
}
