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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SynchronizedTest {

  @Test
  void test() throws IOException {
    Path path = Paths.get("target/" + UUID.randomUUID());
    Files.createDirectories(path);
    try {
      Crud nvm = new SynchronizedNvm(new FileNvm(path)).open();
      assertThrows(RuntimeException.class, () -> nvm.get("/", "\\"));
      assertThrows(RuntimeException.class, nvm::open);
      nvm.close();
      CrudTest.test(nvm);
    } finally {
      try { Files.delete(path); } catch (IOException ignore) {}
    }
  }

}
