package org.example.model;

import java.io.Serializable;
import java.util.Objects;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

public class DataKey {
  public long id;

  @AffinityKeyMapped
  public final String affinityKey;

  public DataKey(long id, String affinityKey) {
    this.id = id;
    this.affinityKey = affinityKey;
  }

  @Override
  public String toString() {
    return "DataKey{" + "id=" + id + ", affinityKey='" + affinityKey + '\''
      + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    DataKey fooKey = (DataKey)o;

    if (id != fooKey.id)
      return false;
    return affinityKey.equals(fooKey.affinityKey);
  }

  @Override
  public int hashCode() {
    int result = Long.hashCode(id);
    result = 31 * result + affinityKey.hashCode();
    return result;
  }
}
