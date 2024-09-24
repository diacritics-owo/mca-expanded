package diacritics.owo.util;

import fabric.net.mca.entity.VillagerLike;

public record CosmeticValues(Holder holder) {

  public CosmeticValues() {
    this(new Holder());
  }

  public void apply(VillagerLike<?> entity) {
    entity.setClothes(this.holder.clothes);
    entity.setHair(this.holder.hair);
    entity.setHairDye(this.holder.hairDye[0], this.holder.hairDye[1], this.holder.hairDye[2]);
  }

  public void update(VillagerLike<?> entity) {
    this.holder.clothes = entity.getClothes();
    this.holder.hair = entity.getHair();
    this.holder.hairDye = entity.getHairDye();
  }

  private static class Holder {
    private String clothes = "";
    private String hair = "";
    private float[] hairDye = new float[] {};
  }
}
