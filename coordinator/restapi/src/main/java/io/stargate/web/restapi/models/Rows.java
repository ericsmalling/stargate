/*
 * Copyright The Stargate Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.stargate.web.restapi.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rows {

  private final int count;
  private final String pageState;
  private final List<Map<String, Object>> rows;

  @ApiModelProperty(value = "Number of records being returned by the request.")
  public int getCount() {
    return count;
  }

  @ApiModelProperty(value = "The rows returned by the request.")
  public List<Map<String, Object>> getRows() {
    return rows;
  }

  @ApiModelProperty(
      value = "A string representing the paging state to be used on future paging requests.")
  public String getPageState() {
    return pageState;
  }

  @JsonCreator
  public Rows(
      @JsonProperty("count") int count,
      @JsonProperty("pageState") String pageState,
      @JsonProperty("rows") List<Map<String, Object>> rows) {
    this.count = count;
    this.pageState = pageState;
    this.rows = rows;
  }
}
