/*
 *   Copyright (C) 2015 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.marvelapiclient.model;

import com.google.gson.annotations.SerializedName;

public class MarvelResponse<T> {
  @SerializedName("code") private int code;
  @SerializedName("status") private String status;
  @SerializedName("copyright") private String copyright;
  @SerializedName("attributionText") private String attributionText;
  @SerializedName("attributionHTML") private String getAttributionHtml;

  @SerializedName("data") private T response;

  public int getCode() {
    return code;
  }

  public String getStatus() {
    return status;
  }

  public String getCopyright() {
    return copyright;
  }

  public String getAttributionText() {
    return attributionText;
  }

  public String getGetAttributionHtml() {
    return getAttributionHtml;
  }

  public T getResponse() {
    return response;
  }

  @Override public String toString() {
    return "MarvelResponse{"
           + "code="
           + code
           + ", status='"
           + status
           + '\''
           + ", copyright='"
           + copyright
           + '\''
           + ", attributionText='"
           + attributionText
           + '\''
           + ", getAttributionHtml='"
           + getAttributionHtml
           + '\''
           + ", response="
           + response
           +
           '}';
  }
}
