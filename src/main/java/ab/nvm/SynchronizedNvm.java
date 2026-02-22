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

public class SynchronizedNvm implements Crud {

  final Crud nvm;
  boolean open;

  public SynchronizedNvm(Crud nvm) {
    this.nvm = nvm;
  }

  @Override
  public byte[] get(String pk, String sk) {
    validateKey(pk);
    if (sk != null) validateKey(sk);
    return nvm.get(pk, sk);
  }

  @Override
  public boolean put(String pk, String sk, byte[] bytes) {
    validateKey(pk);
    validateKey(sk);
    return nvm.put(pk, sk, bytes);
  }

  @Override
  public boolean delete(String pk, String sk) {
    validateKey(pk);
    validateKey(sk);
    return nvm.delete(pk, sk);
  }

  @Override
  public Crud open() {
    if (open) throw new IllegalStateException();
    open = true;
    nvm.open();
    return this;
  }

  @Override
  public void close() {
    if (!open) return;
    open = false;
    nvm.close();
  }

}
