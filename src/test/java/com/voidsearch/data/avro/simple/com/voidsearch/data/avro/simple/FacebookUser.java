package com.voidsearch.data.avro.simple;

@SuppressWarnings("all")
public class FacebookUser extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = org.apache.avro.Schema.parse("{\"type\":\"record\",\"name\":\"FacebookUser\",\"namespace\":\"com.voidsearch.data.avro.simple\",\"fields\":[{\"name\":\"name\",\"type\":[\"string\",\"null\"]},{\"name\":\"num_likes\",\"type\":\"int\"},{\"name\":\"num_photos\",\"type\":\"int\"},{\"name\":\"num_groups\",\"type\":\"int\"}]}");
  public org.apache.avro.util.Utf8 name;
  public int num_likes;
  public int num_photos;
  public int num_groups;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return name;
    case 1: return num_likes;
    case 2: return num_photos;
    case 3: return num_groups;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: name = (org.apache.avro.util.Utf8)value$; break;
    case 1: num_likes = (java.lang.Integer)value$; break;
    case 2: num_photos = (java.lang.Integer)value$; break;
    case 3: num_groups = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
}
