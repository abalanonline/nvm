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

public interface Crud extends AutoCloseable {
  /**
   * TODO: 2026-02-06 document exists() use case
   * @param pk partition key, [A-Za-z0-9-_] not null
   * @param sk sort key, nullable
   * @return concatenated asciiz sort keys if sk is null, bytes or null if pk-sk does not exist
   */
  byte[] get(String pk, String sk);
  /**
   * @return true if the resource was updated, false if created
   */
  boolean put(String pk, String sk, byte[] bytes);
  /**
   * @return true if the resource was deleted
   */
  boolean delete(String pk, String sk);
  default void validateKey(String key) {
    if (key == null || key.isEmpty()) throw new IllegalArgumentException(key);
    for (byte b : key.getBytes())
      if (!(b >= 'A' && b <= 'Z' || b >= 'a' && b <= 'z' || b >= '0' && b <= '9' || b == '-' || b == '_'))
        throw new IllegalArgumentException(key);
  }
  /**
   * Must support opening after closing.
   */
  Crud open();
  @Override
  void close();
}
