package com.labelai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslateResponse {

    @JsonProperty("source_language")
    private String sourceLanguage;

    @JsonProperty("target_country")
    private String targetCountry;

    @JsonProperty("data")
    private TranslatedData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslatedData {
        @JsonProperty("product_info")
        private ProductInfo productInfo;

        @JsonProperty("nutrition_facts")
        private NutritionFacts nutritionFacts;

        private Ingredients ingredients;
        private Manufacturer manufacturer;
        private Warnings warnings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        @JsonProperty("product_type")
        private String productType;
        private String brand;
        @JsonProperty("best_before")
        private String bestBefore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredients {
        private List<String> list;
        @JsonProperty("allergen_info")
        private String allergenInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Manufacturer {
        private String name;
        private List<FacilityInfo> facilities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacilityInfo {
        @JsonProperty("facility_code")
        private String code;
        private String address;
        @JsonProperty("product_report_number")
        private String productReportNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Warnings {
        private String storage;
        private List<String> cautions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionFacts {
        @JsonProperty("serving_size")
        private ServingSizeInfo servingSize;

        private String calories;

        @JsonProperty("total_fat")
        private NutrientValue totalFat;
        @JsonProperty("saturated_fat")
        private NutrientValue saturatedFat;
        @JsonProperty("trans_fat")
        private NutrientValue transFat;
        private NutrientValue cholesterol;
        private NutrientValue sodium;
        @JsonProperty("total_carbohydrate")
        private NutrientValue totalCarbohydrate;
        @JsonProperty("dietary_fiber")
        private NutrientValue dietaryFiber;
        @JsonProperty("total_sugars")
        private NutrientValue totalSugars;
        @JsonProperty("added_sugars")
        private NutrientValue addedSugars;
        private NutrientValue protein;
        @JsonProperty("vitamin_d")
        private NutrientValue vitaminD;
        private NutrientValue calcium;
        private NutrientValue iron;
        private NutrientValue potassium;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServingSizeInfo {
        private String amount;
        @JsonProperty("servings_per_container")
        private String servingsPerContainer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutrientValue {
        private String amount;
        @JsonProperty("daily_value")
        private String dailyValue;
    }
}