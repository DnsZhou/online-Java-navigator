package uk.ac.ncl.cs.tongzhou.navigator.entity;

import java.util.List;

public class GauDto {
    private List<CuDto> cuList;
    private String path;

    public List<CuDto> getCuList() {
        return cuList;
    }

    public void setCuList(List<CuDto> cuList) {
        this.cuList = cuList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
