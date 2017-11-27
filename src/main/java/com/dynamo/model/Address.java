package com.dynamo.model;

import lombok.*;

/**
 * Created by Niharp on 11/26/2017.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    private String line1;
    private String city;
    private String state;
    private Long zipCode;
    private String country;
}
