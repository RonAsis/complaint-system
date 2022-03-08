package com.craft.complaint.external.data.externaldata;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import java.util.UUID;
import lombok.*;
import java.util.Date;

@EqualsAndHashCode(callSuper= false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseData implements AdditionalData{

    private UUID id;
    private UUID userId;
    private UUID productId;
    private String productName;
    private double pricePaidAmount;
    private String priceCurrency;
    private float discountPercent;
    private UUID merchantId;
    private Date purchaseDate;

    @Override
    public DataType getDataType(){
        return DataType.PURCHASE;
    }

}
