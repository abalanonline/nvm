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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class FileNvm implements Crud {
  private final Path path;

  public FileNvm(Path path) {
    this.path = path;
  }

  @Override
  public byte[] get(String pk, String sk) {
    try {
      //validateKey(pk); // TODO: 2026-02-22 move the boilerplate validation to Synchronized class
      Path pkPath = path.resolve(pk);
      if (!Files.isDirectory(pkPath)) return null;
      if (sk != null) return Files.exists(pkPath.resolve(sk)) ? Files.readAllBytes(pkPath.resolve(sk)) : null;
      byte[] bytes = Files.list(pkPath).map(p -> pkPath.relativize(p).toString() + '\0').sorted()
          .collect(Collectors.joining()).getBytes();
      return bytes.length == 0 ? null : bytes;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean put(String pk, String sk, byte[] bytes) {
    try {
      Path pkPath = path.resolve(pk);
      Files.createDirectories(pkPath);
      Path skPath = pkPath.resolve(sk);
      boolean exists = Files.exists(skPath);
      Files.write(skPath, bytes);
      return exists;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public boolean delete(String pk, String sk) {
    try {
      Path pkPath = path.resolve(pk);
      if (!Files.isDirectory(pkPath)) return false;
      boolean result = Files.deleteIfExists(pkPath.resolve(sk));
      if (result) try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pkPath)) {
        if (!directoryStream.iterator().hasNext()) Files.delete(pkPath);
      }
      return result;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public FileNvm open() {
    if (!Files.isDirectory(path)) throw new IllegalArgumentException(path.toString());
    return this;
  }

  @Override
  public void close() {

  }
}
