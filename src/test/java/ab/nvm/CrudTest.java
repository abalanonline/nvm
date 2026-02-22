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

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrudTest {

  static void test(Crud crud) {
    crud.open();
    String pk = UUID.randomUUID().toString();
    String sk = UUID.randomUUID().toString();
    String sk2 = UUID.randomUUID().toString();
    byte[] content = UUID.randomUUID().toString().getBytes();
    assertNull(crud.get(pk, null));
    assertNull(crud.get(pk, sk));

    assertFalse(crud.put(pk, "1", content));
    assertTrue(crud.put(pk, "1", content));
    crud.put(pk, "2", content);
    crud.put(pk, "11", content);
    assertArrayEquals(("1\0" + "11\0" + "2\0").getBytes(), crud.get(pk, null));
    assertTrue(crud.delete(pk, "1"));
    assertFalse(crud.delete(pk, "1"));
    crud.delete(pk, "2");
    crud.delete(pk, "11");
    assertFalse(crud.delete(pk, "11"));

    crud.put(pk, sk, sk.getBytes());
    crud.put(pk, sk, content);
    crud.put(pk, sk2, sk2.getBytes());
    byte[] list = (Arrays.stream(new String[]{sk, sk2}).sorted().collect(Collectors.joining("\0")) + "\0").getBytes();
    assertArrayEquals(list, crud.get(pk, null));
    assertArrayEquals(content, crud.get(pk, sk));

    assertTrue(crud.delete(pk, sk));
    assertFalse(crud.delete(pk, sk));
    assertArrayEquals(Arrays.copyOf(sk2.getBytes(), 37), crud.get(pk, null));
    assertNull(crud.get(pk, sk));

    crud.close();
    crud.open();
    crud.delete(pk, sk2);
    assertNull(crud.get(pk, null));
    crud.close();
  }

}
