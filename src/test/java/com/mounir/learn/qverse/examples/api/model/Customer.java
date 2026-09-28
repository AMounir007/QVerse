package com.mounir.learn.qverse.examples.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mounir.learn.qverse.utils.RandomData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Business payload - Lombok removes boilerplate, Jackson (de)serialises it. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private Integer id;
    private String name;
    private String username;
    private String email;

    public static Customer random() {
        String id = RandomData.uniqueId();
        return Customer.builder().name(RandomData.name()).username("user_" + id).email(RandomData.uniqueEmail()).build();
    }
}
