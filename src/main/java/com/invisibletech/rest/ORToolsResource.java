package com.invisibletech.rest;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import spark.Request;
import spark.Response;

import com.invisibletech.service.MaxFlowService;
import com.invisibletech.service.MaxFlowRequest;

public class ORToolsResource {
  private static Gson GSON = new Gson();

  public static void main(final String[] args) {
    port(8080);

    exception(JsonParseException.class, (exception, request, response) -> {
      response.status(400);
      response.body(GSON.toJson(
        Arrays.asList("Invalid request syntax.", exception.getCause().getMessage())));
    });

    post("/max_flow", (request, response) -> {
      response.type("application/json");

      return MaxFlowService.solve(maxFlowRequest(request));}, GSON::toJson);
  }

  static MaxFlowRequest maxFlowRequest(Request request) {
    return GSON.fromJson(request.body(), MaxFlowRequest.class);
  }
}
