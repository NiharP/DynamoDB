package com.dynamo.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyInfo {
    private Address address;
    private String price;
    private String type;
    private String roomType;
}
