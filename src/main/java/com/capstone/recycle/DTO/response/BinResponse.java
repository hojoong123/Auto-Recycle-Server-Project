package com.capstone.recycle.dto.response;

import com.capstone.recycle.entity.Bin;
import com.capstone.recycle.entity.BinStatus;
import lombok.Getter;

@Getter
public class BinResponse {
    private Long id;
    private String binCode;
    private String binName;
    private String trashTypeCode;
    private String trashTypeName;
    private Integer fillPercent;
    private Boolean isFull;
    private Boolean errorFlag;

    public BinResponse(Bin bin, BinStatus status) {
        this.id = bin.getId();
        this.binCode = bin.getBinCode();
        this.binName = bin.getBinName();
        this.trashTypeCode = bin.getTrashType().getTypeCode();
        this.trashTypeName = bin.getTrashType().getTypeName();
        this.fillPercent = status != null ? status.getFillPercent() : 0;
        this.isFull = status != null ? status.getIsFull() : false;
        this.errorFlag = status != null ? status.getErrorFlag() : false;
    }
}
