package org.dominik.server.data;

import java.util.UUID;

public final class Item {
  private UUID id;
  private UUID owner;
  private String name;

  public Item() {}

  public Item(UUID id, UUID owner, String name) {
    this.id = id;
    this.owner = owner;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOwner() {
    return owner;
  }

  public void setOwner(UUID owner) {
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Item{" +
      "id=" + id +
      ", owner=" + owner +
      ", name='" + name + '\'' +
      '}';
  }
}
