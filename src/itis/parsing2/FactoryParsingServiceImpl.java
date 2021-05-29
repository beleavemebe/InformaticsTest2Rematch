package itis.parsing2;

import itis.parsing2.annotations.Concatenate;
import itis.parsing2.annotations.NotBlank;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FactoryParsingServiceImpl implements FactoryParsingService {

    @Override
    public Factory parseFactoryData(String factoryDataDirectoryPath) throws FactoryParsingException {
        try {
            Constructor<Factory> declaredConstructor = Factory.class.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Factory park = declaredConstructor.newInstance();
            initFields(park.getClass().getDeclaredFields(), park, new File(factoryDataDirectoryPath));
            return park;
        } catch (ReflectiveOperationException e) {
            System.err.println("My bad");
            return null;
        }
    }

    private void initFields(Field[] fields, Factory factory, File dataDir) {
        List<FactoryParsingException.FactoryValidationError> errors = new ArrayList<>();
        Arrays.stream(fields).forEach(field -> {
            String value = "";
            Concatenate annoConcatenate = field.getDeclaredAnnotation(Concatenate.class);
            if (annoConcatenate != null) {
                StringBuilder valueBuilder = new StringBuilder();
                for (String s : annoConcatenate.fieldNames()) {
                    valueBuilder.append(findFieldValueInDirectory(s, dataDir));
                    valueBuilder.append(annoConcatenate.delimiter());
                }
                value = valueBuilder.toString().trim();
            } else {
                value = findFieldValueInDirectory(field.getName(), dataDir);
            }
            if (value.equals("") | value.equals("null")) {
                value = null;
                NotBlank annoNotBlank = field.getDeclaredAnnotation(NotBlank.class);
                if (annoNotBlank != null) {
                    errors.add(new FactoryParsingException.FactoryValidationError(field.getName(), "Поле не должно быть пустой строкой или null"));
                }
            } else if (value.contains("not found")) {
                value = null;
                errors.add(new FactoryParsingException.FactoryValidationError(field.getName(), "Не найдено значение для поля"));
            }

            field.setAccessible(true);
            setValue(factory, field, value);
        });
        if (errors.size() != 0) {
            throw new FactoryParsingException("Произошла ошибка", errors);
        }
    }

    private void setValue(Factory factory, Field field, String value) {
        try {
            if (value == null) {
                field.set(factory, null);
                return;
            }
            if (field.getType() == String.class) {
                field.set(factory, value);
            } else if (field.getType() == Long.class) {
                field.set(factory, Long.parseLong(value));
            } else if (field.getType() == List.class) {
                value = disposeOfCharAndTrim(value, '[');
                value = disposeOfCharAndTrim(value, ']');
                value = disposeOfCharAndTrim(value, ',');
                String[] valuesArr = value.split(" ");
                List<String> values = Arrays.asList(valuesArr);
                field.set(factory, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String findFieldValueInDirectory(String fieldName, File dir) {
        if (!dir.isDirectory()) {
            System.err.println("Not a directory!");
            return null;
        }
        File[] files = dir.listFiles();
        String[] foundValues = new String[files.length];
        for (int i = 0; i < foundValues.length; i++) {
            foundValues[i] = findValueForField(fieldName, files[i]);
            if (!foundValues[i].equals("not found")) return foundValues[i];
        }
        return "not found";
    }

    private String findValueForField(String fieldName, File dataFile) {
        try {
            Scanner scanner = new Scanner(dataFile);
            scanner.useDelimiter("\n");
            String fieldValue = scanner.tokens().filter(line -> {
                if (line.equals("---")) return false;
                String str = line.split(":")[0];
                str = disposeOfCharAndTrim(str, '\"');
                return str.equals(fieldName);
            }).toArray(String[]::new)[0].split(":")[1].trim();
            if (fieldValue.contains("\"")) {
                return disposeOfCharAndTrim(fieldValue, '\"');
            }
            return fieldValue;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "not found";
        }
        return "";
    }

    private String disposeOfCharAndTrim(String str, Character characterToDelete) {
        if (!str.contains(characterToDelete.toString())) return str;
        return str.trim().chars()
                .filter(value -> !characterToDelete.equals((char) value))
                .mapToObj(operand -> Character.valueOf((char) operand).toString())
                .collect(Collectors.joining())
                .trim();
    }

}
