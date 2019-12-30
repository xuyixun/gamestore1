package com.ijianjian.core.domain.util;

public interface PageDTO {
Integer getPage();

Integer getSize();

void setPage(Integer page);

void setSize(Integer size);

default void init() {
 if (getPage() == null) {
  setPage(0);
 }
 if (getSize() == null) {
  setSize(20);
 }
}
}
