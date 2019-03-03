package com.invisibletech.rest;

import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.ortools.graph.MaxFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import com.invisibletech.service.GsonAdaptersMaxFlowRequest;
import com.invisibletech.service.GsonAdaptersMaxFlowResponse;
import com.invisibletech.service.MaxFlowRequest;
import com.invisibletech.service.MaxFlowResponse;
import com.invisibletech.service.MaxFlowService;

public class ORToolsResource {
  private static Gson GSON;

  private static Logger LOGGER = LoggerFactory.getLogger(ORToolsResource.class);

  static {
    try {
      LOGGER.info("Loading ortools library.");

      System.loadLibrary("jniortools");

      LOGGER.info("Creating GSON.");

      GsonBuilder gsonBuilder = new GsonBuilder();
      for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
        gsonBuilder.registerTypeAdapterFactory(factory);
      }

      GSON = gsonBuilder.create();
    } catch (Throwable t) {
      LOGGER.error("Unable to load the resource class.", t);
      throw t;
    }
  }

  public static void main(final String[] args) {
    port(8080);

    exception(JsonParseException.class, (exception, request, response) -> {
      response.status(400);
      response.body(GSON.toJson(
        Arrays.asList("Invalid request syntax.", exception.getCause().getMessage())));
    });

    exception(IllegalArgumentException.class, (exception, request, response) -> {
      response.status(400);
      response.body(GSON.toJson(
        Arrays.asList(exception.getMessage())));
    });

    exception(Exception.class, (exception, request, response) -> {
      response.status(500);
      response.body(GSON.toJson(
        Arrays.asList("Server failure.", exception.getCause().toString())));
    });

    post("/max_flow", (request, response) -> {
      response.type("application/json");

      MaxFlowRequest maxFlowRequest = maxFlowRequest(request);
      LOGGER.info("Max flow request: {}", maxFlowRequest);

      MaxFlowResponse maxFlowResponse = new MaxFlowService(new MaxFlow()).solve(maxFlowRequest);
      LOGGER.info("Max flow response: {}", maxFlowResponse);

      return maxFlowResponse;
   }, GSON::toJson);
  }

  static MaxFlowRequest maxFlowRequest(Request request) {
    return GSON.fromJson(request.body(), MaxFlowRequest.class);
  }
}
