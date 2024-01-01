package com.arittek.befiler_services.model.enums;

public enum ProductType {
    TAXFORM(1),
    NTN(2);

    private final Integer id;

    ProductType(Integer id) { this.id = id; }

    public static ProductType getProductType(Integer id) {
        if (id == null) {
            return null;
        }

        for (ProductType productType : ProductType.values()) {
            if (id == productType.getId()) {
                return productType;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }

}
