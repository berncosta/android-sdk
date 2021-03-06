package com.dreamfactory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.dreamfactory.model.RelatedApp;
import com.dreamfactory.model.Metadata;
public class RelatedApps {
  /* Array of system application records. */
  @JsonProperty("record")
  private List<RelatedApp> record = new ArrayList<RelatedApp>();
  /* Array of metadata returned for GET requests. */
  @JsonProperty("meta")
  private Metadata meta = null;
  public List<RelatedApp> getRecord() {
    return record;
  }
  public void setRecord(List<RelatedApp> record) {
    this.record = record;
  }

  public Metadata getMeta() {
    return meta;
  }
  public void setMeta(Metadata meta) {
    this.meta = meta;
  }

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class RelatedApps {\n");
    sb.append("  record: ").append(record).append("\n");
    sb.append("  meta: ").append(meta).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}

