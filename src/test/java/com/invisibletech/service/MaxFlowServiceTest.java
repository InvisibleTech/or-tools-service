package com.invisibletech.service;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.google.ortools.graph.MaxFlow;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MaxFlowServiceTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private MaxFlow mockMaxFlow;

  private MaxFlowService maxFlowService;

  @Before
  public void setUp() {
    this.maxFlowService = new MaxFlowService(this.mockMaxFlow);
  }

  @Test
  public void solve_whenOptimal_createsResponse() {
    when(this.mockMaxFlow.solve(eq(0), eq(1))).thenReturn(MaxFlow.Status.OPTIMAL);
    when(this.mockMaxFlow.getNumArcs()).thenReturn(1);
    when(this.mockMaxFlow.getOptimalFlow()).thenReturn(666L);
    when(this.mockMaxFlow.getTail(0)).thenReturn(0);
    when(this.mockMaxFlow.getHead(0)).thenReturn(1);
    when(this.mockMaxFlow.getFlow(0)).thenReturn(666L);
    when(this.mockMaxFlow.getCapacity(0)).thenReturn(666L);

    MaxFlowRequest request = ImmutableMaxFlowRequest.builder()
      .source("Here")
      .sink("There")
      .arcs(asList(ImmutableArc.builder()
        .tail("Here")
        .head("There")
        .capacity(666)
        .build()))
      .build();
    MaxFlowResponse actual = this.maxFlowService.solve(request);

    MaxFlowResponse expected = ImmutableMaxFlowResponse.builder()
      .status(MaxFlowResponse.Status.OPTIMAL)
      .optimalFlow(666)
      .arcs(asList(
        ImmutableSolutionArc.builder()
          .tail("Here")
          .head("There")
          .maxFlow(666)
          .capacity(666)
          .build()))
      .build();

    assertThat(actual, is(expected));
  }

  @Test
  public void solve_whenPossibleOverflow_createsResponse() {
    when(this.mockMaxFlow.solve(eq(0), eq(1))).thenReturn(MaxFlow.Status.POSSIBLE_OVERFLOW);

    MaxFlowRequest request = ImmutableMaxFlowRequest.builder()
      .source("Here")
      .sink("There")
      .arcs(asList(ImmutableArc.builder()
        .tail("Here")
        .head("There")
        .capacity(666)
        .build()))
      .build();
    MaxFlowResponse actual = this.maxFlowService.solve(request);

    MaxFlowResponse expected = ImmutableMaxFlowResponse.builder()
      .status(MaxFlowResponse.Status.POSSIBLE_OVERFLOW)
      .optimalFlow(0)
      .arcs(Collections.emptyList())
      .build();

    assertThat(actual, is(expected));
  }

  @Test
  public void solve_whenBadInput_createsResponse() {
    when(this.mockMaxFlow.solve(eq(0), eq(1))).thenReturn(MaxFlow.Status.BAD_INPUT);

    MaxFlowRequest request = ImmutableMaxFlowRequest.builder()
      .source("Here")
      .sink("There")
      .arcs(asList(ImmutableArc.builder()
        .tail("Here")
        .head("There")
        .capacity(666)
        .build()))
      .build();
    MaxFlowResponse actual = this.maxFlowService.solve(request);

    MaxFlowResponse expected = ImmutableMaxFlowResponse.builder()
      .status(MaxFlowResponse.Status.BAD_INPUT)
      .optimalFlow(0)
      .arcs(Collections.emptyList())
      .build();

    assertThat(actual, is(expected));
  }

  @Test
  public void solve_whenMissingArcs_raisesException() {
    MaxFlowRequest request = ImmutableMaxFlowRequest.builder()
      .source("Here")
      .sink("There")
      .arcs(Collections.emptyList())
      .build();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("No arcs were provided for the problem");

    this.maxFlowService.solve(request);
  }

  @Test
  public void solve_whenBadResult_createsResponse() {
    when(this.mockMaxFlow.solve(eq(0), eq(1))).thenReturn(MaxFlow.Status.BAD_RESULT);

    MaxFlowRequest request = ImmutableMaxFlowRequest.builder()
      .source("Here")
      .sink("There")
      .arcs(asList(ImmutableArc.builder()
        .tail("Here")
        .head("There")
        .capacity(666)
        .build()))
      .build();
    MaxFlowResponse actual = this.maxFlowService.solve(request);

    MaxFlowResponse expected = ImmutableMaxFlowResponse.builder()
      .status(MaxFlowResponse.Status.BAD_RESULT)
      .optimalFlow(0)
      .arcs(Collections.emptyList())
      .build();

    assertThat(actual, is(expected));
  }
}
