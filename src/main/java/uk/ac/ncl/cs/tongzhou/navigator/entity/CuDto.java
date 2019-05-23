package uk.ac.ncl.cs.tongzhou.navigator.entity;

import java.util.List;

public class CuDto {
    private String path;
    private List<TypeDto> typeList;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<TypeDto> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<TypeDto> typeList) {
        this.typeList = typeList;
    }
}
