package com.talentEdge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class University {

    private String name;
    private String country;

    @JsonProperty("web_pages")
    private String[] webPages;

    @JsonProperty("domains")
    private String[] domains;

    @JsonProperty("state-province")
    private String stateProvince;

    @JsonProperty("alpha_two_code")
    private String alphaTwoCode;
}
