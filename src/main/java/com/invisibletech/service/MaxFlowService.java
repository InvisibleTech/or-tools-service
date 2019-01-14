package com.invisibletech.service;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.ortools.graph.MaxFlow;

import com.invisibletech.service.MaxFlowResponse.Status;

public class MaxFlowService {
  static final BiFunction<MaxFlow, Map<Integer, String>, MaxFlowResponse> maxFlowMapper = (m, n) -> {
    List<MaxFlowResponse.SolutionArc> arcs = IntStream.range(0, m.getNumArcs())
    .mapToObj(a -> ImmutableSolutionArc.builder()
        .tail(n.get(m.getTail(a)))
        .head(n.get(m.getHead(a)))
        .maxFlow(m.getFlow(a))
        .capacity(m.getCapacity(a))
        .build())
      .collect(Collectors.toList());
    return ImmutableMaxFlowResponse.builder()
      .status(Status.OPTIMAL)
      .optimalFlow(m.getOptimalFlow())
      .arcs(arcs)
      .build();
  };

  private static final Map<MaxFlow.Status, BiFunction<MaxFlow, Map<Integer, String>, MaxFlowResponse>> RESPONSE_MAP =
    new HashMap<MaxFlow.Status, BiFunction<MaxFlow, Map<Integer, String>, MaxFlowResponse>> () {
      {
        put(MaxFlow.Status.OPTIMAL, (m, n) -> maxFlowMapper.apply(m, n));
        put(MaxFlow.Status.POSSIBLE_OVERFLOW, (m, n) ->
          ImmutableMaxFlowResponse.builder()
          .status(Status.POSSIBLE_OVERFLOW)
            .optimalFlow(0)
            .arcs(Collections.emptyList())
            .build());
        put(MaxFlow.Status.BAD_INPUT, (m, n) ->
            ImmutableMaxFlowResponse.builder()
            .status(Status.BAD_INPUT)
            .optimalFlow(0)
            .arcs(Collections.emptyList())
            .build());
        put(MaxFlow.Status.BAD_RESULT, (m, n) ->
            ImmutableMaxFlowResponse.builder()
            .status(Status.BAD_RESULT)
            .optimalFlow(0)
            .arcs(Collections.emptyList())
            .build());
      }
    };

  private final MaxFlow maxFlow;

  // TODO:
  // Gave some serious thought to using Spring (if possible without antics) and
  // Guice.  Guice had more appeal, since searching kept leading to SpringBoot
  // and Spring MVC for REST servers.  However, a good example for Guice I found
  // kind of inflated the complexity of assembly a good bit for this service of
  // only one endpoint, for now.  However, maybe later if there is interest:
  //
  // https://github.com/devng/demo/tree/master/sparkjava-guice
  //
  public MaxFlowService(MaxFlow maxFlow) {
    // TODO Add a factory instead.
    this.maxFlow = maxFlow;
  }

  public MaxFlowResponse solve(MaxFlowRequest request) {
    validate(request);
    Map<String, Integer> nodeNameToIndex = new HashMap<>();

    for (MaxFlowRequest.Arc arc: request.arcs()) {
      nodeNameToIndex.computeIfAbsent(arc.tail(), (_x) -> nodeNameToIndex.size());
      nodeNameToIndex.computeIfAbsent(arc.head(), (_x) -> nodeNameToIndex.size());
      this.maxFlow.addArcWithCapacity(
        nodeNameToIndex.get(arc.tail()),
        nodeNameToIndex.get(arc.head()),
        arc.capacity());
    }

    MaxFlow.Status status = this.maxFlow.solve(
      nodeNameToIndex.get(request.source()),
      nodeNameToIndex.get(request.sink()));

    return RESPONSE_MAP.get(status).apply(
      this.maxFlow,
      nodeNameToIndex.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getValue(), e -> e.getKey())));
  }

  void validate(MaxFlowRequest request) {
    notEmpty(request.arcs(), "No arcs were provided for the problem.");
  }
}
