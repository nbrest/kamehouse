package com.nicobrest.kamehouse.commons.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Marker annotation to mask fields that should be hidden with **** in toString() using JsonUtils,
 * so they are not printed into the logs. This annotation doesn't mask the fields on API responses,
 * only in toString() calls.
 *
 * @author nbrest
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Masked {

  /**
   * Utility class to get the list of fields annotated with Masked for any object.
   */
  public static final class MaskedUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaskedUtils.class);
    private static final String KAMEHOUSE_PACKAGE = "com.nicobrest.kamehouse";

    /**
     * Utils class.
     */
    private MaskedUtils() {

    }

    /**
     * Get all the masked fields for an object annotated with @Masked annotation.
     */
    public static String[] getMaskedFields(Object object) {
      List<String> maskedFields = new ArrayList<>();
      populateMaskedFieldsList(object, maskedFields, null);
      return maskedFields.toArray(new String[0]);
    }

    /**
     * Populate the list of masked fields on the object and it's sub objects annotated with the
     * Masked annotation. It doesn't handle masking fields in Lists or Maps.
     */
    private static void populateMaskedFieldsList(Object object, List<String> maskedFields,
        String parentNode) {
      parentNode = initParentNode(parentNode);
      if (!hasFields(object)) {
        return;
      }
      Field[] fields = object.getClass().getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Masked.class)) {
          if (field.isAnnotationPresent(JsonProperty.class)) {
            String fieldName = field.getAnnotation(JsonProperty.class).value();
            maskedFields.add(parentNode + fieldName);
          } else {
            maskedFields.add(parentNode + field.getName());
          }
        }
        if (shouldSkip(field)) {
          continue;
        }
        if (hasSubFields(field)) {
          try {
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            populateMaskedFieldsList(fieldValue, maskedFields, parentNode + field.getName());
          } catch (IllegalAccessException e) {
            LOGGER.trace("Error accessing object field to get masked fields. Field: {}",
                field.getName());
          }
        }
      }
    }

    /**
     * Init the parent node.
     */
    private static String initParentNode(String parentNode) {
      if (parentNode != null) {
        return parentNode + ".";
      } else {
        return "";
      }
    }

    /**
     * Check if the object has fields.
     */
    private static boolean hasFields(Object object) {
      if (object == null || object.getClass() == null) {
        return false;
      }
      Class<?> clazz = object.getClass();
      Field[] fields = clazz.getDeclaredFields();
      if (fields == null || fields.length <= 0) {
        return false;
      }
      return true;
    }

    /**
     * Check if the field has subfields.
     */
    private static boolean hasSubFields(Field field) {
      Field[] subfields = field.getType().getDeclaredFields();
      if (subfields != null && subfields.length > 0) {
        return true;
      }
      return false;
    }

    /**
     * Check if it should skip the field from processing.
     */
    private static boolean shouldSkip(Field field) {
      if (field.isEnumConstant()) {
        return true;
      }
      Class<?> fieldClass = field.getType();
      if (fieldClass == null || fieldClass.getPackage() == null) {
        return true;
      }
      String packageName = fieldClass.getPackage().getName();
      if (!packageName.startsWith(KAMEHOUSE_PACKAGE)) {
        // Only iterate recursively over fields that are KameHouse defined objects.
        return true;
      }
      return false;
    }
  }
}
