package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

public class GavCu {
    public String group;
    public String artifact;
    public String version;
    public String cuName;

    public GavCu() {
    }

    public GavCu(String groupId, String artifactId, String version, String compilationUnit) {
        this.group = groupId;
        this.artifact = artifactId;
        this.version = version;
        this.cuName = compilationUnit;
    }
}
