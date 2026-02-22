/*
 * Copyright (C) 2026 Aleksei Balan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ab.nvm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

public class RestServer implements AutoCloseable {

  final Crud nvm;
  final int port;
  HttpServer server;

  public RestServer(Crud nvm, int port) {
    this.nvm = nvm;
    this.port = port;
  }

  public RestServer open() {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    server.createContext("/", this::handle500);
    server.start();
    return this;
  }

  @Override
  public void close() {
    server.stop(1);
  }

  void handle500(HttpExchange exchange) throws IOException {
    try {
      handle(exchange);
    } catch (Exception e) {
      exchange.sendResponseHeaders(500, 0);
      exchange.getResponseBody().close();
    }
  }

  void handle(HttpExchange exchange) throws IOException {
    String[] pksk = exchange.getRequestURI().toString().split("/", 3);
    String pk = pksk[1];
    String sk = pksk.length < 3 || pksk[2].isEmpty() ? null : pksk[2];
    int code;
    switch (exchange.getRequestMethod()) {
      case "GET": get(pk, sk, exchange); return;
      case "PUT": code = put(pk, sk, exchange); break;
      case "DELETE": code = delete(pk, sk); break;
      default: throw new IllegalStateException();
    }
    exchange.sendResponseHeaders(code, 0);
    exchange.getResponseBody().close();
  }

  void get(String pk, String sk, HttpExchange exchange) throws IOException {
    // FIXME: 2026-02-22 disable GET cache by Cache-Control header
    byte[] bytes = nvm.get(pk, sk);
    exchange.sendResponseHeaders(bytes == null ? 404 : 200, bytes == null ? 0 : bytes.length);
    try (OutputStream stream = exchange.getResponseBody()) {
      if (bytes != null) stream.write(bytes);
    }
  }

  int put(String pk, String sk, HttpExchange exchange) throws IOException {
    byte[] bytes;
    try (InputStream stream = exchange.getRequestBody()) {
      bytes = stream.readAllBytes();
    }
    return nvm.put(pk, sk, bytes) ? 200 : 201;
  }

  int delete(String pk, String sk) {
    return nvm.delete(pk, sk) ? 200 : 404;
  }

}
