package com.craft.complaint.external.data.externaldata;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import java.util.UUID;
import lombok.*;

@EqualsAndHashCode(callSuper= false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData implements AdditionalData{

    private UUID id;
    private String fullName;
    private String emailAddress;
    private String physicalAddress;

    @Override
    public DataType getDataType(){
        return DataType.USER;
    }

}
