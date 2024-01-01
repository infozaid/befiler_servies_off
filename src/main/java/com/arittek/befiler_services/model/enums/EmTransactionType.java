package com.arittek.befiler_services.model.enums;

public enum EmTransactionType {
    INCOME(1),
    EXPENSE(2);

    private final Integer id;

    private EmTransactionType(Integer id) { this.id = id; }

    public static EmTransactionType getEmDocumentType(Integer id) {
	if (id == null) {
	    return null;
	}

	for (EmTransactionType emTransactionType : EmTransactionType.values()) {
	    if (id == emTransactionType.getId()) {
		return emTransactionType;
	    }
	}

	return null;
    }

    public Integer getId() {
	return id;
    }
}
