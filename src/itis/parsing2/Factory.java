package itis.parsing2;

import itis.parsing2.annotations.Concatenate;
import itis.parsing2.annotations.NotBlank;

import java.util.List;

//ЭТОТ КЛАСС МЕЯТЬ НЕЛЬЗЯ
public final class Factory {

    @NotBlank
    private String title;

    @Concatenate(delimiter = " ", fieldNames = {"firstName","secondName", "middleName"})
    private String organizationChiefFullName;

    private String description;

    private Long amountOfWorkers;

    private List<String> departments;


}
