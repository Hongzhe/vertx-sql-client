package io.vertx.mysqlclient.data.spatial;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A Polygon is a planar Surface representing a multisided geometry. It is defined by a single exterior boundary and zero or more interior boundaries, where each interior boundary defines a hole in the Polygon.
 */
@DataObject(generateConverter = true)
public class Polygon extends Geometry {
  private List<LineString> lineStrings;

  public Polygon() {
  }

  public Polygon(JsonObject json) {
    super(json);
    PolygonConverter.fromJson(json, this);
  }

  public Polygon(Polygon other) {
    super(other);
    this.lineStrings = new ArrayList<>(other.lineStrings);
  }

  public Polygon(long SRID, List<LineString> lineStrings) {
    super(SRID);
    this.lineStrings = lineStrings;
  }

  public Polygon setLineStrings(List<LineString> lineStrings) {
    this.lineStrings = lineStrings;
    return this;
  }

  public List<LineString> getLineStrings() {
    return lineStrings;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = super.toJson();
    PolygonConverter.toJson(this, json);
    return json;
  }
}
