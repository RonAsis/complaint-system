package com.craft.complaint.external.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.craft.complaint.external.data.externaldata.PurchaseData;
import com.craft.complaint.external.data.externaldata.UserData;

@JsonTypeInfo(
        use = Id.NAME,
        include = As.WRAPPER_OBJECT
)
@JsonSubTypes({@Type(
        name = "purchaseData",
        value = PurchaseData.class
), @Type(
        name = "userData",
        value = UserData.class
)})
public interface AdditionalData {

    DataType getDataType();

}
