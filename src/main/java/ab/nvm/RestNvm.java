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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * https://www.rfc-editor.org/rfc/rfc9110
 */
public class RestNvm implements Crud {

  final String url;

  public RestNvm(String url) {
    if (!(url.startsWith("http://") || url.startsWith("https://")) || !url.endsWith("/"))
      throw new IllegalArgumentException(url);
    this.url = url;
  }

  @Override
  public byte[] get(String pk, String sk) {
    try {
      URLConnection connection = new URL(url + pk + "/" + (sk == null ? "" : sk)).openConnection();
      try (InputStream inputStream = connection.getInputStream()) {
        return inputStream.readAllBytes();
      }
    } catch (FileNotFoundException e) {
      return null; // not an error
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean put(String pk, String sk, byte[] bytes) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url + pk + "/" + sk).openConnection();
      connection.setRequestMethod("PUT");
      connection.setDoOutput(true);
      try (OutputStream outputStream = connection.getOutputStream()) {
        outputStream.write(bytes);
      }
      connection.connect();
      int responseCode = connection.getResponseCode();
      switch (responseCode) {
        case 200: case 204: return true;
        case 201: return false;
        default: throw new IllegalStateException();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean delete(String pk, String sk) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url + pk + "/" + sk).openConnection();
      connection.setRequestMethod("DELETE");
      connection.connect();
      int responseCode = connection.getResponseCode();
      switch (responseCode) {
        case 200: case 204: return true;
        case 404: return false;
        default: throw new IllegalStateException();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public RestNvm open() {
    return this;
  }

  @Override
  public void close() {}

}
