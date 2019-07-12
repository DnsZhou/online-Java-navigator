package uk.ac.ncl.cs.tongzhou.navigator.jsonmodel;

public class LinkObject {
    public String group;
    public String artifact;
    public String version;
    public String cuName;
    public String navFrom;
    public String navTo;


    public LinkObject() {
    }

    public LinkObject(String groupId, String artifactId, String version, String compilationUnit, String navFrom, String navTo) {
        this.group = groupId;
        this.artifact = artifactId;
        this.version = version;
        this.cuName = compilationUnit;
        this.navFrom = navFrom;
        this.navTo = navTo;
    }

    public LinkObject(String navFrom, String navTo) {
        this.navFrom = navFrom;
        this.navTo = navTo;
    }

    public LinkObject(String navTo) {
        this.navTo = navTo;
    }
}
