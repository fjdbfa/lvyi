package org.example.pojo.dto;

import lombok.Data;

@Data
public class GenericDto <T>{
    T id;
}
